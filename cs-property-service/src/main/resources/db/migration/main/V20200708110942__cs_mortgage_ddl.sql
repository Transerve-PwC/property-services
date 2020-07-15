DROP TABLE IF EXISTS cs_pt_mortgage_application;
DROP TABLE IF EXISTS cs_pt_mortgage_applicant;
DROP TABLE IF EXISTS cs_pt_mortgage_douments;
DROP TABLE IF EXISTS cs_pt_mortgage_grantdetails;
DROP TABLE IF EXISTS cs_pt_mortgage_application_audit;

--> Mortgage application table

CREATE TABLE cs_pt_mortgage_application (
   id           			CHARACTER VARYING (256) NOT NULL,
   propertyid       		CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   application_number		CHARACTER VARYING (64),
   state    				CHARACTER VARYING (256),
   action   				CHARACTER VARYING (256),
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   created_time         	bigint,
   modified_by     			CHARACTER VARYING (128),
   modified_time       		bigint,

  CONSTRAINT pk_cs_pt_mortgage_application PRIMARY KEY (id),
  CONSTRAINT fk_cs_pt_mortgage_application FOREIGN KEY (propertyid) REFERENCES cs_pt_property_v1 (id)  
);

--> mortgage applicant table
CREATE TABLE cs_pt_mortgage_applicant (
   id           		CHARACTER VARYING (256) NOT NULL,
   mortgage_id       	CHARACTER VARYING (256),
   tenantid			    CHARACTER VARYING (256),
   name       			CHARACTER VARYING (256),
   guardian				CHARACTER VARYING (256),
   relationship			CHARACTER VARYING (64),
   email       			CHARACTER VARYING (256),
   mobileno       		CHARACTER VARYING (256),
   address				CHARACTER VARYING (256),
   aadhaar_number       CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_time         bigint,
   modified_by     		CHARACTER VARYING (128),
   modified_time       	bigint,

  CONSTRAINT pk_cs_pt_mortgage_applicant PRIMARY KEY (id),
  CONSTRAINT fk_cs_pt_mortgage_applicant FOREIGN KEY (mortgage_id) REFERENCES cs_pt_mortgage_application (id)
);

--> Mortgage document table
CREATE TABLE cs_pt_mortgage_douments(
    id 					CHARACTER VARYING(64),
    tenantId 			CHARACTER VARYING(64),
    documentType 		CHARACTER VARYING(64),
    filestoreid 		CHARACTER VARYING(64),
    mortgage_id 		CHARACTER VARYING(64),
    active 				BOOLEAN,
    
    created_by 			CHARACTER VARYING(64),
    modified_by 		CHARACTER VARYING(64),
    created_time 		bigint,
    modified_time 		bigint,

    CONSTRAINT uk_cs_pt_mortgage_douments PRIMARY KEY (id),
    CONSTRAINT fk_cs_pt_mortgage_douments FOREIGN KEY (mortgage_id) REFERENCES cs_pt_mortgage_application (id)
);

CREATE TABLE cs_pt_mortgage_grantdetails(
    id 						CHARACTER VARYING(64),
    mortgage_id 			CHARACTER VARYING(64),
    tenantid 				CHARACTER VARYING(64),
    bank_name 				CHARACTER VARYING(64),
    mortgage_amount 		numeric(12,2),
    sanction_letter_number	CHARACTER VARYING(64),
    sanction_date 			bigint,
    
    created_by 				CHARACTER VARYING(64),
    modified_by 			CHARACTER VARYING(64),
    created_time 			bigint,
    modified_time 			bigint,

    CONSTRAINT pk_cs_pt_mortgage_grantdetails PRIMARY KEY (id),
    CONSTRAINT fk_cs_pt_mortgage_grantdetails FOREIGN KEY (mortgage_id) REFERENCES cs_pt_mortgage_application (id)
);

--> Mortgage application table

CREATE TABLE cs_pt_mortgage_application_audit (
   id           			CHARACTER VARYING (256) NOT NULL,
   propertyid       		CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   application_number		CHARACTER VARYING (64),
   state    				CHARACTER VARYING (256),
   action   				CHARACTER VARYING (256),
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   created_time         	bigint,
   modified_by     			CHARACTER VARYING (128),
   modified_time       		bigint
);





