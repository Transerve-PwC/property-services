ALTER TABLE cs_pt_demand DROP COLUMN IF EXISTS tenantId;
ALTER TABLE cs_pt_payment DROP COLUMN IF EXISTS    tenantId;
ALTER TABLE cs_pt_collection DROP COLUMN IF EXISTS tenantId;
ALTER TABLE cs_pt_account DROP COLUMN IF EXISTS    tenantId;
ALTER TABLE cs_pt_demand_audit DROP COLUMN IF EXISTS   tenantId;
ALTER TABLE cs_pt_account_audit DROP COLUMN IF EXISTS  tenantId;

ALTER TABLE cs_pt_collection DROP COLUMN IF EXISTS payment_id;
ALTER TABLE cs_pt_collection DROP COLUMN IF EXISTS collectionAgainst;
ALTER TABLE cs_pt_collection 
ADD COLUMN IF NOT EXISTS collectedAt bigint;

ALTER TABLE cs_pt_demand 
ADD COLUMN IF NOT EXISTS status CHARACTER VARYING (64);

ALTER TABLE cs_pt_demand_audit 
ADD COLUMN IF NOT EXISTS status CHARACTER VARYING (64);

ALTER TABLE cs_pt_payment 
ADD COLUMN IF NOT EXISTS processed BOOLEAN;
