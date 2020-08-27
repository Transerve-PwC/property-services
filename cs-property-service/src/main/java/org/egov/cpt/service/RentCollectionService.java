package org.egov.cpt.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Iterables;

import org.egov.cpt.models.RentAccount;
import org.egov.cpt.models.RentAccountStatement;
import org.egov.cpt.models.RentAccountStatement.Type;
import org.egov.cpt.models.RentCollection;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentPayment;
import org.egov.cpt.models.RentSummary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class RentCollectionService implements IRentCollectionService {

	@Override
	public List<RentCollection> settle(final List<RentDemand> demandsToBeSettled, final List<RentPayment> payments,
			final RentAccount account, double interestRate) {

		/**
		 * Don't process payments that are already processed.
		 */
		List<RentPayment> paymentsToBeSettled = payments.stream().filter(payment -> !payment.isProceed())
				.collect(Collectors.toList());

		/**
		 * Settle unprocessed payments
		 */
		List<RentCollection> collections = paymentsToBeSettled.stream().map(payment -> {
			return settlePayment(demandsToBeSettled, payment, account, interestRate);
		}).flatMap(Collection::stream).collect(Collectors.toList());

		if (account.getRemainingAmount() == 0) {
			return collections;
		}

		/**
		 * We have positive account balance.
		 */
		List<RentDemand> leftOverDemands = demandsToBeSettled.stream()
				.filter(demand -> demand.getRemainingPrincipal() > 0).collect(Collectors.toList());
		if (leftOverDemands.size() == 0) {
			return collections;
		}
		ArrayList<RentCollection> result = new ArrayList<RentCollection>(collections);
		List<RentDemand> demandsAfterPayments;
		if (CollectionUtils.isEmpty(payments)) {
			demandsAfterPayments = leftOverDemands;
		} else {
			RentPayment lastPayment = Iterables.getLast(payments, null);
			demandsAfterPayments = leftOverDemands.stream()
					.filter(demand -> demand.getGenerationDate() > lastPayment.getDateOfPayment())
					.collect(Collectors.toList());
		}

		for (RentDemand demand : demandsAfterPayments) {
			RentPayment payment = RentPayment.builder().amountPaid(0D).dateOfPayment(demand.getInterestSince()).build();
			List<RentCollection> settledCollections = settlePayment(demandsToBeSettled, payment, account, interestRate);
			if (settledCollections.size() == 0) {
				continue;
			}
			result.addAll(settledCollections);
			if (account.getRemainingAmount() == 0) {
				break;
			}
		}
		return result;
	}

	private List<RentCollection> settlePayment(final List<RentDemand> demandsToBeSettled, final RentPayment payment,
			final RentAccount account, double interestRate) {
		/**
		 * Each payment will only operate on the demands generated before it is paid.
		 */
		List<RentDemand> demands = demandsToBeSettled.stream().filter(demand -> demand.getRemainingPrincipal() > 0
				&& demand.getGenerationDate() <= payment.getDateOfPayment()).collect(Collectors.toList());

		/**
		 * Effective amount to be settled = paidAmount + accountBalance
		 */
		double effectiveAmount = payment.getAmountPaid() + account.getRemainingAmount();

		/**
		 * Break down payment into a set of collections. Any pending interest is to be
		 * collected first.
		 */
		List<RentCollection> interestCollections = extractInterest(interestRate, payment.getDateOfPayment(), demands,
				effectiveAmount);
		effectiveAmount -= interestCollections.stream().mapToDouble(RentCollection::getInterestCollected).sum();

		/**
		 * Amount is left after deducting interest for all the demands. Extract
		 * Principal.
		 */

		List<RentCollection> principalCollections = effectiveAmount > 0 ? extractPrincipal(demands, effectiveAmount)
				: Collections.emptyList();
		effectiveAmount -= principalCollections.stream().mapToDouble(RentCollection::getPrincipalCollected).sum();

		/**
		 * Amount is left after deducting all the principal amounts. Put it back in the
		 * account
		 */
		account.setRemainingAmount(effectiveAmount);

		/**
		 * Mark payment as processed.
		 */
		payment.setProceed(true);
		return Stream.of(interestCollections, principalCollections).flatMap(x -> x.stream())
				.collect(Collectors.toList());
	}

	private List<RentCollection> extractPrincipal(List<RentDemand> demands, double paymentAmount) {
		ArrayList<RentCollection> collections = new ArrayList<RentCollection>();
		List<RentDemand> filteredDemands = demands.stream().filter(d -> d.getRemainingPrincipal() > 0)
				.collect(Collectors.toList());
		for (RentDemand demand : filteredDemands) {
			if (paymentAmount <= 0) {
				break;
			}
			double collectionAmount = Math.min(demand.getRemainingPrincipal(), paymentAmount);

			paymentAmount -= collectionAmount;
			collections.add(RentCollection.builder().tenantId(demand.getTenantId()).demandId(demand.getId())
					.principalCollected(collectionAmount).build());
			demand.setRemainingPrincipal(demand.getRemainingPrincipal() - collectionAmount);
		}
		return collections;
	}

	private List<RentCollection> extractInterest(double interestRate, long paymentTimeStamp, List<RentDemand> demands,
			double paymentAmount) {
		if (interestRate <= 0) {
			return Collections.emptyList();
		}

		ArrayList<RentCollection> collections = new ArrayList<RentCollection>(demands.size());
		for (RentDemand demand : demands) {
			if (paymentAmount <= 0) {
				break;
			}
			LocalDate demandGenerationDate = Instant.ofEpochMilli(demand.getGenerationDate())
					.atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate paymentDate = Instant.ofEpochMilli(paymentTimeStamp).atZone(ZoneId.systemDefault()).toLocalDate();

			long noOfDaysBetweenGenerationAndPayment = ChronoUnit.DAYS.between(demandGenerationDate, paymentDate);
			if (noOfDaysBetweenGenerationAndPayment <= demand.getInitialGracePeriod()) {
				continue;
			}

			LocalDate demandInterestSinceDate = Instant.ofEpochMilli(demand.getInterestSince())
					.atZone(ZoneId.systemDefault()).toLocalDate();

			long noOfDaysForInterestCalculation = ChronoUnit.DAYS.between(demandInterestSinceDate, paymentDate);

			if (noOfDaysForInterestCalculation == 0) {
				continue;
			}
			double interest = demand.getRemainingPrincipal() * noOfDaysForInterestCalculation * interestRate / 365
					/ 100;
			if (interest < paymentAmount) {
				collections.add(RentCollection.builder().tenantId(demand.getTenantId()).interestCollected(interest)
						.demandId(demand.getId()).build());
				demand.setInterestSince(paymentTimeStamp);
				paymentAmount -= interest;
			}
		}
		return collections;
	}

	/**
	 * Get the current rent summary by calculating from the given demands and
	 * collections for the same property.
	 * 
	 * @apiNote This is called every time we return a property in search.
	 * @apiNote This will not change the database in anyway.
	 * @param demands
	 * @param collections
	 * @param payment
	 * @return
	 */
	public RentSummary paymentSummary(List<RentDemand> demands, RentAccount rentAccount) {
		double balancePrincipal = 0;
		double balanceInterest = 0;
		final double interestRate = 24;
		RentSummary rentSummary = new RentSummary();

		for (RentDemand rentDemand : demands) {
			double interest = 0;
			float daysBetween = ((new Date().getTime() - rentDemand.getGenerationDate()) / (1000 * 60 * 60 * 24)) + 1;
			// If days cross the gross period then the interest is applicable

			if (daysBetween > rentDemand.getInitialGracePeriod()) {
				interest = ((rentDemand.getRemainingPrincipal() * interestRate) / 100) * (daysBetween / 365);
			}
			balanceInterest += interest;
			balancePrincipal += rentDemand.getRemainingPrincipal();
		}
		rentSummary.setBalanceAmount(rentAccount.getRemainingAmount());
		rentSummary.setBalanceInterest(balanceInterest);
		rentSummary.setBalancePrincipal(balancePrincipal);
		return rentSummary;
	}

	/**
	 * @apiNote This will provide the account statement between the date specified
	 *          by the user.
	 * @param demands
	 * @param payments
	 * @param lstCollection
	 * @return List<RentAccountStatement>
	 */
	@Override
	public List<RentAccountStatement> accountStatement(List<RentDemand> demands, List<RentPayment> payments,
			List<RentCollection> lstCollection) {

		final double interestRate = 24;
		// ArrayList lstProcessRentDemand;
		ArrayList<RentAccountStatement> lstAccountStatement = new ArrayList<RentAccountStatement>();
		double remainingPrincipal = 0.0;
		double remainingInterest = 0.0;
		double outstandingBalance = 0.0;
		for (RentPayment rentPayment : payments) {

			// lstProcessRentDemand=new ArrayList<RentDemand>();
			Date paymentDate = new Date(rentPayment.getDateOfPayment());

			double collectedAmount = 0.0;
			ArrayList<RentDemand> lstDemandTobeProcess = new ArrayList<RentDemand>();

			for (RentDemand rentDemand : demands) {

				Date demandDate = new Date(rentDemand.getGenerationDate());

				// filter out the demands which have earlier date than payment
				if (demandDate.compareTo(paymentDate) <= 0) {
					lstDemandTobeProcess.add(rentDemand);
					RentAccountStatement rentAccountStatement = new RentAccountStatement();
					rentAccountStatement.setDate(rentDemand.getGenerationDate());
					rentAccountStatement.setAmount(rentDemand.getCollectionPrincipal());
					rentAccountStatement.setType(Type.fromValue("D"));
					rentAccountStatement
							.setRemainingPrincipal(remainingPrincipal + rentDemand.getCollectionPrincipal());

					remainingPrincipal = remainingPrincipal + rentDemand.getCollectionPrincipal();
					float daysBetween = ((rentPayment.getDateOfPayment() - rentDemand.getGenerationDate())
							/ (1000 * 60 * 60 * 24)) + 1;
					// If days cross the gross period then the interest is applicable
					if (daysBetween >= rentDemand.getInitialGracePeriod()) {
						double interest = ((rentDemand.getCollectionPrincipal() * interestRate) / 100)
								* (daysBetween / 365);
						remainingInterest = remainingInterest + interest;
					}
					rentAccountStatement.setRemainingInterest(remainingInterest);
					rentAccountStatement.setDueAmount(
							rentAccountStatement.getRemainingInterest() + rentAccountStatement.getRemainingPrincipal());
					lstAccountStatement.add(rentAccountStatement);

				}

			}
			RentAccountStatement rentAccountStatement = new RentAccountStatement();
			rentAccountStatement.setDate(rentPayment.getDateOfPayment());
			rentAccountStatement.setAmount(rentPayment.getAmountPaid());
			rentAccountStatement.setType(Type.fromValue("C"));
			rentAccountStatement.setRemainingPrincipal(0.0);
			rentAccountStatement.setRemainingInterest(0.0);
			rentAccountStatement.setDueAmount(0.0);

			for (RentCollection rentCollection : lstCollection) {
				if (null != rentCollection.getPaymentId()) {
					if (rentCollection.getPaymentId().equals(rentPayment.getId())) {
						collectedAmount = collectedAmount + rentCollection.getPrincipalCollected();

					}
				} else {

					if (null != lstDemandTobeProcess.stream()
							.filter(tmpRentDemand -> tmpRentDemand.getId().equals(rentCollection.getDemandId()))
							.findAny().orElse(null))
						collectedAmount = collectedAmount + rentCollection.getPrincipalCollected();
				}
			}
			if ((collectedAmount + remainingInterest) < rentPayment.getAmountPaid())
				outstandingBalance = rentPayment.getAmountPaid() - collectedAmount;
			if (remainingInterest <= collectedAmount) {
				rentAccountStatement.setRemainingInterest(0.0);
				remainingInterest = 0;
				collectedAmount = collectedAmount - remainingInterest;
			} else {
				rentAccountStatement.setRemainingInterest(remainingInterest - collectedAmount);
				remainingInterest = remainingInterest - collectedAmount;
				collectedAmount = 0.0;
			}
			if (remainingPrincipal <= collectedAmount) {
				rentAccountStatement.setRemainingPrincipal(0.0 - outstandingBalance);
				remainingPrincipal = 0;
				collectedAmount = collectedAmount - remainingPrincipal;
			} else {
				rentAccountStatement.setRemainingPrincipal(remainingPrincipal - collectedAmount - outstandingBalance);
				remainingPrincipal = remainingPrincipal - collectedAmount;
				collectedAmount = 0.0;
			}
			rentAccountStatement.setDueAmount(
					rentAccountStatement.getRemainingInterest() + rentAccountStatement.getRemainingPrincipal());

			lstAccountStatement.add(rentAccountStatement);
			demands.removeAll(lstDemandTobeProcess);

		}
		if (demands.size() > 0) {
			for (RentDemand rentDemand : demands) {
				RentAccountStatement rentAccountStatement = new RentAccountStatement();
				rentAccountStatement.setDate(rentDemand.getGenerationDate());
				rentAccountStatement.setAmount(rentDemand.getCollectionPrincipal());
				rentAccountStatement.setType(Type.fromValue("D"));
				rentAccountStatement.setRemainingPrincipal(
						remainingPrincipal + rentDemand.getCollectionPrincipal() - outstandingBalance);

				remainingPrincipal = remainingPrincipal + rentDemand.getCollectionPrincipal() - outstandingBalance;
				float daysBetween = ((new Date().getTime() - rentDemand.getGenerationDate()) / (1000 * 60 * 60 * 24))
						+ 1;
				// If days cross the gross period then the interest is applicable
				if (daysBetween >= rentDemand.getInitialGracePeriod()) {
					double interest = ((rentDemand.getCollectionPrincipal() * interestRate) / 100)
							* (daysBetween / 365);
					remainingInterest = remainingInterest + interest;
				}
				rentAccountStatement.setRemainingInterest(remainingInterest);
				rentAccountStatement.setDueAmount(
						rentAccountStatement.getRemainingInterest() + rentAccountStatement.getRemainingPrincipal());
				lstAccountStatement.add(rentAccountStatement);
			}

		}
		return lstAccountStatement;
	}
}
