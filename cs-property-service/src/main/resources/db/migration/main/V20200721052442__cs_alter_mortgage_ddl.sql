DROP TABLE IF EXISTS cs_pt_mortgage_approved_grantdetails;

--> Mortgage approved grant details table

CREATE TABLE cs_pt_mortgage_approved_grantdetails(
    id 						CHARACTER VARYING(64),
    property_detail_id 		CHARACTER VARYING(64),
    owner_id 				CHARACTER VARYING(64),
    tenantid 				CHARACTER VARYING(64),
    
    bank_name 				CHARACTER VARYING(64),
    mortgage_amount 		numeric(12,2),
    sanction_letter_number	CHARACTER VARYING(64),
    sanction_date 			bigint,
    mortgage_end_date 		CHARACTER VARYING(64),
    
    created_by 				CHARACTER VARYING(64),
    modified_by 			CHARACTER VARYING(64),
    created_time 			bigint,
    modified_time 			bigint,

    CONSTRAINT pk_cs_pt_mortgage_approved_grantdetails PRIMARY KEY (id)
);
