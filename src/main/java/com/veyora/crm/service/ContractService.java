package com.veyora.crm.service;

import com.veyora.crm.constant.Constant;
import com.veyora.crm.constant.ProductPromotionOfferStatus;
import com.veyora.crm.constant.ProductPromotionOfferType;
import com.veyora.crm.dto.ContractStepRequest;
import com.veyora.crm.dto.ContractSummaryDto;
import com.veyora.crm.dto.ProductPromotionOfferRequest;
import com.veyora.crm.dto.RateUpdateRequest;
import com.veyora.crm.entity.User;
import com.veyora.crm.entity.MarketPlaceHotel;
import com.veyora.crm.entity.ProductPromotionOffer;
import com.veyora.crm.entity.RatePlan;
import com.veyora.crm.entity.SupplierPackagePricing;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.repository.MarketPlaceHotelRepository;
import com.veyora.crm.repository.ProductPromotionOfferRepository;
import com.veyora.crm.repository.SupplierPackagePricingRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The add-contract wizard and the contracts dashboard, mirroring tf-main
 * moveContractStep and loadHotelContractsForManage.
 */
@Service
public class ContractService {

    private final SupplierPackageService supplierPackageService;
    private final HotelDataService hotelDataService;
    private final MarketPlaceHotelRepository hotelRepository;
    private final SupplierPackagePricingRepository pricingRepository;
    private final ProductPromotionOfferRepository promotionOfferRepository;

    public ContractService(SupplierPackageService supplierPackageService,
            HotelDataService hotelDataService,
            MarketPlaceHotelRepository hotelRepository,
            SupplierPackagePricingRepository pricingRepository,
            ProductPromotionOfferRepository promotionOfferRepository) {
        this.supplierPackageService = supplierPackageService;
        this.hotelDataService = hotelDataService;
        this.hotelRepository = hotelRepository;
        this.pricingRepository = pricingRepository;
        this.promotionOfferRepository = promotionOfferRepository;
    }

    /**
     * The 4-step contract wizard (tf-main /hotels/contract-x):
     * 1 select/create rate plan, 2 load rates, 3 promotions, 4 done/summary.
     */
    @Transactional
    public Map<String, Object> moveContractStep(ContractStepRequest request, User user) {
        Map<String, Object> result = new HashMap<>();
        int stepId = request.getStepId();

        switch (stepId) {
        case 1 -> {
            RatePlan plan;
            if (request.getSelectedRatePlanId() != null) {
                plan = supplierPackageService.loadRatePlan(request.getSelectedRatePlanId());
            } else if (request.getRatePlan() != null) {
                plan = supplierPackageService.addOrUpdateRatePlan(request.getRatePlan(), user);
            } else {
                throw new BadRequestException("Select an existing rate plan or define a new one");
            }
            result.put("rpId", plan.getId());
            result.put("rpNm", plan.getPlanName());
            result.put("curr", plan.getCurrency());
        }
        case 2 -> {
            if (request.getRates() == null || request.getRates().isEmpty()) {
                throw new BadRequestException("At least one rate window is required");
            }
            List<Long> savedIds = new ArrayList<>();
            for (RateUpdateRequest rate : request.getRates()) {
                SupplierPackagePricing saved = supplierPackageService.updateHotelRates(rate, user);
                savedIds.add(saved.getId());
            }
            result.put("rslts", savedIds);
            result.put("promos", hotelDataService.loadPromotionsForHotel(request.getHotelId(), true));
        }
        case 3 -> {
            int added = 0;
            if (request.getPromotions() != null) {
                for (ProductPromotionOfferRequest promo : request.getPromotions()) {
                    hotelDataService.saveProductPromotion(promo, user);
                    added++;
                }
            }
            if (added > 0) {
                result.put("msgs", "Added " + added + " promotion(s) successfully");
            }
        }
        case 4 -> result.put("status", true);
        default -> throw new BadRequestException("Unknown contract step " + stepId);
        }
        return result;
    }

    /**
     * Contracting dashboard per hotel: audited/unaudited rate counts, promotion
     * counts and last valid rate date over the next 180 days
     * (tf-main loadHotelContractsForManage).
     */
    public List<ContractSummaryDto> loadHotelContractsForManage(Integer cityId) {
        List<MarketPlaceHotel> hotels = cityId != null
                ? hotelRepository.findByCityIdAndEnabledTrue(cityId)
                : hotelRepository.findByEnabledTrue();
        if (hotels.isEmpty()) {
            return List.of();
        }
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(Constant.CONTRACT_SUMMARY_WINDOW_DAYS);

        Map<Long, ContractSummaryDto> summaryMap = new HashMap<>();
        for (MarketPlaceHotel hotel : hotels) {
            ContractSummaryDto dto = new ContractSummaryDto();
            dto.setHotelId(hotel.getId());
            dto.setHotelName(hotel.getName());
            summaryMap.put(hotel.getId(), dto);
        }

        List<Long> hotelIds = hotels.stream().map(MarketPlaceHotel::getId).toList();
        for (Long hotelId : hotelIds) {
            List<SupplierPackagePricing> pricings = pricingRepository
                    .findOverlapping(hotelId, startDate, endDate);
            ContractSummaryDto dto = summaryMap.get(hotelId);
            for (SupplierPackagePricing pricing : pricings) {
                if (pricing.isAudited()) {
                    dto.setAudited(dto.getAudited() + 1);
                    if (dto.getLastValidDate() == null
                            || dto.getLastValidDate().isBefore(pricing.getTravelEndDate())) {
                        dto.setLastValidDate(pricing.getTravelEndDate());
                    }
                } else {
                    dto.setUnaudited(dto.getUnaudited() + 1);
                }
            }
        }

        List<ProductPromotionOffer> promos = promotionOfferRepository.findActiveForHotels(
                hotelIds, ProductPromotionOfferType.SUPPLIER_PROMOTION,
                ProductPromotionOfferStatus.ENABLED, startDate, endDate);
        for (ProductPromotionOffer promo : promos) {
            ContractSummaryDto dto = summaryMap.get(promo.getHotelId());
            if (dto != null) {
                dto.setPromotions(dto.getPromotions() + 1);
            }
        }
        return new ArrayList<>(summaryMap.values());
    }
}
