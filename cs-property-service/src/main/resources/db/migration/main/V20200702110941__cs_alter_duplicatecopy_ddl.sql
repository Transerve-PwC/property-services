--duplicatecopy_application
ALTER TABLE cs_pt_duplicate_ownership_application DROP CONSTRAINT fk_cs_pt_duplicate_ownership_application;

ALTER TABLE cs_pt_duplicate_ownership_application
RENAME transit_number TO property_id;

ALTER TABLE cs_pt_duplicate_ownership_application
ADD CONSTRAINT fk_cs_pt_duplicate_ownership_application FOREIGN KEY (property_id) REFERENCES cs_pt_property_v1 (id);

--duplicatecopy_application_audit
ALTER TABLE cs_pt_duplicate_ownership_application_audit
RENAME transit_number TO property_id;


--duplicatecopy_applicant
ALTER TABLE cs_pt_duplicatecopy_applicant DROP CONSTRAINT fk_cs_pt_duplicatecopy_applicant;

ALTER TABLE cs_pt_duplicatecopy_applicant RENAME property_id TO application_id;

ALTER TABLE cs_pt_duplicatecopy_applicant
ADD CONSTRAINT fk_cs_pt_duplicatecopy_applicant FOREIGN KEY (application_id) REFERENCES cs_pt_duplicate_ownership_application (id);


--duplicatecopy_applicant_audit
ALTER TABLE cs_pt_duplicatecopy_applicant_audit
RENAME property_id TO application_id;

--documnet
ALTER TABLE cs_pt_duplicatecopy_document DROP CONSTRAINT fk_cs_pt_duplicatecopy_document;

ALTER TABLE cs_pt_duplicatecopy_document RENAME propertydetailid TO application_id;

ALTER TABLE cs_pt_duplicatecopy_document
ADD CONSTRAINT fk_cs_pt_duplicatecopy_document FOREIGN KEY (application_id) REFERENCES cs_pt_duplicate_ownership_application (id);


