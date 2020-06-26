package org.egov.cpt.config;

import org.egov.cpt.producer.Producer;
import org.egov.cpt.workflow.WorkflowIntegrator;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestConfiguration {
	@Bean
    @Primary
	 public WorkflowIntegrator workflowIntegrator(){
    	return Mockito.mock(WorkflowIntegrator.class);
    }
	
	@Bean
    @Primary
	 public Producer producer(){
    	return Mockito.mock(Producer.class);
    }
}
