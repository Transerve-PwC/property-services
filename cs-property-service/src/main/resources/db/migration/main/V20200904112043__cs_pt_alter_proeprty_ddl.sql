ALTER TABLE cs_pt_property_v1 
ADD COLUMN IF NOT EXISTS payment_amount numeric(12,2);

ALTER TABLE cs_pt_property_v1 
ADD COLUMN IF NOT EXISTS rent_payment_consumer_code CHARACTER VARYING (256);

ALTER TABLE cs_pt_property_v1 
ADD COLUMN IF NOT EXISTS transaction_id CHARACTER VARYING (256);

ALTER TABLE cs_pt_property_v1 
ADD COLUMN IF NOT EXISTS bank_name CHARACTER VARYING (256);

--Audit table
ALTER TABLE cs_pt_property_audit_v1 
ADD COLUMN IF NOT EXISTS payment_amount numeric(12,2);

ALTER TABLE cs_pt_property_audit_v1 
ADD COLUMN IF NOT EXISTS rent_payment_consumer_code CHARACTER VARYING (256);

ALTER TABLE cs_pt_property_audit_v1 
ADD COLUMN IF NOT EXISTS transaction_id CHARACTER VARYING (256);

ALTER TABLE cs_pt_property_audit_v1 
ADD COLUMN IF NOT EXISTS bank_name CHARACTER VARYING (256);