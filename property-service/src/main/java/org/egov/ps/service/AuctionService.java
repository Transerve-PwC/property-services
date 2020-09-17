package org.egov.ps.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ps.model.Auction;
import org.egov.ps.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {
	
	@Autowired
	private AuctionRepository AuctionRepository;

	public List<Auction> createAuctions(List<Auction> auctions,RequestInfo requestInfo) {
		AuctionRepository.create(auctions,requestInfo);
        return auctions;
    }

}
