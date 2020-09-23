package org.egov.ps.web.contracts;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.ps.model.Auction;
import org.egov.ps.model.AuctionMaster;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionTransactionResponse {
	
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;
	
	@JsonProperty("Auctions")
	@Valid
	private List<Auction> auctions;
	
	public AuctionTransactionResponse addAuctions(Auction auction) {
		if (this.auctions == null) {
			this.auctions = new ArrayList<>();
		}
		this.auctions.add(auction);
		return this;
	}

}
