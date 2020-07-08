DROP TABLE IF EXISTS cs_pt_ot_documents_v1;

CREATE TABLE cs_pt_ot_documents_v1 (
   id           		CHARACTER VARYING (256) NOT NULL,
   owner_id       		CHARACTER VARYING (256) NOT NULL,
   tenantid			    CHARACTER VARYING (256),
   is_active   			CHARACTER VARYING (256),
   document_type   		CHARACTER VARYING (256),
   fileStore_id         CHARACTER VARYING (256),
   document_uid   		CHARACTER VARYING (256),
  
   created_by           CHARACTER VARYING (128) NOT NULL,
   created_date         CHARACTER VARYING NOT NULL,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	CHARACTER VARYING,

  CONSTRAINT pk_cs_pt_ot_documents_v1 PRIMARY KEY (id)
);
