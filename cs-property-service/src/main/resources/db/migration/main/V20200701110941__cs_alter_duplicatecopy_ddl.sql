ALTER TABLE cs_pt_duplicatecopy_applicant 
DROP COLUMN address;

ALTER TABLE cs_pt_duplicatecopy_applicant_audit 
DROP COLUMN address;

ALTER TABLE cs_pt_duplicate_ownership_application 
ADD COLUMN application_number CHARACTER VARYING(64);

ALTER TABLE cs_pt_duplicate_ownership_application_audit 
ADD COLUMN application_number CHARACTER VARYING(64);