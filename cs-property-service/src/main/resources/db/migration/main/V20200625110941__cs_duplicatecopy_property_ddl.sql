DROP TABLE IF EXISTS cs_pt_duplicatecopy_property_v1;
DROP TABLE IF EXISTS cs_pt_duplicatecopy_applicant_v1;
DROP TABLE IF EXISTS cs_pt_duplicate_ownership_application;
DROP TABLE IF EXISTS cs_pt_duplicatecopy_property_audit_v1;
DROP TABLE IF EXISTS cs_pt_duplicatecopy_applicant_audit_v1;

--> Duplicate copy Property table

CREATE TABLE cs_pt_duplicatecopy_property_v1 (
   id           			CHARACTER VARYING (256) NOT NULL,
   transit_number       	CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   state    				CHARACTER VARYING (256),
   action   				CHARACTER VARYING (256),
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   created_time         	bigint,
   modified_by     			CHARACTER VARYING (128),
   modified_time       		bigint,

  CONSTRAINT pk_cs_pt_duplicatecopy_property_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pt_duplicatecopy_property_v1 FOREIGN KEY (transit_number) REFERENCES cs_pt_property_v1 (transit_number)  
);

--> Duplicate copy applicant details table
CREATE TABLE cs_pt_duplicatecopy_applicant_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   property_id       	CHARACTER VARYING (256),
   tenantid			    CHARACTER VARYING (256),
   name       			CHARACTER VARYING (256),
   email       			CHARACTER VARYING (256),
   mobileno       		CHARACTER VARYING (256),
   guardian				CHARACTER VARYING (256),
   relationship			CHARACTER VARYING (256),
   address				CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_time         bigint,
   modified_by     		CHARACTER VARYING (128),
   modified_time       	bigint,

  CONSTRAINT pk_cs_pt_duplicatecopy_applicant_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pt_duplicatecopy_applicant_v1 FOREIGN KEY (property_id) REFERENCES cs_pt_duplicatecopy_property_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

--> Duplicate copy document table
CREATE TABLE cs_pt_duplicate_ownership_application(
    id 					CHARACTER VARYING(64),
    tenantId 			CHARACTER VARYING(64),
    documentType 		CHARACTER VARYING(64),
    filestoreid 		CHARACTER VARYING(64),
    propertydetailid 	CHARACTER VARYING(64),
    active 				BOOLEAN,
    createdBy 			CHARACTER VARYING(64),
    lastModifiedBy 		CHARACTER VARYING(64),
    created_time 		bigint,
    modified_time 		bigint,

    CONSTRAINT uk_cs_pt_duplicate_ownership_application PRIMARY KEY (id),
    CONSTRAINT fk_cs_pt_duplicate_ownership_application FOREIGN KEY (propertydetailid) REFERENCES cs_pt_propertydetails_v1 (id)
);

CREATE TABLE cs_pt_duplicatecopy_property_audit_v1 (
   id           			CHARACTER VARYING (256) NOT NULL,
   transit_number       	CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   state    				CHARACTER VARYING (256),
   action   				CHARACTER VARYING (256),
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   created_time         	bigint,
   modified_by     			CHARACTER VARYING (128),
   modified_time       		bigint
);

CREATE TABLE cs_pt_duplicatecopy_applicant_audit_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   property_id       	CHARACTER VARYING (256),
   tenantid			    CHARACTER VARYING (256),
   name       			CHARACTER VARYING (256),
   email       			CHARACTER VARYING (256),
   mobileno       			CHARACTER VARYING (256),
   guardian				CHARACTER VARYING (256),
   relationship			CHARACTER VARYING (256),
   address				CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_time         bigint,
   modified_by     		CHARACTER VARYING (128),
   modified_time       	bigint
);




