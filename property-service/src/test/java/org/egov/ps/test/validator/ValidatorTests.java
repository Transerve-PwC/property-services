package org.egov.ps.test.validator;

import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.egov.ps.validator.ApplicationField;
import org.egov.ps.validator.ApplicationValidation;
import org.egov.ps.validator.DateField;
import org.egov.ps.validator.IApplicationField;
import org.egov.ps.validator.IValidation;
import org.egov.ps.validator.application.DateRangeValidator;
import org.egov.ps.validator.application.EmailValidator;
import org.egov.ps.validator.application.LengthValidator;
import org.egov.ps.validator.application.MinMaxValidator;
import org.egov.ps.validator.application.NumericValidator;
import org.egov.ps.validator.application.PhoneNumberValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ValidatorTests {

	@Autowired
	EmailValidator emailValidator;

	@Autowired
	PhoneNumberValidator phoneNumberValidator;

	@Autowired
	LengthValidator lengthValidator;

	@Autowired
	DateRangeValidator dateRangeValidator;

	@Autowired
	MinMaxValidator minMaxValidator;
	
	@Autowired
	NumericValidator numericValidator;

	@Test
	public void testEmailValidator() {
		IValidation validation = ApplicationValidation.builder().type("email").build();
		IApplicationField field = ApplicationField.builder().path("email").required(false).build();

		//field require false
		assertNull(emailValidator.validate(validation, field, "", null));
		assertNull(emailValidator.validate(validation, field, null, null));
		assertNull(emailValidator.validate(validation, field, "minesh.b@transerve.com", null));
		assertNull(emailValidator.validate(validation, field, "minesh.b@gmail.com", null));

		assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve.co.@m", null).isEmpty());
		assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve..@m", null).isEmpty());
		assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve@m", null).isEmpty());
		assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve@com.com", null).isEmpty());
		assertFalse(emailValidator.validate(validation, field, "minesh.b@gmail@yahoo", null).isEmpty());
		assertFalse(emailValidator.validate(validation, field, "minesh.b@gmail.com.com", null).isEmpty());

		//field require true
		field = ApplicationField.builder().path("email").required(true).build();
		assertNull(emailValidator.validate(validation, field, "minesh.b@transerve.com", null));
		assertNull(emailValidator.validate(validation, field, "minesh.b@gmail.com", null));

		assertFalse(emailValidator.validate(validation, field, "", null).isEmpty());
		assertFalse(emailValidator.validate(validation, field, null, null).isEmpty());

		assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve.co.@m", null).isEmpty());
		assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve..@m", null).isEmpty());
		assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve@m", null).isEmpty());
		assertFalse(emailValidator.validate(validation, field, "minesh.b@transerve@com.com", null).isEmpty());
		assertFalse(emailValidator.validate(validation, field, "minesh.b@gmail@yahoo", null).isEmpty());
		assertFalse(emailValidator.validate(validation, field, "minesh.b@gmail.com.com", null).isEmpty());
	}

	@Test
	public void testPhoneNumberValidator() {

		//field require true
		IValidation validation = ApplicationValidation.builder().type("mobile").build();
		IApplicationField field = ApplicationField.builder().required(true).build();

		assertNull(phoneNumberValidator.validate(validation, field, "8866581197", null));
		assertNull(phoneNumberValidator.validate(validation, field, "8965321445", null));

		assertFalse(phoneNumberValidator.validate(validation, field, "", null).isEmpty());
		assertFalse(phoneNumberValidator.validate(validation, field, null, null).isEmpty());
		assertFalse(phoneNumberValidator.validate(validation, field, "414141253623", null).isEmpty());
		assertFalse(phoneNumberValidator.validate(validation, field, "88665811976", null).isEmpty());
		assertFalse(phoneNumberValidator.validate(validation, field, "8866581197a", null).isEmpty());
		assertFalse(phoneNumberValidator.validate(validation, field, "008866581197", null).isEmpty());
		assertFalse(phoneNumberValidator.validate(validation, field, "+918866581197", null).isEmpty());

		//field require false

		field = ApplicationField.builder().required(false).build();

		assertNull(phoneNumberValidator.validate(validation, field, "8866581197", null));
		assertNull(phoneNumberValidator.validate(validation, field, "8965321445", null));
		assertNull(phoneNumberValidator.validate(validation, field, "", null));
		assertNull(phoneNumberValidator.validate(validation, field, null, null));

		assertFalse(phoneNumberValidator.validate(validation, field, "414141253623", null).isEmpty());
		assertFalse(phoneNumberValidator.validate(validation, field, "88665811976", null).isEmpty());
		assertFalse(phoneNumberValidator.validate(validation, field, "8866581197a", null).isEmpty());
		assertFalse(phoneNumberValidator.validate(validation, field, "008866581197", null).isEmpty());
		assertFalse(phoneNumberValidator.validate(validation, field, "+918866581197", null).isEmpty());
	}

	@Test
	public void testLengthValidator() {

		//field require true
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("max", 6);
		map.put("min", 3);

		IValidation validation = ApplicationValidation.builder()
				.type("length")
				.params(map)
				.build();
		IApplicationField field = ApplicationField.builder().required(true).build();
		assertNull(lengthValidator.validate(validation, field, "tested", null));

		assertFalse(lengthValidator.validate(validation, field, "", null).isEmpty());
		assertFalse(lengthValidator.validate(validation, field, null, null).isEmpty());
		assertFalse(lengthValidator.validate(validation, field, "te", null).isEmpty());
		assertFalse(lengthValidator.validate(validation, field, "testing", null).isEmpty());

		//field require false
		field = ApplicationField.builder().required(false).build();
		assertNull(lengthValidator.validate(validation, field, "tested", null));

		assertNull(lengthValidator.validate(validation, field, "", null));
		assertNull(lengthValidator.validate(validation, field, null, null));

		assertFalse(lengthValidator.validate(validation, field, "te", null).isEmpty());
		assertFalse(lengthValidator.validate(validation, field, "testing", null).isEmpty());

	}

	@Test
	public void testDateRangeValidator() {
		IApplicationField field = ApplicationField.builder().required(true).build();

		//

		DateField startDate = DateField.builder()
				.unit("month")
				.value("-6")
				.build();

		DateField endDate = DateField.builder()
				.unit("second")
				.value("0")
				.build();

		Map<String, Object> map_2= new HashMap<String, Object>();
		map_2.put("start", startDate);
		map_2.put("end", endDate);

		IValidation validation_2 = ApplicationValidation.builder()
				.type("date-range")
				.params(map_2)
				.build();

		assertNull(dateRangeValidator.validate(validation_2, field, "16-aug-2020", null));
		assertNull(dateRangeValidator.validate(validation_2, field, "15-May-2020", null));
		assertFalse(dateRangeValidator.validate(validation_2, field, "1-jan-2020", null).isEmpty());
		assertFalse(dateRangeValidator.validate(validation_2, field, "1-Dec-2020", null).isEmpty());


		//case : pass start date and end date - positive test case start < end
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("start", "01-Jan-2020");
		map.put("end", "01-Dec-2020");

		IValidation validation = ApplicationValidation.builder()
				.type("date-range")
				.params(map)
				.build();

		assertNull(dateRangeValidator.validate(validation, field, "01-Oct-2020", null));

		//case : not pass date start and end 
		IValidation validation_1 = ApplicationValidation.builder()
				.type("date-range")
				.build();
		assertNotNull(dateRangeValidator.validate(validation_1, field, "", null));
		assertNotNull(dateRangeValidator.validate(validation_1, field, null, null));


		//case : pass date start and end - negative test case start > end.. 
		map = new HashMap<String, Object>();
		map.put("start", "01-Jan-2021");
		map.put("end", "01-Dec-2020");

		IValidation validation_ = ApplicationValidation.builder()
				.type("date-range") 
				.params(map)
				.build();
		assertNotNull(dateRangeValidator.validate(validation_, field, null, null));
		assertNotNull(dateRangeValidator.validate(validation_, field, "01-Jan-1990", null));

	}

	@Test
	public void testMinMaxValidator() {
		//field require true
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("max", 6);
		map.put("min", 3);

		IValidation validation = ApplicationValidation.builder()
				.type("minmax")
				.params(map)
				.build();

		IApplicationField field = ApplicationField.builder().required(true).build();
		assertNull(minMaxValidator.validate(validation, field, "tested", null));

		assertFalse(minMaxValidator.validate(validation, field, "", null).isEmpty());
		assertFalse(minMaxValidator.validate(validation, field, null, null).isEmpty());

		assertFalse(minMaxValidator.validate(validation, field, "testingminmax", null).isEmpty());
		assertFalse(minMaxValidator.validate(validation, field, "te", null).isEmpty());
		assertFalse(minMaxValidator.validate(validation, field, "testing", null).isEmpty());

		//field require false

		field = ApplicationField.builder().required(false).build();
		assertNull(minMaxValidator.validate(validation, field, "tested", null));

		assertNull(minMaxValidator.validate(validation, field, "", null));
		assertNull(minMaxValidator.validate(validation, field, null, null));

		assertFalse(minMaxValidator.validate(validation, field, "testingminmax", null).isEmpty());
		assertFalse(minMaxValidator.validate(validation, field, "te", null).isEmpty());
		assertFalse(minMaxValidator.validate(validation, field, "testing", null).isEmpty());
	}

	@Test
	public void testNumericValidator() {
		IValidation validation = ApplicationValidation.builder().type("numeric").build();
		
		//field require true
		IApplicationField field = ApplicationField.builder().required(true).build();

		assertNull(numericValidator.validate(validation, field, "123654789", null));
		assertNull(numericValidator.validate(validation, field, "9876543210", null));

		assertFalse(numericValidator.validate(validation, field, null, null).isEmpty());
		assertFalse(numericValidator.validate(validation, field, "", null).isEmpty());
		
		assertFalse(numericValidator.validate(validation, field, "test", null).isEmpty());
		assertFalse(numericValidator.validate(validation, field, "123456te", null).isEmpty());
		assertFalse(numericValidator.validate(validation, field, "97a", null).isEmpty());
		
		//field require false
		field = ApplicationField.builder().required(false).build();

		assertNull(numericValidator.validate(validation, field, "123654789", null));
		assertNull(numericValidator.validate(validation, field, "9876543210", null));

		assertNull(numericValidator.validate(validation, field, null, null));
		assertNull(numericValidator.validate(validation, field, "", null));
		
		assertFalse(numericValidator.validate(validation, field, "test", null).isEmpty());
		assertFalse(numericValidator.validate(validation, field, "123456te", null).isEmpty());
		assertFalse(numericValidator.validate(validation, field, "97a", null).isEmpty());
	}
}