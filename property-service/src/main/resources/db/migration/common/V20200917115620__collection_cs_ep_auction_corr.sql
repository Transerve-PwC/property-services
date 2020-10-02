DROP TABLE IF EXISTS cs_ep_auction;

CREATE TABLE cs_ep_auction
(
  id CHARACTER VARYING (256) NOT NULL,
  property_id CHARACTER VARYING (256) NOT NULL,
  auction_id CHARACTER VARYING (256) NOT NULL,
  tenant_id character varying(50),
  file_number character varying(50),
  auction_description character varying(250),
  participated_bidders character varying(15),
  deposited_emd_amount numeric(12,2),
  deposit_date bigint,
  emdValidity_date bigint,
  refund_status VARCHAR(256)NOT NULL,
  comment character varying(250),
  created_by VARCHAR(256)NOT NULL,
  last_modified_by VARCHAR(256)NOT NULL,
  created_date bigint,
  last_modified_date bigint,
  
  CONSTRAINT pk_cs_ep_auction PRIMARY KEY (id),
  CONSTRAINT fk_cs_ep_auction FOREIGN KEY (property_id) REFERENCES cs_ep_property_v1 (id)
  ON UPDATE CASCADE
  ON DELETE CASCADE	 
 );