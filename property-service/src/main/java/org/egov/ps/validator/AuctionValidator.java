package org.egov.ps.validator;

import java.util.List;

import org.egov.ps.model.Auction;
import org.egov.ps.model.AuctionSearchCritirea;
import org.egov.ps.model.Property;
import org.egov.ps.model.PropertyCriteria;
import org.egov.ps.repository.AuctionRepository;
import org.egov.ps.web.contracts.AuctionSaveRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Component
public class AuctionValidator {
	
	@Autowired
	private AuctionRepository auctionRepository;

	public List<Auction> validateAuctionUpdate(AuctionSaveRequest request){
		
		AuctionSearchCritirea auctionSearchCritirea = getAuctionCriteriaForSearch(request);
		List<Auction> auctionsFromSearchResponse = auctionRepository.search(auctionSearchCritirea);
		boolean ifAuctionExists = AuctionExists(auctionsFromSearchResponse);
		if (!ifAuctionExists) {
			throw new CustomException("AUCTION NOT FOUND", "The auction to be updated does not exist");
		}
		return null;
	}

	private boolean AuctionExists(List<Auction> auctionsFromSearchResponse) {
		return (!CollectionUtils.isEmpty(auctionsFromSearchResponse) && auctionsFromSearchResponse.size() == 1);
	}

	/*
	 * 
	 */
	private AuctionSearchCritirea getAuctionCriteriaForSearch(AuctionSaveRequest request) {		
		AuctionSearchCritirea criteria = new AuctionSearchCritirea();
		if (!CollectionUtils.isEmpty(request.getAuctions())) {
			request.getAuctions().forEach(auction -> {
				if (auction.getId() != null)
					criteria.setAuctionId(auction.getId());

			});
		}
		return criteria;
	}
}
