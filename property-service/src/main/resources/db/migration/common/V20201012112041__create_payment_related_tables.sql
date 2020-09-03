DROP TABLE IF EXISTS cs_ep_payment_v1;

CREATE TABLE cs_ep_payment_v1 (
   	id           						CHARACTER VARYING (256) NOT NULL,
   	tenantid			    			CHARACTER VARYING (256),
   	owner_details_id     				CHARACTER VARYING (256) NOT NULL,

    payment_type						CHARACTER VARYING (256),
	due_date_of_payment					bigint,
	payable								numeric(15,2),
	amount								numeric(15,2),
	total								numeric(15,2),
	date_of_deposit						bigint,
	delay_in_payment					numeric(15,2),
	interest_for_delay					numeric(15,2),
	total_amount_due_with_interest		numeric(15,2),
	amount_deposited					numeric(15,2),
	amount_deposited_intt				numeric(15,2),
	balance								numeric(15,2),
	balance_intt						numeric(15,2),
	total_due							numeric(15,2),
	receipt_number						CHARACTER VARYING (256),
	receipt_date						bigint,
	st_rate_of_st_gst					numeric(15,2),
	st_amount_of_gst					numeric(15,2),
	st_payment_made_by					CHARACTER VARYING (256),
	bank_name							CHARACTER VARYING (256),
	cheque_number						CHARACTER VARYING (256),
	
    installment_one						numeric(15,2),
    installment_two						numeric(15,2),
	installment_two_due_date			bigint,
	installment_three					numeric(15,2),
	installment_three_due_date			bigint,
	monthly_or_annually					CHARACTER VARYING (256),
	ground_rent_start_date				bigint,
	rent_revision						numeric(15,2),
	lease_period						numeric(2,0),
	license_fee_of_year					numeric(2,0),
	license_fee							numeric(15,2),
  
   	created_by           				CHARACTER VARYING (128) NOT NULL,
   	last_modified_by     				CHARACTER VARYING (128),
   	created_time         				bigint NOT NULL,
   	last_modified_time   				bigint,

  CONSTRAINT pk_cs_ep_payment_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_ep_payment_v1 FOREIGN KEY (owner_details_id) REFERENCES cs_ep_owner_details_v1 (id)
);