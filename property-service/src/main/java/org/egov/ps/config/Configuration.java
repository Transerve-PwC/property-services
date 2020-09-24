package org.egov.ps.config;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Import({ TracerConfiguration.class })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class Configuration {

	@Value("${app.timezone}")
	private String timeZone;

	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	@Bean
	@Autowired
	public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		return converter;
	}

	// PERSISTER

	@Value("${persister.save.property.topic}")
	private String savePropertyTopic;

	@Value("${persister.update.property.topic}")
	private String updatePropertyTopic;

	@Value("${persister.save.application.topic}")
	private String saveApplicationTopic;

	@Value("${persister.update.application.topic}")
	private String updateApplicationTopic;
	
	@Value("${persister.save.auction.topic}")
	private String saveAuctionTopic;
	
	@Value("${persister.update.auction.topic}")
	private String updateAuctionTopic;

	// Workflow

	@Value("${is.workflow.enabled}")
	private Boolean isWorkflowEnabled;

	@Value("${create.ps.workflow.name}")
	private String psBusinessServiceValue;
	
	@Value("${create.eb.ot.sd.workflow.name}")
	private String ebOtSdBusinessServiceValue;

	@Value("${workflow.context.path}")
	private String wfHost;

	@Value("${workflow.transition.path}")
	private String wfTransitionPath;

	// Property Search params
	@Value("${pt.search.pagination.default.limit}")
	private Long defaultLimit;

	@Value("${pt.search.pagination.default.offset}")
	private Long defaultOffset;

	@Value("${pt.search.pagination.max.search.limit}")
	private Long maxSearchLimit;

	// BUSINESS SERVICE

	@Value("${workflow.businessservice.search.path}")
	private String wfBusinessServiceSearchPath;

	// ID Generation

	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Value("${egov.idgen.ps.applicationNum.name}")
	private String applicationNumberIdgenNamePS;

	@Value("${egov.idgen.ps.applicationNum.format}")
	private String applicationNumberIdgenFormatPS;

	// MDMS Configuration

	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsSearchEndpoint;

	@Value("${egov.mdms.get.endpoint}")
	private String mdmsGetEndpoint;

}