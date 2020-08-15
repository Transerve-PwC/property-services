package org.egov.ps.test.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.egov.ps.model.ApplicationField;
import org.egov.ps.model.ApplicationValidation;
import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.egov.ps.validator.PhoneNumberValidator;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
//@RunWith(MockitoJUnitRunner.class)
public class PhoneNumberValidatorTest {

	//@InjectMocks
	//PhoneNumberValidator phoneNumberValidator;

	@Autowired
	PhoneNumberValidator phoneNumberValidator;
	

	@Test
	public void isValidPhoneNumber() throws JSONException {
		IValidation validation = ApplicationValidation.builder().type("phoneNumber").build();
		IApplicationField field = ApplicationField.builder().required(true).build();
		
		assertTrue(phoneNumberValidator.isValid(validation, field, "8866581197", null));
		assertTrue(phoneNumberValidator.isValid(validation, field, "8965321445", null));
		
		assertFalse(phoneNumberValidator.isValid(validation, field, "414141253623", null));
		assertFalse(phoneNumberValidator.isValid(validation, field, "88665811976", null));
		assertFalse(phoneNumberValidator.isValid(validation, field, "8866581197a", null));
		assertFalse(phoneNumberValidator.isValid(validation, field, "008866581197", null));
		assertFalse(phoneNumberValidator.isValid(validation, field, "+918866581197", null));
	}

}
