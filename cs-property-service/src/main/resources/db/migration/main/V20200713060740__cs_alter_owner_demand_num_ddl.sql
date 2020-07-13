ALTER TABLE cs_pt_ownershipdetails_v1
ALTER COLUMN due_amount TYPE INT USING due_amount::integer;

ALTER TABLE cs_pt_ownershipdetails_v1
ALTER COLUMN apro_charge TYPE INT USING apro_charge::integer;


ALTER TABLE cs_pt_ownershipdetails_audit_v1
ALTER COLUMN due_amount TYPE INT USING due_amount::integer;

ALTER TABLE cs_pt_ownershipdetails_audit_v1
ALTER COLUMN apro_charge TYPE INT USING apro_charge::integer;
