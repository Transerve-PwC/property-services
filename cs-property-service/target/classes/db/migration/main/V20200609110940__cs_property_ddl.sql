DROP TABLE IF EXISTS cs_pt_property_v1;
DROP TABLE IF EXISTS cs_pt_propertydetails_v1;
DROP TABLE IF EXISTS cs_pt_ownership_v1;
DROP TABLE IF EXISTS cs_pt_address_v1;
DROP TABLE IF EXISTS cs_pt_property_audit_v1;
DROP TABLE IF EXISTS cs_pt_propertydetails_audit_v1;
DROP TABLE IF EXISTS cs_pt_ownership_audit_v1;

--> Property table

CREATE TABLE cs_pt_property_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   transit_number       CHARACTER VARYING (256) NOT NULL,
   colony           	CHARACTER VARYING (256),
   master_data_state    CHARACTER VARYING (256),
   master_data_action   CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_date         CHARACTER VARYING NOT NULL,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	CHARACTER VARYING NOT NULL,

  CONSTRAINT pk_cs_pt_property_v1 PRIMARY KEY (id),
  CONSTRAINT uk_cs_pt_property_v1 UNIQUE (transit_number)
);

CREATE TABLE cs_pt_propertydetails_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   property_id       	CHARACTER VARYING (256),
   transit_number       CHARACTER VARYING (256) NOT NULL,
   area           		CHARACTER VARYING (256),
   rent_per_sqyd        CHARACTER VARYING (256),
   current_owner        CHARACTER VARYING (256),
   floors           	CHARACTER VARYING (256),
   additional_details   CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_date         CHARACTER VARYING NOT NULL,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	CHARACTER VARYING NOT NULL,

  CONSTRAINT pk_cs_pt_propertydetails_v1 PRIMARY KEY (id),
  CONSTRAINT uk_cs_pt_propertydetails_v1 UNIQUE (transit_number),
  CONSTRAINT fk_cs_pt_propertydetails_v1 FOREIGN KEY (property_id) REFERENCES cs_pt_property_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE cs_pt_ownership_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   property_id       	CHARACTER VARYING (256),
   owner_id       		CHARACTER VARYING (256),
   name       			CHARACTER VARYING (256),
   email       			CHARACTER VARYING (256),
   phone       			CHARACTER VARYING (256),
   gender       		CHARACTER VARYING (256),
   date_of_birth       	CHARACTER VARYING (256),
   aadhaar_number       CHARACTER VARYING (256),
   allotment_startdate  CHARACTER VARYING (256),
   allotment_enddate    CHARACTER VARYING (256),
   posession_startdate  CHARACTER VARYING (256),
   posession_enddate    CHARACTER VARYING (256),
   allotmen_number   	CHARACTER VARYING (256),
   application_status   CHARACTER VARYING (256),
   active_state         CHARACTER VARYING (256),
   is_primary_owner   	CHARACTER VARYING (256),
   monthly_rent         CHARACTER VARYING (256),
   revision_period      CHARACTER VARYING (256),
   revision_percentage  CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_date         CHARACTER VARYING NOT NULL,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	CHARACTER VARYING NOT NULL,

  CONSTRAINT pk_cs_pt_ownership_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pt_ownership_v1 FOREIGN KEY (property_id) REFERENCES cs_pt_property_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE cs_pt_address_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   property_id       	CHARACTER VARYING (256),
   transit_number       CHARACTER VARYING (256),
   colony             	CHARACTER VARYING (256),
   area            		CHARACTER VARYING (256),
   district           	CHARACTER VARYING (256),
   state       			CHARACTER VARYING (256),
   country           	CHARACTER VARYING (256),
   pincode             	CHARACTER VARYING (256),
   landmark            	CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_date         CHARACTER VARYING NOT NULL,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	CHARACTER VARYING NOT NULL,

  CONSTRAINT pk_cs_pt_address_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pt_address_v1 FOREIGN KEY (property_id) REFERENCES cs_pt_property_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE cs_pt_property_audit_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   transit_number       CHARACTER VARYING (256) NOT NULL,
   colony           	CHARACTER VARYING (256),
   state             	CHARACTER VARYING (256),
   action            	CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_date         CHARACTER VARYING NOT NULL,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	CHARACTER VARYING NOT NULL
);

CREATE TABLE cs_pt_propertydetails_audit_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   property_id       	CHARACTER VARYING (256),
   transit_number       CHARACTER VARYING (256) NOT NULL,
   area           		CHARACTER VARYING (256),
   rent_per_sqyd        CHARACTER VARYING (256),
   current_owner        CHARACTER VARYING (256),
   floors           	CHARACTER VARYING (256),
   additional_details   CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_date         CHARACTER VARYING NOT NULL,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	CHARACTER VARYING NOT NULL
);

CREATE TABLE cs_pt_ownership_audit_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   property_id       	CHARACTER VARYING (256),
   owner_id       		CHARACTER VARYING (256),
   allotment_startdate  CHARACTER VARYING (256),
   allotment_enddate    CHARACTER VARYING (256),
   posession_startdate  CHARACTER VARYING (256),
   posession_enddate    CHARACTER VARYING (256),
   allotmen_number   	CHARACTER VARYING (256),
   application_status   CHARACTER VARYING (256),
   active_state         CHARACTER VARYING (256),
   is_primary_owner   	CHARACTER VARYING (256),
   monthly_rent         CHARACTER VARYING (256),
   revision_period      CHARACTER VARYING (256),
   revision_percentage  CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_date         CHARACTER VARYING NOT NULL,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	CHARACTER VARYING NOT NULL
);