package org.egov.ps.test.validator;

import static org.junit.Assert.assertTrue;

import org.egov.ps.model.ApplicationField;
import org.egov.ps.model.ApplicationValidation;
import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.egov.ps.validator.CategoryValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ValidatorTests {

    @Autowired
    CategoryValidator categoryValidator;

    @Test
    public void testEmailValidator() {

        IValidation validation = ApplicationValidation.builder().type("email").build();
        IApplicationField field = ApplicationField.builder().path("email").build();

        // Test minesh@transerve.com is valid.
        assertTrue(this.categoryValidator.isValid(validation, field, "minesh@transerve.com", null));

        // Test minesh@transerve.c is invalid.

        // Test minesh@transerve.co@.com is invalid.

    }
}