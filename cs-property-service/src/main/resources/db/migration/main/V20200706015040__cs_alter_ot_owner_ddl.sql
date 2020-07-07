ALTER TABLE cs_pt_ownership_v1
DROP COLUMN application_status,
ADD COLUMN application_state CHARACTER VARYING (256),
ADD COLUMN application_action CHARACTER VARYING (256);


ALTER TABLE cs_pt_ownership_audit_v1
DROP COLUMN application_status,
ADD COLUMN application_state CHARACTER VARYING (256),
ADD COLUMN application_action CHARACTER VARYING (256);