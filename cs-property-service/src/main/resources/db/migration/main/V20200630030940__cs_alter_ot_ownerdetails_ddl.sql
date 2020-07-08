ALTER TABLE cs_pt_ownershipdetails_v1
ADD COLUMN relation_with_deceased_allottee CHARACTER VARYING (256);

ALTER TABLE cs_pt_ownershipdetails_v1
ADD COLUMN date_of_death_allottee CHARACTER VARYING (256);

ALTER TABLE cs_pt_ownershipdetails_v1
ADD COLUMN application_number CHARACTER VARYING (256);

ALTER TABLE cs_pt_ownershipdetails_v1
ADD COLUMN application_type CHARACTER VARYING (256);


ALTER TABLE cs_pt_ownershipdetails_audit_v1
ADD COLUMN relation_with_deceased_allottee CHARACTER VARYING (256);

ALTER TABLE cs_pt_ownershipdetails_audit_v1
ADD COLUMN date_of_death_allottee CHARACTER VARYING (256);

ALTER TABLE cs_pt_ownershipdetails_audit_v1
ADD COLUMN application_number CHARACTER VARYING (256);

ALTER TABLE cs_pt_ownershipdetails_audit_v1
ADD COLUMN application_type CHARACTER VARYING (256);
