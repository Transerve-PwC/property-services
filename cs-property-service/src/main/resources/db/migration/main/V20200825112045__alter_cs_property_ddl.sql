ALTER TABLE cs_pt_ownership_v1 
ALTER COLUMN active_state TYPE BOOLEAN using is_primary_owner::boolean;

--Audit table
ALTER TABLE cs_pt_ownership_audit_v1 
ALTER COLUMN active_state TYPE BOOLEAN using is_primary_owner::boolean;

