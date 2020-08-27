package org.egov.cpt.service;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.egov.cpt.models.RentAccount;
import org.egov.cpt.models.RentCollection;
import org.egov.cpt.models.RentDemand;
import org.egov.cpt.models.RentPayment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@SuppressWarnings("unused")
public class RentCollectionServiceTests {
    private final String JAN_1_2020 = "01 01 2020";
    private final String JAN_15_2020 = "15 01 2020";
    private final String FEB_1_2020 = "01 02 2020";
    private final String FEB_15_2020 = "15 02 2020";
    private final String MAY_1_2020 = "01 05 2020";
    private final double DEFAULT_INTEREST_RATE = 25D;
    private final double ZERO_INTEREST_RATE = 0D;

    RentCollectionService rentCollectionService;

    @Before
    public void setup() {
        this.rentCollectionService = new RentCollectionService();
    }

    /**
     * Always make sure the total paid amount = total collections +
     * 
     * Initial account balance : 0 Payment : 100 collection 100 final account
     * balance : 100
     * 
     * @throws ParseException
     */
    @Test
    public void testSimpleSettlement() throws ParseException {
        // Setup
        List<RentDemand> demands = Collections.emptyList();
        List<RentPayment> payments = Arrays.asList(getPayment(100D, JAN_1_2020), getPayment(200D, JAN_15_2020));
        RentAccount account = getAccount(0D);

        // Test
        List<RentCollection> collections = this.rentCollectionService.settle(demands, payments, account,
                ZERO_INTEREST_RATE);

        // Verify
        reconcileDemands(demands, collections);
        verifyCollectedAmount(collections, 0D);
        verifyRemainingBalance(account, 300D);
    }

    @Test
    public void testSimpleSettlementWithDemands() throws ParseException {
        // Setup
        List<RentDemand> demands = Arrays.asList(getDemand(400, JAN_1_2020));
        List<RentPayment> payments = Arrays.asList(getPayment(100D, JAN_1_2020), getPayment(200D, JAN_15_2020));
        RentAccount account = getAccount(50D);

        // Test
        List<RentCollection> collections = this.rentCollectionService.settle(demands, payments, account,
                ZERO_INTEREST_RATE);

        // Verify
        reconcileDemands(demands, collections);
        verifyCollectedAmount(collections, 350D);
        verifyRemainingBalance(account, 0D);
    }

    @Test
    public void testSimpleSettlementWithDemands2() throws ParseException {
        // Setup
        List<RentDemand> demands = Arrays.asList(getDemand(200, JAN_1_2020), getDemand(200, FEB_1_2020));
        List<RentPayment> payments = Arrays.asList(getPayment(100D, JAN_1_2020), getPayment(200D, JAN_15_2020));
        RentAccount account = getAccount(50D);

        // Test
        List<RentCollection> collections = this.rentCollectionService.settle(demands, payments, account,
                ZERO_INTEREST_RATE);

        // Verify
        reconcileDemands(demands, collections);
        verifyCollectedAmount(collections, 350D);
        verifyRemainingBalance(account, 0D);
    }

    @Test
    public void testNoPaymentsSettlement() throws ParseException {
        // Setup
        List<RentDemand> demands = Arrays.asList(getDemand(200, JAN_1_2020), getDemand(200, JAN_15_2020));
        List<RentPayment> payments = Collections.emptyList();
        RentAccount account = getAccount(350D);

        // Test
        List<RentCollection> collections = this.rentCollectionService.settle(demands, payments, account,
                ZERO_INTEREST_RATE);

        // Verify
        reconcileDemands(demands, collections);
        verifyCollectedAmount(collections, 350D);
        verifyRemainingBalance(account, 0D);
    }

    @Test
    public void testNoPaymentsSettlement2() throws ParseException {
        // Setup
        List<RentDemand> demands = Arrays.asList(getDemand(200, JAN_1_2020), getDemand(200, JAN_15_2020));
        List<RentPayment> payments = Collections.emptyList();
        RentAccount account = getAccount(550D);

        // Test
        List<RentCollection> collections = this.rentCollectionService.settle(demands, payments, account,
                ZERO_INTEREST_RATE);

        // Verify
        reconcileDemands(demands, collections);
        verifyCollectedAmount(collections, 400D);
        verifyRemainingBalance(account, 150D);
    }

    public void testExcessPaymentSettlement() throws ParseException {
        // Setup
        List<RentDemand> demands = Arrays.asList(getDemand(200, JAN_1_2020), getDemand(200, FEB_1_2020),
                getDemand(200, FEB_15_2020));
        List<RentPayment> payments = Arrays.asList(getPayment(400D, JAN_1_2020), getPayment(200D, JAN_15_2020));
    }

    @Test
    public void testInterestSettlement() throws ParseException {
        List<RentDemand> demands = getDemands(500, JAN_1_2020, 10);
        List<RentPayment> payments = getPayments(500, MAY_1_2020, 10);
        RentAccount account = getAccount(0);
        List<RentCollection> collections = this.rentCollectionService.settle(demands, payments, account, 25);
        assertEquals(29, collections.size());
    }

    private long getEpochFromDateString(String date) throws ParseException {
        return this.getDateFromString(date).getTime();
    }

    private static final String DATE_FORMAT = "dd MM yyyy";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

    private Date getDateFromString(String date) throws ParseException {
        return dateFormatter.parse(date);
    }

    private List<RentPayment> getPayments(double amount, String date, int count) throws ParseException {
        return Collections.nCopies(count, 0).stream().map(ele -> {
            try {
                return getPayment(amount, date);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());// ;);
    }

    private List<RentDemand> getDemands(double amount, String date, int count) throws ParseException {
        return IntStream.range(0, count).mapToObj(index -> {
            try {
                return getDemand(amount, date, "Demand-" + index);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

    private RentPayment getPayment(double amount, String date) throws ParseException {
        return RentPayment.builder().amountPaid(amount).dateOfPayment(getEpochFromDateString(date)).build();
    }

    private RentDemand getDemand(double amount, String date) throws ParseException {
        return this.getDemand(amount, date, "");
    }

    private RentDemand getDemand(double amount, String date, String demandId) throws ParseException {
        return RentDemand.builder().collectionPrincipal(amount).remainingPrincipal(amount).id(demandId)
                .generationDate(getEpochFromDateString(date)).interestSince(getEpochFromDateString(date)).build();
    }

    private RentAccount getAccount(double initialBalance) {
        return RentAccount.builder().remainingAmount(initialBalance).build();
    }

    private void reconcileDemands(List<RentDemand> demands, List<RentCollection> collections) {
        double collectionAccordingToDemands = demands.stream()
                .mapToDouble(demand -> demand.getCollectionPrincipal() - demand.getRemainingPrincipal()).sum();
        double collection = collections.stream().mapToDouble(RentCollection::getPrincipalCollected).sum();
        assertEquals(collectionAccordingToDemands, collection, 0.1);
    }

    private void verifyCollectedAmount(List<RentCollection> collections, double expectedAmount) {
        double totalCollected = collections.stream().map(RentCollection::getPrincipalCollected)
                .mapToDouble(Double::valueOf).sum();
        assertEquals(expectedAmount, totalCollected, 0.1);
    }

    private void verifyRemainingBalance(RentAccount account, double expectedBalance) {
        assertEquals(expectedBalance, account.getRemainingAmount(), 0.1);
    }
}