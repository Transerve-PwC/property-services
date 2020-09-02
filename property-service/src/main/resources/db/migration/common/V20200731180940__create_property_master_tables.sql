DROP TABLE IF EXISTS cs_ep_property_v1;
DROP TABLE IF EXISTS cs_ep_property_details_v1;
DROP TABLE IF EXISTS cs_ep_owner_v1;
DROP TABLE IF EXISTS cs_ep_owner_details_v1;
DROP TABLE IF EXISTS cs_ep_documents_v1;
DROP TABLE IF EXISTS cs_ep_court_case_v1;

DROP TABLE IF EXISTS cs_ep_property_audit_v1;
DROP TABLE IF EXISTS cs_ep_property_details_audit_v1;
DROP TABLE IF EXISTS cs_ep_owner_audit_v1;
DROP TABLE IF EXISTS cs_ep_owner_details_audit_v1;

--> Property tables

CREATE TABLE cs_ep_property_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   file_number			CHARACTER VARYING (256) NOT NULL,
   category           	CHARACTER VARYING (256),
   sub_category    		CHARACTER VARYING (256),
   site_number   		CHARACTER VARYING (256),
   sector_number        CHARACTER VARYING (256),
   state    			CHARACTER VARYING (256),
   action   			CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         bigint NOT NULL,
   last_modified_time   bigint,

  CONSTRAINT pk_cs_ep_property_v1 PRIMARY KEY (id)
);

CREATE TABLE cs_ep_property_details_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   property_id       	CHARACTER VARYING (256) NOT NULL,
   property_type		CHARACTER VARYING (256),
   type_of_allocation   CHARACTER VARYING (256),
   mode_of_auction      CHARACTER VARYING (256),
   scheme_name        	CHARACTER VARYING (256),
   date_of_auction      bigint,
   area_sqft   			numeric(15,2),
   rate_per_sqft        numeric(15,2),
   last_noc_date        bigint,
   service_category   	CHARACTER VARYING (256),
   is_property_active	BOOLEAN,
   trade_type			CHARACTER VARYING (256),
   company_name			CHARACTER VARYING (256),
   company_address		CHARACTER VARYING (256),
   company_registration_number	CHARACTER VARYING (256),
   company_type			CHARACTER VARYING (256),

   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         bigint NOT NULL,
   last_modified_time   bigint,

  CONSTRAINT pk_cs_ep_property_details_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_ep_property_details_v1 FOREIGN KEY (property_id) REFERENCES cs_ep_property_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE cs_ep_owner_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   property_details_id	CHARACTER VARYING (256) NOT NULL,
   serial_number   		CHARACTER VARYING (256),
   share   				numeric(3,2),
   cp_number         	CHARACTER VARYING (256),
   state   				CHARACTER VARYING (256),
   action   			CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         bigint NOT NULL,
   last_modified_time   bigint,

  CONSTRAINT pk_cs_ep_owner_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_ep_owner_v1 FOREIGN KEY (property_details_id) REFERENCES cs_ep_property_details_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE cs_ep_owner_details_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   owner_id       		CHARACTER VARYING (256) NOT NULL,
   owner_name			CHARACTER VARYING (256),
   guardian_name       	CHARACTER VARYING (256),
   guardian_relation    CHARACTER VARYING (256),
   mobile_number       	CHARACTER VARYING (256),
   allotment_number     CHARACTER VARYING (256),
   date_of_allotment    bigint,
   possesion_date       bigint,
   is_approved			BOOLEAN,
   is_current_owner  	BOOLEAN,
   is_master_entry    	BOOLEAN,
   due_amount  			numeric(12,2),
   address    			CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         bigint NOT NULL,
   last_modified_time   bigint,

  CONSTRAINT pk_cs_ep_owner_details_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_ep_owner_details_v1 FOREIGN KEY (owner_id) REFERENCES cs_ep_owner_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE cs_ep_documents_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid			    CHARACTER VARYING (256),
   reference_id       	CHARACTER VARYING (256) NOT NULL,
   document_type   		CHARACTER VARYING (256),
   file_store_id        CHARACTER VARYING (256),
   is_active   			BOOLEAN,
   property_id    		CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         bigint NOT NULL,
   last_modified_time   bigint,

  CONSTRAINT pk_cs_ep_documents_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_ep_documents_v1 FOREIGN KEY (property_id) REFERENCES cs_ep_property_v1 (id)
);


CREATE TABLE cs_ep_court_case_v1 (
   id           				CHARACTER VARYING (256) NOT NULL,
   tenantid       				CHARACTER VARYING (256),
   property_details_id			CHARACTER VARYING (256) NOT NULL,
   estate_officer_court 		CHARACTER VARYING (256),
   commissioners_court  		CHARACTER VARYING (256),
   chief_administartors_court   CHARACTER VARYING (256),
   advisor_to_admin_court   	CHARACTER VARYING (256),
   honorable_district_court   	CHARACTER VARYING (256),
   honorable_high_court         CHARACTER VARYING (256),
   honorable_supreme_court   	CHARACTER VARYING (256),
  
   created_by           		CHARACTER VARYING (128) NOT NULL,
   last_modified_by     		CHARACTER VARYING (128),
   created_time         		bigint NOT NULL,
   last_modified_time   		bigint,

  CONSTRAINT pk_cs_ep_court_case_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_ep_court_case_v1 FOREIGN KEY (property_details_id) REFERENCES cs_ep_property_details_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);


--> Property audit tables


CREATE TABLE cs_ep_property_audit_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   file_number			CHARACTER VARYING (256) NOT NULL,
   category           	CHARACTER VARYING (256),
   sub_category    		CHARACTER VARYING (256),
   site_number   		CHARACTER VARYING (256),
   sector_number        CHARACTER VARYING (256),
   state    			CHARACTER VARYING (256),
   action   			CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         bigint NOT NULL,
   last_modified_time   bigint
);

CREATE TABLE cs_ep_property_details_audit_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   property_id       	CHARACTER VARYING (256) NOT NULL,
   property_type		CHARACTER VARYING (256),
   type_of_allocation   CHARACTER VARYING (256),
   mode_of_auction      CHARACTER VARYING (256),
   scheme_name        	CHARACTER VARYING (256),
   date_of_auction      bigint,
   area_sqft   			numeric(15,2),
   rate_per_sqft        numeric(15,2),
   last_noc_date        bigint,
   service_category   	CHARACTER VARYING (256),
   is_property_active	BOOLEAN,
   trade_type			CHARACTER VARYING (256),
   company_name			CHARACTER VARYING (256),
   company_address		CHARACTER VARYING (256),
   company_registration_number	CHARACTER VARYING (256),
   company_type			CHARACTER VARYING (256),

   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         bigint NOT NULL,
   last_modified_time   bigint
);

CREATE TABLE cs_ep_owner_audit_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   property_details_id	CHARACTER VARYING (256) NOT NULL,
   serial_number   		CHARACTER VARYING (256),
   share   				numeric(3,2),
   cp_number         	CHARACTER VARYING (256),
   state   				CHARACTER VARYING (256),
   action   			CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         bigint NOT NULL,
   last_modified_time   bigint
);

CREATE TABLE cs_ep_owner_details_audit_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   owner_id       		CHARACTER VARYING (256) NOT NULL,
   owner_name			CHARACTER VARYING (256),
   guardian_name       	CHARACTER VARYING (256),
   guardian_relation    CHARACTER VARYING (256),
   mobile_number       	CHARACTER VARYING (256),
   allotment_number     CHARACTER VARYING (256),
   date_of_allotment    bigint,
   possesion_date       bigint,
   is_approved			BOOLEAN,
   is_current_owner  	BOOLEAN,
   is_master_entry    	BOOLEAN,
   due_amount  			numeric(12,2),
   address    			CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         bigint NOT NULL,
   last_modified_time   bigint
);

