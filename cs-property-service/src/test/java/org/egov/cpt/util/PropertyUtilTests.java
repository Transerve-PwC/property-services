package org.egov.cpt.util;

import static org.junit.Assert.assertEquals;

import org.egov.cpt.CSPropertyApplication;
import org.egov.tracer.model.CustomException;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CSPropertyApplication.class)
public class PropertyUtilTests {

    @Autowired
    PropertyUtil utils;

    @Test
    public void testValidConsumerCodes() {
        String[] transitSiteNumbers = { "3034", "1", "9999", "234" };
        for (String transitSiteNumber : transitSiteNumbers) {
            String consumerCode = utils.getPropertyRentConsumerCode(transitSiteNumber);
            assertEquals(transitSiteNumber, utils.getTransitNumberFromConsumerCode(consumerCode));
        }
    }

    @Test(expected = CustomException.class)
    public void testInvalidConsumerCodes() {
        String[] transitSiteNumbers = { "", "10000", "A-9999", "234a2" };
        for (String transitSiteNumber : transitSiteNumbers) {
            ExpectedException exception = ExpectedException.none();
            String consumerCode = utils.getPropertyRentConsumerCode(transitSiteNumber);
            exception.expect(CustomException.class);
            utils.getTransitNumberFromConsumerCode(consumerCode);
        }
    }
}