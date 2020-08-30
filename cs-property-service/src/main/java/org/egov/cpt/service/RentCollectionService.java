package org.egov.cpt.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
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
		Collections.sort(demandsToBeSettled);
		Collections.sort(payments);
		/**
		 * Don't process payments that are already processed.
		 */
		List<RentPayment> paymentsToBeSettled = payments.stream().filter(payment -> !payment.isProcessed())
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
		List<RentDemand> leftOverDemands = demandsToBeSettled.stream().filter(RentDemand::isUnPaid)
				.collect(Collectors.toList());
		if (leftOverDemands.size() == 0) {
			return collections;
		}

		/**
		 * In the case of 1) demand generation at 1st of every month. 2) More amount
		 * payed toward the end which should be adjusted to left over demands.
		 */
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

		/**
		 * Settle each demand by creating an empty payment with the demand generation
		 * date.
		 */
		for (RentDemand demand : demandsAfterPayments) {
			RentPayment payment = RentPayment.builder().amountPaid(0D).dateOfPayment(demand.getGenerationDate())
					.build();
			List<RentCollection> settledCollections = settlePayment(demandsToBeSettled, payment, account, 0);
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
		List<RentDemand> demands = demandsToBeSettled.stream()
				.filter(demand -> demand.isUnPaid() && demand.getGenerationDate() <= payment.getDateOfPayment())
				.collect(Collectors.toList());

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

		boolean hasDemandsWithInterest = interestRate > 0 && demands.stream()
				.filter(demand -> demand.getInterestSince().longValue() < payment.getDateOfPayment().longValue())
				.count() > 0;
		/**
		 * Amount is left after deducting interest for all the demands. Extract
		 * Principal.
		 */

		List<RentCollection> principalCollections = effectiveAmount > 0 && !hasDemandsWithInterest
				? extractPrincipal(demands, effectiveAmount, payment.getDateOfPayment())
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
		payment.setProcessed(true);
		return Stream.of(interestCollections, principalCollections).flatMap(x -> x.stream())
				.collect(Collectors.toList());
	}

	private List<RentCollection> extractPrincipal(List<RentDemand> demands, double paymentAmount,
			long paymentTimestamp) {
		ArrayList<RentCollection> collections = new ArrayList<RentCollection>();
		List<RentDemand> filteredDemands = demands.stream().filter(RentDemand::isUnPaid).collect(Collectors.toList());
		for (RentDemand demand : filteredDemands) {
			if (paymentAmount <= 0) {
				break;
			}
			double collectionAmount = Math.min(demand.getRemainingPrincipal(), paymentAmount);

			paymentAmount -= collectionAmount;
			collections.add(RentCollection.builder().demandId(demand.getId()).principalCollected(collectionAmount)
					.collectedAt(paymentTimestamp).build());
			demand.setRemainingPrincipalAndUpdatePaymentStatus(demand.getRemainingPrincipal() - collectionAmount);
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
			LocalDate demandGenerationDate = getLocalDate(demand.getGenerationDate());
			LocalDate paymentDate = getLocalDate(paymentTimeStamp);

			long noOfDaysBetweenGenerationAndPayment = ChronoUnit.DAYS.between(demandGenerationDate, paymentDate);
			if (noOfDaysBetweenGenerationAndPayment <= demand.getInitialGracePeriod()) {
				continue;
			}

			LocalDate demandInterestSinceDate = getLocalDate(demand.getInterestSince());

			long noOfDaysForInterestCalculation = ChronoUnit.DAYS.between(demandInterestSinceDate, paymentDate);

			if (noOfDaysForInterestCalculation == 0) {
				continue;
			}
			double interest = demand.getRemainingPrincipal() * noOfDaysForInterestCalculation * interestRate / 365
					/ 100;
			if (interest < paymentAmount) {
				collections.add(RentCollection.builder().interestCollected(interest).collectedAt(paymentTimeStamp)
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
	@Override
	public RentSummary calculateRentSummaryAt(List<RentDemand> demands, RentAccount rentAccount, double interestRate,
			long atTimestamp) {
		final LocalDate atDate = getLocalDate(atTimestamp);
		return demands.stream().filter(RentDemand::isUnPaid).reduce(
				RentSummary.builder().balanceAmount(rentAccount.getRemainingAmount()).build(), (summary, demand) -> {

					/**
					 * Calculate interest till atDate
					 */
					LocalDate demandGenerationDate = getLocalDate(demand.getGenerationDate());
					double calculatedInterest = 0D;
					long noOfDaysBetweenGenerationAndPayment = 1
							+ ChronoUnit.DAYS.between(demandGenerationDate, atDate);
					if (noOfDaysBetweenGenerationAndPayment > demand.getInitialGracePeriod()) {

						LocalDate demandInterestSinceDate = getLocalDate(demand.getInterestSince());

						long noOfDaysForInterestCalculation = ChronoUnit.DAYS.between(demandInterestSinceDate, atDate);
						calculatedInterest = demand.getRemainingPrincipal() * noOfDaysForInterestCalculation
								* interestRate / 365 / 100;
					}
					/**
					 * Summarize the result.
					 */
					return RentSummary.builder()
							.balancePrincipal(summary.getBalancePrincipal() + demand.getRemainingPrincipal())
							.balanceInterest(summary.getBalanceInterest() + calculatedInterest)
							.balanceAmount(rentAccount.getRemainingAmount()).build();
				}, (summary, demand) -> summary);
	}

	private LocalDate getLocalDate(long atTimestamp) {
		return Instant.ofEpochMilli(atTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();
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
	public List<RentAccountStatement> getAccountStatement(List<RentDemand> demands, List<RentPayment> payments,
			double interestRate, Long fromDateTimestamp, Long toDateTimestamp) {
		Collections.sort(demands);
		Collections.sort(payments);
		List<RentAccountStatement> accountStatementItems = new ArrayList<RentAccountStatement>();
		RentAccount rentAccount = RentAccount.builder().build();
		List<RentDemand> demandsToBeSettled = new ArrayList<RentDemand>(demands.size());
		Iterator<RentDemand> demandIterator = demands.iterator();
		Iterator<RentPayment> paymentIterator = payments.iterator();
		RentDemand currentDemand = demandIterator.hasNext() ? demandIterator.next() : null;
		RentPayment currentPayment = paymentIterator.hasNext() ? paymentIterator.next() : null;
		while (true) {
			RentSummary rentSummary;
			RentAccountStatement statement = RentAccountStatement.builder().build();
			if (currentDemand == null && currentPayment == null) {
				break;
			} else if (currentDemand == null) {
				this.settle(demandsToBeSettled, Collections.singletonList(currentPayment), rentAccount, interestRate);
				rentSummary = calculateRentSummaryAt(demandsToBeSettled, rentAccount, interestRate,
						currentPayment.getDateOfPayment());
				statement.setDate(currentPayment.getDateOfPayment());
				statement.setAmount(currentPayment.getAmountPaid());
				statement.setType(Type.C);
				currentPayment = paymentIterator.hasNext() ? paymentIterator.next() : null;
			} else if (currentPayment == null) {
				demandsToBeSettled.add(currentDemand);
				this.settle(demandsToBeSettled, Collections.emptyList(), rentAccount, interestRate);
				rentSummary = calculateRentSummaryAt(demandsToBeSettled, rentAccount, interestRate,
						currentDemand.getGenerationDate());
				statement.setDate(currentDemand.getGenerationDate());
				statement.setAmount(currentDemand.getCollectionPrincipal());
				statement.setType(Type.D);
				currentDemand = demandIterator.hasNext() ? demandIterator.next() : null;
			} else if (currentDemand.getGenerationDate() <= currentPayment.getDateOfPayment()) {
				demandsToBeSettled.add(currentDemand);
				this.settle(demandsToBeSettled, Collections.emptyList(), rentAccount, interestRate);
				rentSummary = calculateRentSummaryAt(demandsToBeSettled, rentAccount, interestRate,
						currentDemand.getGenerationDate());
				statement.setDate(currentDemand.getGenerationDate());
				statement.setAmount(currentDemand.getCollectionPrincipal());
				statement.setType(Type.D);
				currentDemand = demandIterator.hasNext() ? demandIterator.next() : null;
			} else {
				this.settle(demandsToBeSettled, Collections.singletonList(currentPayment), rentAccount, interestRate);
				rentSummary = calculateRentSummaryAt(demandsToBeSettled, rentAccount, interestRate,
						currentPayment.getDateOfPayment());
				statement.setDate(currentPayment.getDateOfPayment());
				statement.setAmount(currentPayment.getAmountPaid());
				statement.setType(Type.C);
				currentPayment = paymentIterator.hasNext() ? paymentIterator.next() : null;
			}
			statement.setRemainingPrincipal(rentSummary.getBalancePrincipal());
			statement.setRemainingInterest(rentSummary.getBalanceInterest());
			statement.setRemainingBalance(rentSummary.getBalanceAmount());
			accountStatementItems.add(statement);
		}
		return accountStatementItems;
	}

	@Override
	public RentSummary calculateRentSummary(List<RentDemand> demands, RentAccount rentAccount, double interestRate) {
		return this.calculateRentSummaryAt(demands, rentAccount, interestRate, System.currentTimeMillis());
	}
}
