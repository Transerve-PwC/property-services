DROP TABLE IF EXISTS cs_pm_property_v1;
DROP TABLE IF EXISTS cs_pm_property_details_v1;
DROP TABLE IF EXISTS cs_pm_owner_v1;
DROP TABLE IF EXISTS cs_pm_owner_details_v1;
DROP TABLE IF EXISTS cs_pm_owner_documents_v1;
DROP TABLE IF EXISTS cs_pm_court_case_v1;
DROP TABLE IF EXISTS cs_pm_purchase_details_v1;

DROP TABLE IF EXISTS cs_pm_property_audit_v1;
DROP TABLE IF EXISTS cs_pm_property_details_audit_v1;
DROP TABLE IF EXISTS cs_pm_owner_audit_v1;
DROP TABLE IF EXISTS cs_pm_owner_details_audit_v1;

--> Property tables

CREATE TABLE cs_pm_property_v1 (
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
   created_time         CHARACTER VARYING NOT NULL,
   last_modified_time   CHARACTER VARYING,

  CONSTRAINT pk_cs_pm_property_v1 PRIMARY KEY (id)
);

CREATE TABLE cs_pm_property_details_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   property_id       	CHARACTER VARYING (256) NOT NULL,
   property_type		CHARACTER VARYING (256),
   type_of_allocation   CHARACTER VARYING (256),
   mode_of_auction      CHARACTER VARYING (256),
   scheme_name        	CHARACTER VARYING (256),
   date_of_auction      CHARACTER VARYING (256),
   area_sqft   			CHARACTER VARYING (256),
   rate_per_sqft        CHARACTER VARYING (256),
   last_noc_date        CHARACTER VARYING (256),
   service_category   	CHARACTER VARYING (256),
   
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         CHARACTER VARYING NOT NULL,
   last_modified_time   CHARACTER VARYING,

  CONSTRAINT pk_cs_pm_property_details_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pm_property_details_v1 FOREIGN KEY (property_id) REFERENCES cs_pm_property_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE cs_pm_owner_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   property_details_id	CHARACTER VARYING (256) NOT NULL,
   serial_number   		CHARACTER VARYING (256),
   share   				CHARACTER VARYING (256),
   cp_number         	CHARACTER VARYING (256),
   state   				CHARACTER VARYING (256),
   action   			CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         CHARACTER VARYING NOT NULL,
   last_modified_time   CHARACTER VARYING,

  CONSTRAINT pk_cs_pm_owner_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pm_owner_v1 FOREIGN KEY (property_details_id) REFERENCES cs_pm_property_details_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE cs_pm_owner_details_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   owner_id       		CHARACTER VARYING (256) NOT NULL,
   owner_name			CHARACTER VARYING (256),
   guardian_name       	CHARACTER VARYING (256),
   guardian_relation    CHARACTER VARYING (256),
   mobile_number       	CHARACTER VARYING (256),
   allotment_number     CHARACTER VARYING (256),
   date_of_allotment    CHARACTER VARYING (256),
   possesion_date       CHARACTER VARYING (256),
   is_current_owner  	CHARACTER VARYING (256),
   is_master_entry    	CHARACTER VARYING (256),
   due_amount  			CHARACTER VARYING (256),
   address    			CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         CHARACTER VARYING NOT NULL,
   last_modified_time   CHARACTER VARYING,

  CONSTRAINT pk_cs_pm_owner_details_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pm_owner_details_v1 FOREIGN KEY (owner_id) REFERENCES cs_pm_owner_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE cs_pm_owner_documents_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   owner_details_id     CHARACTER VARYING (256) NOT NULL,
   document_type		CHARACTER VARYING (256),
   file_store_id        CHARACTER VARYING (256),
   is_active            CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         CHARACTER VARYING NOT NULL,
   last_modified_time   CHARACTER VARYING,

  CONSTRAINT pk_cs_pm_owner_documents_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pm_owner_documents_v1 FOREIGN KEY (owner_details_id) REFERENCES cs_pm_owner_details_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE cs_pm_court_case_v1 (
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
   created_time         		CHARACTER VARYING NOT NULL,
   last_modified_time   		CHARACTER VARYING,

  CONSTRAINT pk_cs_pm_court_case_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pm_court_case_v1 FOREIGN KEY (property_details_id) REFERENCES cs_pm_property_details_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE cs_pm_purchase_details_v1 (
   id           				CHARACTER VARYING (256) NOT NULL,
   tenantid       				CHARACTER VARYING (256),
   property_details_id			CHARACTER VARYING (256) NOT NULL,
   new_owner_name 				CHARACTER VARYING (256),
   new_owner_father_name  		CHARACTER VARYING (256),
   new_owner_address   			CHARACTER VARYING (256),
   new_owner_mobile_number   	CHARACTER VARYING (256),
   seller_name   				CHARACTER VARYING (256),
   seller_father_name         	CHARACTER VARYING (256),
   percentage_of_share   		CHARACTER VARYING (256),
   mode_of_transfer   			CHARACTER VARYING (256),
   registration_number         	CHARACTER VARYING (256),
   date_of_registration   		CHARACTER VARYING (256),
  
   created_by           		CHARACTER VARYING (128) NOT NULL,
   last_modified_by     		CHARACTER VARYING (128),
   created_time         		CHARACTER VARYING NOT NULL,
   last_modified_time   		CHARACTER VARYING,

  CONSTRAINT pk_cs_pm_purchase_details_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pm_purchase_details_v1 FOREIGN KEY (property_details_id) REFERENCES cs_pm_property_details_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

--> Property audit tables


CREATE TABLE cs_pm_property_audit_v1 (
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
   created_time         CHARACTER VARYING NOT NULL,
   last_modified_time   CHARACTER VARYING
);

CREATE TABLE cs_pm_property_details_audit_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   property_id       	CHARACTER VARYING (256) NOT NULL,
   property_type		CHARACTER VARYING (256),
   type_of_allocation   CHARACTER VARYING (256),
   mode_of_auction      CHARACTER VARYING (256),
   scheme_name        	CHARACTER VARYING (256),
   date_of_auction      CHARACTER VARYING (256),
   area_sqft   			CHARACTER VARYING (256),
   rate_per_sqft        CHARACTER VARYING (256),
   last_noc_date        CHARACTER VARYING (256),
   service_category   	CHARACTER VARYING (256),
   
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         CHARACTER VARYING NOT NULL,
   last_modified_time   CHARACTER VARYING
);

CREATE TABLE cs_pm_owner_audit_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   property_details_id	CHARACTER VARYING (256) NOT NULL,
   serial_number   		CHARACTER VARYING (256),
   share   				CHARACTER VARYING (256),
   cp_number         	CHARACTER VARYING (256),
   state   				CHARACTER VARYING (256),
   action   			CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         CHARACTER VARYING NOT NULL,
   last_modified_time   CHARACTER VARYING
);

CREATE TABLE cs_pm_owner_details_audit_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   tenantid       		CHARACTER VARYING (256),
   owner_id       		CHARACTER VARYING (256) NOT NULL,
   owner_name			CHARACTER VARYING (256),
   guardian_name       	CHARACTER VARYING (256),
   guardian_relation    CHARACTER VARYING (256),
   mobile_number       	CHARACTER VARYING (256),
   allotment_number     CHARACTER VARYING (256),
   date_of_allotment    CHARACTER VARYING (256),
   possesion_date       CHARACTER VARYING (256),
   is_current_owner  	CHARACTER VARYING (256),
   is_master_entry    	CHARACTER VARYING (256),
   due_amount  			CHARACTER VARYING (256),
   address    			CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   last_modified_by     CHARACTER VARYING (128),
   created_time         CHARACTER VARYING NOT NULL,
   last_modified_time   CHARACTER VARYING
);

