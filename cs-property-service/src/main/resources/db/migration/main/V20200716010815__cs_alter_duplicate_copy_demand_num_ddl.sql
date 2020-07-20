ALTER TABLE cs_pt_duplicatecopy_applicant
ALTER COLUMN fee_amount TYPE INT USING fee_amount::integer;

ALTER TABLE cs_pt_duplicatecopy_applicant
ALTER COLUMN apro_charge TYPE INT USING apro_charge::integer;


ALTER TABLE cs_pt_duplicatecopy_applicant_audit
ALTER COLUMN fee_amount TYPE INT USING fee_amount::integer;

ALTER TABLE cs_pt_duplicatecopy_applicant_audit
ALTER COLUMN apro_charge TYPE INT USING apro_charge::integer;
