package org.egov.cpt.validations;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BasicValidationTests {

    boolean validateTransitNumber(String transitNumber) {
        String regex = "\\d{1,4}";
        return transitNumber.matches(regex);
        // if (!transitNumber.matches(regex)) {
        // return false;
        // }
        // int intTransitNo = Integer.parseInt(transitNumber);
        // return intTransitNo > 0 && intTransitNo <= 10000;
    }

    @Test
    public void testValidTransitNumbers() {
        String[] transitNumbers = { "apple", "5040", "A-151", "151A", "500", "10001" };
        boolean[] expectedValues = { false, true, false, false, true, false };

        for (int i = 0; i < transitNumbers.length; i++) {
            assertTrue(transitNumbers[i], validateTransitNumber(transitNumbers[i]) == expectedValues[i]);
        }
    }
}