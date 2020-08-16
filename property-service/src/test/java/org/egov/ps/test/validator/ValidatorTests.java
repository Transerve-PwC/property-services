package org.egov.ps.test.validator;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;

import org.egov.ps.validator.ApplicationField;
import org.egov.ps.validator.ApplicationValidation;
import org.egov.ps.validator.IApplicationField;
import org.egov.ps.validator.IValidation;
import org.egov.ps.validator.application.EmailValidator;
import org.egov.ps.validator.application.PhoneNumberValidator;
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
    EmailValidator emailValidator;

    @Autowired
    PhoneNumberValidator phoneNumberValidator;

    @Test
    public void testEmailValidator() {
        IValidation validation = ApplicationValidation.builder().type("email").build();
        IApplicationField field = ApplicationField.builder().path("email").required(false).build();

        assertNull(emailValidator.validate(validation, field, "minesh.b@transerve.com", null));
        assertNull(emailValidator.validate(validation, field, "minesh.b@gmail.com", null));

        assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve.co.@m", null).isEmpty());
        assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve..@m", null).isEmpty());
        assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve@m", null).isEmpty());
        assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve@com.com", null).isEmpty());
        assertFalse(emailValidator.validate(validation, field, "minesh.b@gmail@yahoo", null).isEmpty());
        assertFalse(emailValidator.validate(validation, field, "minesh.b@gmail.com.com", null).isEmpty());
    }

    @Test
    public void testPhoneNumberValidator() {
        IValidation validation = ApplicationValidation.builder().type("phoneNumber").build();
        IApplicationField field = ApplicationField.builder().required(true).build();

        assertNull(phoneNumberValidator.validate(validation, field, "8866581197", null));
        assertNull(phoneNumberValidator.validate(validation, field, "8965321445", null));

        assertFalse(phoneNumberValidator.validate(validation, field, "414141253623", null).isEmpty());
        assertFalse(phoneNumberValidator.validate(validation, field, "88665811976", null).isEmpty());
        assertFalse(phoneNumberValidator.validate(validation, field, "8866581197a", null).isEmpty());
        assertFalse(phoneNumberValidator.validate(validation, field, "008866581197", null).isEmpty());
        assertFalse(phoneNumberValidator.validate(validation, field, "+918866581197", null).isEmpty());
    }
}