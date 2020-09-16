package org.egov.ps.test.validator;

import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.egov.ps.validator.ApplicationField;
import org.egov.ps.validator.ApplicationValidation;
import org.egov.ps.validator.DateField;
import org.egov.ps.validator.IApplicationField;
import org.egov.ps.validator.IValidation;
import org.egov.ps.validator.application.ArrayLengthValidator;
import org.egov.ps.validator.application.ArrayValidator;
import org.egov.ps.validator.application.BooleanValidator;
import org.egov.ps.validator.application.DateRangeValidator;
import org.egov.ps.validator.application.DateValidator;
import org.egov.ps.validator.application.EmailValidator;
import org.egov.ps.validator.application.LengthValidator;
import org.egov.ps.validator.application.MinMaxValidator;
import org.egov.ps.validator.application.NumericValidator;
import org.egov.ps.validator.application.ObjectValidator;
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

	@Autowired
	LengthValidator lengthValidator;

	@Autowired
	DateRangeValidator dateRangeValidator;

	@Autowired
	MinMaxValidator minMaxValidator;

	@Autowired
	NumericValidator numericValidator;

	@Autowired
	DateValidator dateValidator;

	@Autowired
	ObjectValidator objectValidator;

	@Autowired
	BooleanValidator booleanValidator;

	@Autowired
	ArrayValidator arrayValidator;

	@Autowired
	ArrayLengthValidator arrayLengthValidator;


	@Test
	public void testDateRangeValidator() {
		IApplicationField field = ApplicationField.builder().required(true).build();


		
		
		//##########
		
		DateField startDate = DateField.builder()
				.unit("year")
				.value("-40")
				.build();

		DateField endDate = DateField.builder()
				.unit("second")
				.value("0")
				.build();

		Map<String, Object> map_1= new HashMap<String, Object>();
		map_1.put("start", startDate);
		map_1.put("end", endDate);

		IValidation validation_1 = ApplicationValidation.builder()
				.type("date-range")
				.params(map_1)
				.build();
																	
		assertNull(dateRangeValidator.validate(validation_1, field, 1594899004, null));
		//assertNull(dateRangeValidator.validate(validation_1, field, "1597827021", null));

	}

	
}