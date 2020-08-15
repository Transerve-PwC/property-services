package org.egov.ps.test.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.egov.ps.model.ApplicationField;
import org.egov.ps.model.ApplicationValidation;
import org.egov.ps.model.IApplicationField;
import org.egov.ps.model.IValidation;
import org.egov.ps.validator.EmailValidator;
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
public class EmailValidatorTest {
	
	//@InjectMocks
	//EmailValidator emailValidator;
	
	@Autowired
	EmailValidator emailValidator;
	
	@Test
	public void isValidEmail() throws JSONException {
		IValidation validation = ApplicationValidation.builder().type("email").build();
		IApplicationField field = ApplicationField.builder().required(false).build();
		
		assertTrue(emailValidator.isValid(validation, field, "minesh.b@transerve.com", null));
		assertTrue(emailValidator.isValid(validation, field, "minesh.b@gmail.com", null));
		
		assertFalse(emailValidator.isValid(validation, field, "minesh.b@transerve.co.@m", null));
		assertFalse(emailValidator.isValid(validation, field, "minesh.b@transerve..@m", null));
		assertFalse(emailValidator.isValid(validation, field, "minesh.b@transerve@m", null));
		assertFalse(emailValidator.isValid(validation, field, "minesh.b@transerve@com.com", null));
		assertFalse(emailValidator.isValid(validation, field, "minesh.b@gmail@yahoo", null));
		assertFalse(emailValidator.isValid(validation, field, "minesh.b@gmail.com.com", null));
	}
}
