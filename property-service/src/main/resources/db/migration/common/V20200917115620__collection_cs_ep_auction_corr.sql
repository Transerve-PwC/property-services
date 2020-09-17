DROP TABLE IF EXISTS cs_ep_auction;

DROP SEQUENCE IF EXISTS seq_cs_ep_auction;

CREATE TABLE cs_ep_auction
(
  id bigint NOT NULL,
  auctionDescription character varying(50),
  participatedBidders character varying(50),
  depositedEMDAmount character varying(15) ,
  depositDate character varying(15),
  emdValidityDate character varying(15) ,
  refundStatus character varying(12),
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  createddate bigint,
  lastmodifieddate bigint,
  CONSTRAINT pk_cs_ep_auction PRIMARY KEY (id)
  );

CREATE SEQUENCE seq_cs_ep_auction;