DROP TABLE IF EXISTS cs_pt_demand;
DROP TABLE IF EXISTS cs_pt_payment;
DROP TABLE IF EXISTS cs_pt_collection;
DROP TABLE IF EXISTS cs_pt_account;
DROP TABLE IF EXISTS cs_pt_demand_audit;
DROP TABLE IF EXISTS cs_pt_account_audit;


--> Demand table
CREATE TABLE cs_pt_demand (
   id           		CHARACTER VARYING (256) NOT NULL,
   property_id          CHARACTER VARYING (256) NOT NULL,
   initialGracePeriod   int,
   generationDate       bigint,
   collectionPrincipal  numeric(13,6),
   remainingPrincipal   numeric(13,6),
   interestSince		   bigint,
   mode					   CHARACTER VARYING (64),
   status               CHARACTER VARYING (64),
  
   created_by           CHARACTER VARYING (128),
   created_date         bigint,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	bigint,
  
  CONSTRAINT pk_cs_pt_demand PRIMARY KEY (id),
  CONSTRAINT fk_cs_pt_demand FOREIGN KEY (property_id) REFERENCES cs_pt_property_v1 (id)
);

--> Payment Table
CREATE TABLE cs_pt_payment (
   id           		   CHARACTER VARYING (256) NOT NULL,
   property_id          CHARACTER VARYING (256) NOT NULL,
   receiptNo	    	   CHARACTER VARYING(64),
   amountPaid   		   numeric(13,6),
   dateOfPayment   		bigint,
   mode					   CHARACTER VARYING (64),
   processed            BOOLEAN,
  
   created_by           CHARACTER VARYING (128),
   created_date         bigint,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	bigint,
	
  CONSTRAINT pk_cs_pt_payment PRIMARY KEY (id), 
  CONSTRAINT fk_cs_pt_payment FOREIGN KEY (property_id) REFERENCES cs_pt_property_v1 (id)
);

--> Collection Table
CREATE TABLE cs_pt_collection (
   id           		CHARACTER VARYING (256) NOT NULL,
   demand_id            CHARACTER VARYING (256) NOT NULL,
   interestCollected    numeric(13,6),
   principalCollected   numeric(13,6),
  
   created_by           CHARACTER VARYING (128),
   created_date         bigint,
   collectedAt         bigint,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	bigint,

  CONSTRAINT pk_cs_pt_collection PRIMARY KEY (id), 
  CONSTRAINT fk_cs_pt_collection_demand FOREIGN KEY (demand_id) REFERENCES cs_pt_demand (id) ON DELETE CASCADE
);

--> Account Table
CREATE TABLE cs_pt_account (
   id           		CHARACTER VARYING (256) NOT NULL,
   property_id          CHARACTER VARYING (256) NOT NULL,
   remainingAmount	    numeric(13,6),
  
   created_by           CHARACTER VARYING (128),
   created_date         bigint,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	bigint,
	
  CONSTRAINT pk_cs_pt_account PRIMARY KEY (id), 
  CONSTRAINT fk_cs_pt_account_payment FOREIGN KEY (property_id) REFERENCES cs_pt_property_v1 (id)
);

--> Audit Tables
CREATE TABLE cs_pt_demand_audit(
   id           		CHARACTER VARYING (256) NOT NULL,
   property_id          CHARACTER VARYING (256) NOT NULL,
   initialGracePeriod   int,
   generationDate       bigint,
   collectionPrincipal  numeric(13,6),
   remainingPrincipal   numeric(13,6),
   interestSince		bigint,
   mode					CHARACTER VARYING (64),
   status            CHARACTER VARYING (64),
  
   created_by           CHARACTER VARYING (128),
   created_date         bigint,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	bigint
);

CREATE TABLE cs_pt_account_audit (
   id           		CHARACTER VARYING (256) NOT NULL,
   property_id          CHARACTER VARYING (256) NOT NULL,
   remainingAmount	    numeric(13,6),
  
   created_by           CHARACTER VARYING (128),
   created_date         bigint,
   modified_by     		CHARACTER VARYING (128),
   modified_date       	bigint
);