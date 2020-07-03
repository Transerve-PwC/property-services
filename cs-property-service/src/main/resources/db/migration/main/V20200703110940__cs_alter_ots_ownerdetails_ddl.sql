ALTER TABLE cs_pt_ownershipdetails_v1
ADD COLUMN permanent CHARACTER VARYING (256);


ALTER TABLE cs_pt_ownershipdetails_audit_v1
ADD COLUMN permanent CHARACTER VARYING (256);
