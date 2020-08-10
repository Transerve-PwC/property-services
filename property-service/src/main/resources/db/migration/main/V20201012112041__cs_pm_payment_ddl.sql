DROP TABLE IF EXISTS cs_pm_payment_v1;

CREATE TABLE cs_pm_payment_v1 (
   id           						CHARACTER VARYING (256) NOT NULL,
   tenantid			    				CHARACTER VARYING (256),
   owner_details_id     				CHARACTER VARYING (256) NOT NULL,

   gr_due_date_of_payment    			bigint,
   gr_payable    						numeric(15,2),
   gr_amount_of_gr    					numeric(15,2),
   gr_total_gr    						numeric(15,2),
   gr_date_of_deposit    				bigint,
   gr_delay_in_payment    				numeric(15,2),
   gr_interest_for_delay    			numeric(15,2),
   gr_total_amount_due_with_interest    numeric(15,2),
   gr_amount_deposited_gr    			numeric(15,2),
   gr_amount_deposited_intt    			numeric(15,2),
   gr_balance_gr    					numeric(15,2),
   gr_balance_intt    					numeric(15,2),
   gr_total_due    						numeric(15,2),
   gr_receipt_number    				CHARACTER VARYING (256),
   gr_receipt_date    					bigint,
   
   st_rate_of_st_gst    				numeric(15,2),
   st_amount_of_gst    					numeric(15,2),
   st_amount_due    					numeric(15,2),
   st_date_of_deposit    				bigint,
   st_delay_in_payment    				numeric(15,2),
   st_interest_for_delay    			numeric(15,2),
   st_total_amount_due_with_interest  	numeric(15,2),
   st_amount_deposited_st_gst    		numeric(15,2),
   st_amount_deposited_intt    			numeric(15,2),
   st_balance_st_gst    				numeric(15,2),
   st_balance_intt    					numeric(15,2),
   st_total_due    						numeric(15,2),
   st_receipt_number    				CHARACTER VARYING (256),
   st_receipt_date    					bigint,
   st_payment_made_by    				CHARACTER VARYING (256),
  
   created_by           				CHARACTER VARYING (128) NOT NULL,
   last_modified_by     				CHARACTER VARYING (128),
   created_time         				bigint NOT NULL,
   last_modified_time   				bigint,

  CONSTRAINT pk_cs_pm_payment_v1 PRIMARY KEY (id),
  CONSTRAINT fk_cs_pm_payment_v1 FOREIGN KEY (owner_details_id) REFERENCES cs_pm_owner_details_v1 (id)
);
