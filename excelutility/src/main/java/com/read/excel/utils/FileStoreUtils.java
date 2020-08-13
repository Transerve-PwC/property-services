package com.read.excel.utils;

import java.util.HashMap;
import java.util.Map;

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
	public String fetchFileStoreUrl(String tenantId, String fileStoreIds) {
		String responseMap ="";
		StringBuilder uri = new StringBuilder(fileStoreUrl);
		uri.append("?tenantId="+tenantId+"&fileStoreIds="+fileStoreIds);
		try {
			Map<String,Object> response = restTemplate.getForObject(uri.toString(), HashMap.class);
			responseMap = String.valueOf(response.get(fileStoreIds));
		} catch (Exception e) {
			log.error("Exception while fetching file url: ", e);
		}
		return responseMap;
	}
	

}
