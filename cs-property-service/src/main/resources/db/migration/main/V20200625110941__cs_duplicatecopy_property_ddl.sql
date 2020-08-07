DROP TABLE IF EXISTS cs_pt_duplicate_ownership_application;
DROP TABLE IF EXISTS cs_pt_duplicatecopy_applicant;
DROP TABLE IF EXISTS cs_pt_duplicate_ownership_application_audit;
DROP TABLE IF EXISTS cs_pt_duplicatecopy_applicant_audit;

--> Duplicate copy Property table

CREATE TABLE cs_pt_duplicate_ownership_application (
   id           			CHARACTER VARYING (256) NOT NULL,
   property_id       	    CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   state    				CHARACTER VARYING (256),
   action   				CHARACTER VARYING (256),
   application_number   	CHARACTER VARYING(64),
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   created_time         	bigint,
   modified_by     			CHARACTER VARYING (128),
   modified_time       		bigint,

  CONSTRAINT pk_cs_pt_duplicate_ownership_application PRIMARY KEY (id),
  CONSTRAINT fk_cs_pt_duplicate_ownership_application FOREIGN KEY (property_id) REFERENCES cs_pt_property_v1 (id)  
);

--> Duplicate copy applicant details table
CREATE TABLE cs_pt_duplicatecopy_applicant (
   id           		CHARACTER VARYING (256) NOT NULL,
   application_id       CHARACTER VARYING (256),
   tenantid			    CHARACTER VARYING (256),
   name       			CHARACTER VARYING (256),
   email       			CHARACTER VARYING (256),
   mobileno       		CHARACTER VARYING (256),
   guardian				CHARACTER VARYING (256),
   relationship			CHARACTER VARYING (256),
   aadhaar_number       CHARACTER VARYING (256),
   fee_amount			INTEGER,
   apro_charge			INTEGER,
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_time         bigint,
   modified_by     		CHARACTER VARYING (128),
   modified_time       	bigint,

  CONSTRAINT pk_cs_pt_duplicatecopy_applicant PRIMARY KEY (id),
  CONSTRAINT fk_cs_pt_duplicatecopy_applicant FOREIGN KEY (application_id) REFERENCES cs_pt_duplicate_ownership_application (id)
);


CREATE TABLE cs_pt_duplicate_ownership_application_audit(
   id           			CHARACTER VARYING (256) NOT NULL,
   property_id		       	CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   state    				CHARACTER VARYING (256),
   action   				CHARACTER VARYING (256),
   application_number   	CHARACTER VARYING(64),
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   created_time         	bigint,
   modified_by     			CHARACTER VARYING (128),
   modified_time       		bigint
);

CREATE TABLE cs_pt_duplicatecopy_applicant_audit (
   id           		CHARACTER VARYING (256) NOT NULL,
   application_id       CHARACTER VARYING (256),
   tenantid			    CHARACTER VARYING (256),
   name       			CHARACTER VARYING (256),
   email       			CHARACTER VARYING (256),
   mobileno       		CHARACTER VARYING (256),
   guardian				CHARACTER VARYING (256),
   relationship			CHARACTER VARYING (256),
   aadhaar_number       CHARACTER VARYING (256),
   fee_amount			INTEGER,
   apro_charge			INTEGER,
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_time         bigint,
   modified_by     		CHARACTER VARYING (128),
   modified_time       	bigint
);




