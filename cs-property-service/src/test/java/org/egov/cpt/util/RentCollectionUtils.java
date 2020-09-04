package org.egov.cpt.util;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.cpt.models.RentAccountStatement;
import org.egov.cpt.models.RentAccountStatement.Type;

public class RentCollectionUtils {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");

	public void printStatement(List<RentAccountStatement> accountStatementItems) {
		if (CollectionUtils.isEmpty(accountStatementItems)) {
			System.out.println("=> Statement is empty <==");
		}
		// accountStatementItems.forEach(statementItem -> {
		// System.out.println(statementItem);
		// });
		System.out.println(String.format("%10s |%10s |%10s |%14s |%13s |%13s |%15s", "Date", "Amount", "Type",
				"Principal Due", "Interest Due", "Total Due", "Account Balance"));
		System.out.println(
				"=================================================================================================");
		accountStatementItems.forEach(item -> {
			System.out.println(String.format("%10s |%10.2f |%10s |%14.2f |%13.2f |%13.2f |%15.2f",
					dateFormat.format(new Date(item.getDate())), item.getAmount(),
					item.getType() == RentAccountStatement.Type.C ? "Payment" : "Rent", item.getRemainingPrincipal(),
					item.getRemainingInterest(), item.getDueAmount(), item.getRemainingBalance()));
		});
	}

	private static final double ERROR_RANGE = 25.0D;

	public void reconcileStatement(List<RentAccountStatement> accountStatementItems, double interestRate) {
		Iterator<RentAccountStatement> accountStatementIterator = accountStatementItems.iterator();
		RentAccountStatement prevItem = null;
		RentAccountStatement currentItem = null;
		while (accountStatementIterator.hasNext()) {
			prevItem = currentItem;
			currentItem = accountStatementIterator.next();
			if (prevItem == null) {
				continue;
			}
			/**
			 * If account has balance, it should mean principal, interest and due amounts
			 * should be zero.
			 */
			if (currentItem.getRemainingBalance() > 0) {
				assertEquals(currentItem.getRemainingPrincipal(), 0D, 0.0);
				assertEquals(currentItem.getRemainingInterest(), 0D, 0.0);
				assertEquals(currentItem.getDueAmount(), 0D, 0.0);
			}
			boolean isPayment = currentItem.getType() == RentAccountStatement.Type.C;

			/**
			 * If there was no principal earlier, there should be no interest now. Any
			 * credit should reduce the principal to zero and accumulate into account if any
			 */
			final double totalAmount = isPayment ? -currentItem.getAmount() : currentItem.getAmount();
			if (prevItem.getRemainingPrincipal() == 0) {
				assertEquals(0, currentItem.getRemainingInterest(), 0D);
				assertEquals(Math.max(0, totalAmount - prevItem.getRemainingBalance()),
						currentItem.getRemainingPrincipal(), 0D);
				assertEquals(Math.max(0, prevItem.getRemainingBalance() - totalAmount),
						currentItem.getRemainingBalance(), 0D);
			} else {

				long daysBetween = getDaysBetween(prevItem.getDate(), currentItem.getDate());
				/**
				 * DISCLAIMER: There is no way to check the exact interest when the payment date
				 * is close to the last demand generation date. For all practical purposes it is
				 * sufficient to consider only one demand before the current payment for tax
				 * grace period.
				 */
				double interestBetweenRange = prevItem.getRemainingPrincipal() * daysBetween * interestRate / 100 / 365;
				if (currentItem.getType() == Type.C && prevItem.getType() == Type.D && daysBetween > 0
						&& daysBetween < 10) {
					interestBetweenRange = (prevItem.getRemainingPrincipal() - prevItem.getAmount()) * daysBetween
							* interestRate / 100 / 365;
				}
				double expectedInterest = isPayment
						? Math.max(0, prevItem.getRemainingInterest() + interestBetweenRange - currentItem.getAmount())
						: prevItem.getRemainingInterest() + interestBetweenRange;
				assertEquals(dateFormat.format(currentItem.getDate()), expectedInterest,
						currentItem.getRemainingInterest(), ERROR_RANGE);

				double expectedPrincipal = isPayment
						? (expectedInterest > 0 ? prevItem.getRemainingPrincipal()
								: Math.max(0,
										(prevItem.getRemainingPrincipal() + prevItem.getRemainingInterest()
												+ interestBetweenRange) - currentItem.getAmount()))
						: prevItem.getRemainingPrincipal() + currentItem.getAmount();
				assertEquals(dateFormat.format(currentItem.getDate()), expectedPrincipal,
						currentItem.getRemainingPrincipal(), ERROR_RANGE);

				double expectedBalance = isPayment ? Math.max(0, currentItem.getAmount()
						- (prevItem.getRemainingPrincipal() + prevItem.getRemainingInterest() + interestBetweenRange))
						: 0;
				assertEquals(dateFormat.format(currentItem.getDate()), expectedBalance,
						currentItem.getRemainingBalance(), ERROR_RANGE);
			}
		}
	}

	private long getDaysBetween(long startTimestamp, long endTimestamp) {
		LocalDate startDate = Instant.ofEpochMilli(startTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate endDate = Instant.ofEpochMilli(endTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();
		return ChronoUnit.DAYS.between(startDate, endDate);
	}
}