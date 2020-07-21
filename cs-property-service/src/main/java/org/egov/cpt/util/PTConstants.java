package org.egov.cpt.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PTConstants {

	private PTConstants() {
	}

	public static final String businessService_csp = "CSP";

	public static final String JSONPATH_CODES = "$.MdmsRes.PropertyServices";

	public static final String MDMS_PT_MOD_NAME = "PropertyServices";

	public static final String MDMS_PT_PROPERTYTYPE = "PropertyType";

	public static final String MDMS_PT_USAGECATEGORY = "UsageCategory";

	public static final String MDMS_PT_PROPERTYSUBTYPE = "PropertySubType";

	public static final String MDMS_PT_OCCUPANCYTYPE = "OccupancyType";

	public static final String MDMS_PT_CONSTRUCTIONTYPE = "ConstructionType";

	public static final String MDMS_PT_CONSTRUCTIONSUBTYPE = "ConstructionSubType";

	public static final String MDMS_PT_OWNERSHIP = "OwnerShipCategory";

	public static final String MDMS_PT_SUBOWNERSHIP = "SubOwnerShipCategory";

	public static final String MDMS_PT_USAGEMAJOR = "UsageCategoryMajor";

	public static final String MDMS_PT_USAGEMINOR = "UsageCategoryMinor";

	public static final String MDMS_PT_USAGEDETAIL = "UsageCategoryDetail";

	public static final String MDMS_PT_USAGESUBMINOR = "UsageCategorySubMinor";

	public static final String MDMS_PT_OWNERTYPE = "OwnerType";

	public static final String MDMS_PT_EGF_MASTER = "egf-master";

	public static final String MDMS_PT_EGF_PROPERTY_SERVICE = "PropertyServices";

	public static final String MDMS_PT_FINANCIALYEAR = "FinancialYear";

	public static final String MDMS_PT_COLONY = "colonies";

	public static final String JSONPATH_FINANCIALYEAR = "$.MdmsRes.egf-master";

	public static final String JSONPATH_COLONY = "$.MdmsRes.PropertyServices";

	public static final String BOUNDARY_HEIRARCHY_CODE = "REVENUE";

//	payment

	public static final String BUSINESS_SERVICE_OT = "OwnershipTransferRP";

	public static final String BUSINESS_SERVICE_DC = "DuplicateCopyOfAllotmentLetterRP";

	public static final String ACTION_INITIATE = "INITIATE";

	public static final String ACTION_APPLY = "APPLY";

	public static final String ACTION_APPROVE = "APPROVE";

	public static final String ACTION_REJECT = "REJECT";

	public static final String TRIGGER_NOWORKFLOW = "NOWORKFLOW";

	public static final String ACTION_CANCEL = "CANCEL";

	public static final String ACTION_ADHOC = "ADHOC";

	public static final String STATUS_INITIATED = "INITIATED";

	public static final String STATUS_APPLIED = "APPLIED";

	public static final String STATUS_APPROVED = "APPROVED";

	public static final String STATUS_REJECTED = "REJECTED";

	public static final String STATUS_FIELDINSPECTION = "FIELDINSPECTION";

	public static final String STATUS_CANCELLED = "CANCELLED";

	public static final String STATUS_PAID = "PAID";

	public static final String BILL_AMOUNT_JSONPATH = "$.billResponse.Bill[0].totalAmount";

	public static final String MODULE = "rainmaker-tl";

	public static final String NOTIFICATION_LOCALE = "en_IN";

	public static final String NOTIFICATION_CREATE_CODE = "pt.property.en.create";

	public static final String NOTIFICATION_UPDATE_CODE = "pt.property.en.update";

	public static final String NOTIFICATION_EMPLOYEE_UPDATE_CODE = "pt.property.en.update.employee";

	public static final String NOTIFICATION_PAYMENT_ONLINE = "pt.payment.online";

	public static final String NOTIFICATION_PAYMENT_OFFLINE = "pt.payment.offline";

	public static final String NOTIFICATION_PAYMENT_FAIL = "pt.payment.fail";

	public static final String NOTIFICATION_OLDPROPERTYID_ABSENT = "pt.oldpropertyid.absent";

	public static final String ACTION_PAY = "PAY";

	public static final String USREVENTS_EVENT_TYPE = "SYSTEMGENERATED";
	public static final String USREVENTS_EVENT_NAME = "Property Tax";
	public static final String USREVENTS_EVENT_POSTEDBY = "SYSTEM-PT";

	// Variable names for diff

	public static final String VARIABLE_ACTION = "action";

	public static final String VARIABLE_WFDOCUMENTS = "wfDocuments";

	public static final String VARIABLE_ACTIVE = "active";

	public static final String VARIABLE_USERACTIVE = "status";

	public static final String VARIABLE_CREATEDBY = "createdBy";

	public static final String VARIABLE_LASTMODIFIEDBY = "lastModifiedBy";

	public static final String VARIABLE_CREATEDTIME = "createdTime";

	public static final String VARIABLE_LASTMODIFIEDTIME = "lastModifiedTime";

	public static final String VARIABLE_OWNER = "ownerInfo";

	public static final List<String> FIELDS_TO_IGNORE = Collections
			.unmodifiableList(Arrays.asList(VARIABLE_ACTION, VARIABLE_WFDOCUMENTS, VARIABLE_CREATEDBY,
					VARIABLE_LASTMODIFIEDBY, VARIABLE_CREATEDTIME, VARIABLE_LASTMODIFIEDTIME));

	public static final List<String> FIELDS_FOR_OWNER_MUTATION = Collections
			.unmodifiableList(Arrays.asList("name", "gender", "fatherOrHusbandName"));

	public static final List<String> FIELDS_FOR_PROPERTY_MUTATION = Collections.unmodifiableList(
			Arrays.asList("propertyType", "usageCategory", "ownershipCategory", "noOfFloors", "landArea"));

	// OwnershipTransfer ACTION
	public static final String ACTION_OT_SUBMIT = "SUBMIT";
	public static final String ACTION_OT_REJECT = "REJECT";
	public static final String ACTION_OT_SENDBACK = "SENDBACK";
	public static final String ACTION_OT_APPROVE = "APPROVE";
	public static final String ACTION_OT_PAY = "PAY";

	// OwnershipTransfer STATE
	public static final String STATE_OT_PENDING_CLARIFICATION = "PENDINGCLARIFICATION";
	public static final String STATE_OT_INITIATED = "INITIATED";
	public static final String STATE_OT_APPROVED = "APPROVED";

	// OwnershipTransfer Notifications
	public static final String NOTIFICATION_OT_SUBMIT = "rp.en.counter.submit";
	public static final String NOTIFICATION_OT_REJECTED = "rp.en.counter.rejected";
	public static final String NOTIFICATION_OT_SENDBACK = "rp.en.counter.sendback";
	public static final String NOTIFICATION_OT_PAYMENT = "rp.en.counter.payment";
	public static final String NOTIFICATION_OT_PAYMENT_SUCCESS = "rp.en.counter.paymentsuccess";
	public static final String NOTIFICATION_OT_APPROVED = "rp.en.counter.approved";
	public static final String OWNERSHIP_TRANSFER_APPLICATION = "Ownership Transfer request";
	public static final String DUPLICATE_COPY_APPLICATION = "Duplicate Copy request";
	public static final String MORTGAGE_APPLICATION = "Mortgage request";

	// ACTION_STATUS combinations for notification

	public static final String ACTION_STATUS_INITIATED = "INITIATE_INITIATED";
	public static final String ACTION_STATUS_SUBMIT = "SUBMIT_PENDINGCLVERIFICATION";
	public static final String ACTION_STATUS_REJECTED = "REJECT_REJECTED";
	public static final String ACTION_STATUS_SENDBACK = "SENDBACK_PENDINGCLARIFICATION";
	public static final String ACTION_STATUS_PAYMENT = "APPROVE_PENDINGPAYMENT";
	public static final String ACTION_STATUS_PAYMENT_SUCCESS = "PAY_APPROVED";
	public static final String ACTION_STATUS_APPROVED = "PAY_APPROVED";
	public static final String ACTION_STATUS_MORTGAGE_APPROVED = "APPROVE_APPROVED";

//    demand generation
	public static final String STATE_PENDING_SA_VERIFICATION = "PENDINGSAVERIFICATION";
	public static final String STATE_PENDING_APRO = "PENDINGAPRO";
	public static final String STATE_PENDING_GRANTDETAIL = "PENDINGGRANTDETAIL";

}
