ALTER TABLE cs_pt_mortgage_approved_grantdetails
RENAME COLUMN property_detail_id TO property_id;

DROP TABLE IF EXISTS cs_pt_mortgage_grantdetails;