ALTER TABLE cs_pt_account 
ADD COLUMN IF NOT EXISTS remaining_since bigint;

--Audit table

ALTER TABLE cs_pt_account_audit 
ADD COLUMN IF NOT EXISTS remaining_since bigint;
