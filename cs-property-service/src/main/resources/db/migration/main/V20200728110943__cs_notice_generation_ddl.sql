DROP TABLE IF EXISTS cs_pt_notice_generation_application;
DROP TABLE IF EXISTS cs_pt_notice_generation_application_audit;

--> Notice Generation application table

CREATE TABLE cs_pt_notice_generation_application (
   id           			CHARACTER VARYING (256) NOT NULL,
   propertyid       		CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   memo_number				CHARACTER VARYING (64),
   memo_date				bigint,
   notice_type				CHARACTER VARYING (64),
   guardian 				CHARACTER VARYING (256),
   relationship 			CHARACTER VARYING (256),
   violations 				CHARACTER VARYING (500),
   description				CHARACTER VARYING (256),
   demand_notice_from		bigint,
   demand_notice_to			bigint,
   recovery_type			CHARACTER VARYING (256),
   amount					numeric(12,2),
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   created_time         	bigint,
   modified_by     			CHARACTER VARYING (128),
   modified_time       		bigint,

  CONSTRAINT pk_cs_pt_notice_generation_application PRIMARY KEY (id),
  CONSTRAINT fk_cs_pt_notice_generation_application FOREIGN KEY (propertyid) REFERENCES cs_pt_property_v1 (id)  
);

--> Notice Generation document table
CREATE TABLE cs_pt_notice_douments(
    id 					CHARACTER VARYING(64),
    tenantId 			CHARACTER VARYING(64),
    documentType 		CHARACTER VARYING(64),
    filestoreid 		CHARACTER VARYING(64),
    notice_id 			CHARACTER VARYING(64),
    active 				BOOLEAN,
    
    created_by 			CHARACTER VARYING(64),
    modified_by 		CHARACTER VARYING(64),
    created_time 		bigint,
    modified_time 		bigint,

    CONSTRAINT uk_cs_pt_notice_douments PRIMARY KEY (id),
    CONSTRAINT fk_cs_pt_notice_douments FOREIGN KEY (notice_id) REFERENCES cs_pt_notice_generation_application (id)
);



--> Notice Generation audit table

CREATE TABLE cs_pt_notice_generation_application_audit (
   id           			CHARACTER VARYING (256) NOT NULL,
   propertyid       		CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   application_number		CHARACTER VARYING (64),
   notice_type				CHARACTER VARYING (64),
   guardian 				CHARACTER VARYING (256),
   relationship 			CHARACTER VARYING (256),
   violations 				CHARACTER VARYING (500),
   description				CHARACTER VARYING (256),
   demand_notice_from		bigint,
   demand_notice_to			bigint,
   recovery_type			CHARACTER VARYING (256),
   amount					numeric(12,2),
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   created_time         	bigint,
   modified_by     			CHARACTER VARYING (128),
   modified_time       		bigint
);





