package org.egov.ps.validator;

import java.util.List;

import org.egov.ps.model.AuctionBidder;
import org.egov.ps.model.AuctionSearchCritirea;
import org.egov.ps.repository.AuctionRepository;
import org.egov.ps.web.contracts.AuctionSaveRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class AuctionValidator {

	@Autowired
	private AuctionRepository auctionRepository;

	public List<AuctionBidder> validateAuctionUpdate(AuctionSaveRequest request) {

		AuctionSearchCritirea auctionSearchCritirea = getAuctionCriteriaForSearch(request);
		List<AuctionBidder> auctionsFromSearchResponse = auctionRepository.search(auctionSearchCritirea);
		boolean ifAuctionExists = AuctionExists(auctionsFromSearchResponse);
		if (!ifAuctionExists) {
			throw new CustomException("AUCTION NOT FOUND", "The auction to be updated does not exist");
		}
		return null;
	}

	private boolean AuctionExists(List<AuctionBidder> auctionsFromSearchResponse) {
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
