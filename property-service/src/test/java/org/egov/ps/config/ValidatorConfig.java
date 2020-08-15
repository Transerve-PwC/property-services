package org.egov.ps.config;

import org.egov.ps.service.MDMSService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class ValidatorConfig {

	@Bean
	@Primary
	public MDMSService mdmsService() {
		return Mockito.mock(MDMSService.class);
	}

}
