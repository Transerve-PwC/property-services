ALTER TABLE cs_pt_ownershipdetails_v1
ADD COLUMN due_amount CHARACTER VARYING (256);

ALTER TABLE cs_pt_ownershipdetails_v1
ADD COLUMN apro_charge CHARACTER VARYING (256);


ALTER TABLE cs_pt_ownershipdetails_audit_v1
ADD COLUMN due_amount CHARACTER VARYING (256);

ALTER TABLE cs_pt_ownershipdetails_audit_v1
ADD COLUMN apro_charge CHARACTER VARYING (256);
