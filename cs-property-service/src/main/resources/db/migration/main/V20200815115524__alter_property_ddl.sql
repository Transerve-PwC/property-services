ALTER TABLE cs_pt_property_v1
ADD COLUMN IF NOT EXISTS property_number CHARACTER VARYING (64);

ALTER TABLE cs_pt_property_audit_v1
ADD COLUMN IF NOT EXISTS property_number CHARACTER VARYING (64);

ALTER TABLE cs_pt_property_v1 DROP CONSTRAINT uk_cs_pt_property_v1;

ALTER TABLE cs_pt_propertydetails_v1 DROP CONSTRAINT uk_cs_pt_propertydetails_v1;

