ALTER TABLE cs_pt_property_images_application
ADD COLUMN IF NOT EXISTS capturedBy CHARACTER VARYING (256);

ALTER TABLE cs_pt_property_images_application_audit
ADD COLUMN IF NOT EXISTS capturedBy CHARACTER VARYING (256);


ALTER TABLE cs_pt_notice_generation_application
ADD COLUMN IF NOT EXISTS property_image_id CHARACTER VARYING (256);

ALTER TABLE cs_pt_notice_generation_application_audit
ADD COLUMN IF NOT EXISTS property_image_id CHARACTER VARYING (256);

ALTER TABLE cs_pt_notice_generation_application
ADD CONSTRAINT fk_cs_pt_notice_generation_piid FOREIGN KEY (property_image_id) REFERENCES cs_pt_property_images_application (id);