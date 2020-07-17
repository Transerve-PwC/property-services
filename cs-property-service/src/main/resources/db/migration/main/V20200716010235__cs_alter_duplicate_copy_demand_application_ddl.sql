ALTER TABLE cs_pt_duplicatecopy_applicant
ADD COLUMN fee_amount CHARACTER VARYING (256);

ALTER TABLE cs_pt_duplicatecopy_applicant
ADD COLUMN apro_charge CHARACTER VARYING (256);


ALTER TABLE cs_pt_duplicatecopy_applicant_audit
ADD COLUMN fee_amount CHARACTER VARYING (256);

ALTER TABLE cs_pt_duplicatecopy_applicant_audit
ADD COLUMN apro_charge CHARACTER VARYING (256);
