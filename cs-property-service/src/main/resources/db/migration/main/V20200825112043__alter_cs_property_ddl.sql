ALTER TABLE cs_pt_ownership_v1 
ALTER COLUMN is_primary_owner TYPE BOOLEAN using is_primary_owner::boolean;

ALTER TABLE cs_pt_ownershipdetails_v1 
ALTER COLUMN permanent TYPE BOOLEAN using permanent::boolean;

--Audit table
ALTER TABLE cs_pt_ownership_audit_v1 
ALTER COLUMN is_primary_owner TYPE BOOLEAN using is_primary_owner::boolean;


ALTER TABLE cs_pt_ownershipdetails_audit_v1 
ALTER COLUMN permanent TYPE BOOLEAN using permanent::boolean;

