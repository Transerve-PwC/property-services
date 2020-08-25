ALTER TABLE cs_pt_propertydetails_v1 
ADD COLUMN IF NOT EXISTS interest_rate numeric(4,2);

ALTER TABLE cs_pt_propertydetails_v1 
ADD COLUMN IF NOT EXISTS rent_increment_percentage numeric(4,2);

ALTER TABLE cs_pt_propertydetails_v1 
ADD COLUMN IF NOT EXISTS rent_increment_period int;

--Audit table
ALTER TABLE cs_pt_propertydetails_audit_v1 
ADD COLUMN IF NOT EXISTS interest_rate numeric(4,2);

ALTER TABLE cs_pt_propertydetails_audit_v1 
ADD COLUMN IF NOT EXISTS rent_increment_percentage numeric(4,2);

ALTER TABLE cs_pt_propertydetails_audit_v1 
ADD COLUMN IF NOT EXISTS rent_increment_period int;



