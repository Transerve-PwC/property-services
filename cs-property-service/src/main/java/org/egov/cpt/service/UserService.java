package org.egov.cpt.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.cpt.config.PropertyConfiguration;
import org.egov.cpt.models.CreateUserRequest;
import org.egov.cpt.models.OwnerInfo;
import org.egov.cpt.models.Property;
import org.egov.cpt.models.RentDetail;
import org.egov.cpt.models.UserDetailResponse;
import org.egov.cpt.models.UserSearchRequest;
import org.egov.cpt.repository.ServiceRequestRepository;
import org.egov.cpt.web.contracts.PropertyRentRequest;
import org.egov.cpt.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class UserService {

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.context.path}")
	private String userContextPath;

	@Value("${egov.user.create.path}")
	private String userCreateEndpoint;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	@Value("${egov.user.update.path}")
	private String userUpdateEndpoint;

	public void createUser(PropertyRentRequest request) {
//		UserDetailResponse userResponse=null;

		List<RentDetail> rentDetails = request.getRentDetails();
		RequestInfo requestInfo = request.getRequestInfo();
		Role role = getCitizenRole(rentDetails.get(0).getTenantId());

		rentDetails.forEach(rentDetail -> {
			UserDetailResponse userDetailResponse = searchByUserName(rentDetail.getOwnerMobileNumber(), getStateLevelTenant(rentDetail.getTenantId()));
			org.egov.cpt.models.OwnerInfo owner=new org.egov.cpt.models.OwnerInfo();
			if (CollectionUtils.isEmpty(userDetailResponse.getUser())) {

				addUserDefaultFields(rentDetail.getTenantId(), role, owner);
				StringBuilder uri = new StringBuilder(userHost).append(userContextPath).append(userCreateEndpoint);
				owner.setUserName(rentDetail.getOwnerMobileNumber());
				owner.setName(rentDetail.getOwnerMobileNumber());
				owner.setMobileNumber(rentDetail.getOwnerMobileNumber());

				UserDetailResponse userResponse = userCall(new CreateUserRequest(requestInfo, owner), uri);
                if (userResponse.getUser().get(0).getUuid() == null) {
                    throw new CustomException("INVALID USER RESPONSE", "The user created has uuid as null");
                }
                log.info("owner created --> " + userResponse.getUser().get(0).getUuid());
                setOwnerFields(owner, userResponse, requestInfo);

				if (ObjectUtils.isEmpty(userResponse)) {
					throw new CustomException("INVALID USER RESPONSE",
							"The user create has failed for the mobileNumber : " + owner.getUserName());
				}
			} else {
				owner.setId(userDetailResponse.getUser().get(0).getId());
				owner.setUuid(userDetailResponse.getUser().get(0).getUuid());
				addUserDefaultFields(rentDetail.getTenantId(), role, owner);

				StringBuilder uri = new StringBuilder(userHost).append(userContextPath).append(userUpdateEndpoint);
				userDetailResponse = userCall(new CreateUserRequest(requestInfo, owner), uri);
				if (userDetailResponse.getUser().get(0).getUuid() == null) {
					throw new CustomException("INVALID USER RESPONSE", "The user updated has uuid as null");
				}
			}
		});
//		return userResponse;
	}

	private void addUserDefaultFields(String tenantId, Role role, org.egov.cpt.models.OwnerInfo owner) {
		        owner.setActive(true);
		        owner.setTenantId(tenantId.split("\\.")[0]);
		        owner.setRoles(Collections.singletonList(role));
		        owner.setType("CITIZEN");
		        owner.setCorrespondenceAddress("Address");
		        owner.setPermanentAddress("Address");
	}

	/**
	 * provides a user search request with basic mandatory parameters
	 * 
	 * @param tenantId
	 * @param requestInfo
	 * @return
	 */
	public UserSearchRequest getBaseUserSearchRequest(String transitNumber, RequestInfo requestInfo) {
		return UserSearchRequest.builder().requestInfo(requestInfo).transitNumber(transitNumber).build();
	}

	/**
	 * Returns user using user search based on propertyCriteria(owner
	 * name,mobileNumber,userName)
	 * 
	 * @param criteria
	 * @param requestInfo
	 * @return serDetailResponse containing the user if present and the
	 *         responseInfo
	 */
	public UserDetailResponse getUser(UserSearchRequest userSearchRequest) {
		StringBuilder uri = new StringBuilder(userHost).append(userSearchEndpoint);
		UserDetailResponse userDetailResponse = userCall(userSearchRequest, uri);
		return userDetailResponse;
	}

	/**
	 * Returns UserDetailResponse by calling user service with given uri and
	 * object
	 * 
	 * @param userRequest
	 *            Request object for user service
	 * @param uri
	 *            The address of the endpoint
	 * @return Response from user service as parsed as userDetailResponse
	 */
	@SuppressWarnings("unchecked")
	private UserDetailResponse userCall(Object userRequest, StringBuilder uri) {

		String dobFormat = null;
		if (uri.toString().contains(userSearchEndpoint) || uri.toString().contains(userUpdateEndpoint))
			dobFormat = "yyyy-MM-dd";
		else if (uri.toString().contains(userCreateEndpoint))
			dobFormat = "dd/MM/yyyy";
		try{
            LinkedHashMap responseMap = (LinkedHashMap)serviceRequestRepository.fetchResult(uri, userRequest);
            parseResponse(responseMap,dobFormat);
            UserDetailResponse userDetailResponse = mapper.convertValue(responseMap,UserDetailResponse.class);
            return userDetailResponse;
        }
        catch(IllegalArgumentException  e)
        {
            throw new CustomException("IllegalArgumentException","ObjectMapper not able to convertValue in userCall");
        }
	}

	/**
	 * Parses date formats to long for all users in responseMap
	 * 
	 * @param responeMap
	 *            LinkedHashMap got from user api response
	 * @param dobFormat
	 *            dob format (required because dob is returned in different
	 *            format's in search and create response in user service)
	 */
	@SuppressWarnings("unchecked")
	private void parseResponse(LinkedHashMap<String, Object> responeMap, String dobFormat) {

		List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responeMap.get("user");
		String format1 = "dd-MM-yyyy HH:mm:ss";

		if (null != users) {

			users.forEach(map -> {

				map.put("createdDate", dateTolong((String) map.get("createdDate"), format1));
				if ((String) map.get("lastModifiedDate") != null)
					map.put("lastModifiedDate", dateTolong((String) map.get("lastModifiedDate"), format1));
				if ((String) map.get("dob") != null)
					map.put("dob", dateTolong((String) map.get("dob"), dobFormat));
				if ((String) map.get("pwdExpiryDate") != null)
					map.put("pwdExpiryDate", dateTolong((String) map.get("pwdExpiryDate"), format1));
			});
		}
	}

	/**
	 * Converts date to long
	 * 
	 * @param date
	 *            date to be parsed
	 * @param format
	 *            Format of the date
	 * @return Long value of date
	 */
	private Long dateTolong(String date, String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		Date d = null;
		try {
			d = f.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d.getTime();
	}

	private Role getCitizenRole(String tenantId){
	        Role role = new Role();
	        role.setCode("CITIZEN");
	        role.setName("Citizen");
//	        role.setTenantId(getStateLevelTenant(tenantId));
	        return role;
	    }

	private String getStateLevelTenant(String tenantId) {
		return tenantId.split("\\.")[0];
	}
	
	private UserDetailResponse searchByUserName(String userName,String tenantId){
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setUserType("CITIZEN");
        userSearchRequest.setUserName(userName);
        userSearchRequest.setTenantId(tenantId);
        StringBuilder uri = new StringBuilder(userHost).append(userSearchEndpoint);
        return userCall(userSearchRequest,uri);

    }
	
	private void setOwnerFields(OwnerInfo owner, UserDetailResponse userDetailResponse,RequestInfo requestInfo){
        owner.setUuid(userDetailResponse.getUser().get(0).getUuid());
        owner.setId(userDetailResponse.getUser().get(0).getId());
        owner.setUserName((userDetailResponse.getUser().get(0).getUserName()));
        owner.setCreatedBy(requestInfo.getUserInfo().getUuid());
        owner.setLastModifiedBy(requestInfo.getUserInfo().getUuid());
//        owner.setCreatedDate(System.currentTimeMillis());
//        owner.setLastModifiedDate(System.currentTimeMillis());
        owner.setActive(userDetailResponse.getUser().get(0).getActive());
    }

}
