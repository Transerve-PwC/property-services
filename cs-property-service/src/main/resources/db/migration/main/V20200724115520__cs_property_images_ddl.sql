DROP TABLE IF EXISTS cs_pt_property_images_application;
DROP TABLE IF EXISTS cs_pt_property_images_application_audit;

--> Property images application table

CREATE TABLE cs_pt_property_images_application (
   id           			CHARACTER VARYING (256) NOT NULL,
   propertyid       		CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   application_number		CHARACTER VARYING (64),
   description				CHARACTER VARYING (1000),
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   created_time         	bigint,
   modified_by     			CHARACTER VARYING (128),
   modified_time       		bigint,

  CONSTRAINT pk_cs_pt_property_images_application PRIMARY KEY (id),
  CONSTRAINT fk_cs_pt_property_images_application FOREIGN KEY (propertyid) REFERENCES cs_pt_property_v1 (id)  
);

--> Property images application audit table

CREATE TABLE cs_pt_property_images_application_audit (
   id           			CHARACTER VARYING (256) NOT NULL,
   propertyid       		CHARACTER VARYING (256) NOT NULL,
   tenantid			    	CHARACTER VARYING (256),
   application_number		CHARACTER VARYING (64),
   description				CHARACTER VARYING (1000),
  
   created_by           	CHARACTER VARYING (128) NOT NULL,
   created_time         	bigint,
   modified_by     			CHARACTER VARYING (128),
   modified_time       		bigint
);





