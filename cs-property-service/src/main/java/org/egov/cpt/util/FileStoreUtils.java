package org.egov.cpt.util;

import java.util.HashMap;
import java.util.Map;

import org.egov.cpt.models.ExcelSearchCriteria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class FileStoreUtils {

	@Value("${egov.filestore-service-host}${egov.file.url.path}")
	private String fileStoreUrl;

	private RestTemplate restTemplate;

	public FileStoreUtils(RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
	}

	@Cacheable(value = "fileUrl", sync = true)
	@SuppressWarnings("unchecked")
	public String fetchFileStoreUrl(ExcelSearchCriteria searchCriteria) {
		String responseMap = "";
		StringBuilder uri = new StringBuilder(fileStoreUrl);
		uri.append("?tenantId=" + searchCriteria.getTenantId() + "&fileStoreIds=" + searchCriteria.getFileStoreId());
		try {
			Map<String, Object> response = (Map<String, Object>) (restTemplate.getForObject(uri.toString(),
					HashMap.class));
			responseMap = String.valueOf(response.get(searchCriteria.getFileStoreId()));
		} catch (Exception e) {
			log.error("Exception while fetching file url: ", e);
		}
		return responseMap;
	}

}
