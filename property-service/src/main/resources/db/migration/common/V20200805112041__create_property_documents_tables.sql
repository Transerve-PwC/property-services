DROP TABLE IF EXISTS cs_pm_documents_v1;

CREATE TABLE cs_pm_documents_v1 (
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

  CONSTRAINT pk_cs_pm_documents_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pm_documents_v1 FOREIGN KEY (property_id) REFERENCES cs_pm_property_v1 (id)
);
