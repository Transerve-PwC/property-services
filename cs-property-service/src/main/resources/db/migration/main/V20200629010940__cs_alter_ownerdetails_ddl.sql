ALTER TABLE cs_pt_ownershipdetails_v1
ADD COLUMN father_or_husband CHARACTER VARYING (256);

ALTER TABLE cs_pt_ownershipdetails_v1
ADD COLUMN relation CHARACTER VARYING (256);


ALTER TABLE cs_pt_ownershipdetails_audit_v1
ADD COLUMN father_or_husband CHARACTER VARYING (256);

ALTER TABLE cs_pt_ownershipdetails_audit_v1
ADD COLUMN relation CHARACTER VARYING (256);
