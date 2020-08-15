DROP TABLE IF EXISTS cs_pm_application_v1;
DROP TABLE IF EXISTS cs_pm_applicant_v1;

CREATE TABLE cs_pm_application_v1 (
   id           			CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   property_id       		CHARACTER VARYING (256) NOT NULL,
   application_number   	CHARACTER VARYING (256) NOT NULL,
   branch_type				CHARACTER VARYING (256),
   module_type        		CHARACTER VARYING (256),
   application_type			CHARACTER VARYING (256),
   comments   				CHARACTER VARYING (256),
   hardcopy_received_date 	bigint,
   application_details      jsonb,
   state   					CHARACTER VARYING (256),
   action    				CHARACTER VARYING (256),
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   last_modified_by     	CHARACTER VARYING (128),
   created_time         	bigint NOT NULL,
   last_modified_time   	bigint,

  CONSTRAINT pk_cs_pm_application_v1 PRIMARY KEY (id, property_id),
--  CONSTRAINT uk_cs_pm_application_v1 UNIQUE (property_id),
  CONSTRAINT fk_cs_pm_application_v1 FOREIGN KEY (property_id) REFERENCES cs_pm_property_v1 (id)
);

CREATE TABLE cs_pm_applicant_v1 (
   id           			CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   property_id       		CHARACTER VARYING (256) NOT NULL,
   application_id   		CHARACTER VARYING (256) NOT NULL,
   mobile_number        	CHARACTER VARYING (256),
   first_name   			CHARACTER VARYING (256),
   last_name 				CHARACTER VARYING (256),
   user_id   				CHARACTER VARYING (256),
   applicant_details        jsonb,
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   last_modified_by     	CHARACTER VARYING (128),
   created_time         	bigint NOT NULL,
   last_modified_time   	bigint,

  CONSTRAINT pk_cs_pm_applicant_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pm_applicant_v1 FOREIGN KEY (property_id, application_id) REFERENCES cs_pm_application_v1 (property_id, id)
);
