package com.veyora.crm.service;

import com.veyora.crm.constant.Constant;
import com.veyora.crm.constant.ProductPromotionOfferStatus;
import com.veyora.crm.constant.ProductPromotionOfferType;
import com.veyora.crm.dto.ContractStepRequest;
import com.veyora.crm.dto.ContractSummaryDto;
import com.veyora.crm.dto.ProductPromotionOfferRequest;
import com.veyora.crm.dto.RateUpdateRequest;
import com.veyora.crm.entity.User;
import com.veyora.crm.entity.HotelSupplierMap;
import com.veyora.crm.entity.MarketPlaceHotel;
import com.veyora.crm.entity.User;
import com.veyora.crm.entity.ProductPromotionOffer;
import com.veyora.crm.entity.RatePlan;
import com.veyora.crm.entity.SupplierPackagePricing;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.repository.HotelSupplierMapRepository;
import com.veyora.crm.repository.MarketPlaceHotelRepository;
import com.veyora.crm.repository.ProductPromotionOfferRepository;
import com.veyora.crm.repository.SupplierPackagePricingRepository;
import com.veyora.crm.repository.UserRepository;
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
    private final HotelSupplierMapRepository hotelSupplierMapRepository;
    private final UserRepository userRepository;

    public ContractService(SupplierPackageService supplierPackageService,
            HotelDataService hotelDataService,
            MarketPlaceHotelRepository hotelRepository,
            SupplierPackagePricingRepository pricingRepository,
            ProductPromotionOfferRepository promotionOfferRepository,
            HotelSupplierMapRepository hotelSupplierMapRepository,
            UserRepository userRepository) {
        this.supplierPackageService = supplierPackageService;
        this.hotelDataService = hotelDataService;
        this.hotelRepository = hotelRepository;
        this.pricingRepository = pricingRepository;
        this.promotionOfferRepository = promotionOfferRepository;
        this.hotelSupplierMapRepository = hotelSupplierMapRepository;
        this.userRepository = userRepository;
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
     * Contracts dashboard rows for a city, as tf-main loadHotelContractsForManage:
     * one row per hotel-supplier mapping with audited/unaudited rate counts,
     * live promotion counts and the last valid rate date over the next 180 days.
     */
    public List<ContractSummaryDto> loadHotelContractsForManage(Integer cityId) {
        List<MarketPlaceHotel> hotels = cityId != null
                ? hotelRepository.findByCityIdAndEnabledTrue(cityId)
                : hotelRepository.findByEnabledTrue();
        if (hotels.isEmpty()) {
            return List.of();
        }
        Map<Long, MarketPlaceHotel> hotelMap = new HashMap<>();
        for (MarketPlaceHotel hotel : hotels) {
            hotelMap.put(hotel.getId(), hotel);
        }
        List<Long> hotelIds = new ArrayList<>(hotelMap.keySet());

        // mappings for the city's hotels (tf-main getMappingsForCity)
        List<HotelSupplierMap> mappings = hotelSupplierMapRepository
                .findByHotelIdInAndMapType(hotelIds, HotelSupplierMap.TYPE_SUPPLIER);
        if (mappings.isEmpty()) {
            return List.of();
        }

        Map<Long, User> suppliers = new HashMap<>();
        userRepository.findAllById(mappings.stream().map(HotelSupplierMap::getSupplierId).toList())
                .forEach(u -> suppliers.put(u.getUserId(), u));

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(Constant.CONTRACT_SUMMARY_WINDOW_DAYS);

        List<ContractSummaryDto> rows = new ArrayList<>();
        for (HotelSupplierMap mapping : mappings) {
            MarketPlaceHotel hotel = hotelMap.get(mapping.getHotelId());
            User supplier = suppliers.get(mapping.getSupplierId());
            ContractSummaryDto dto = new ContractSummaryDto();
            dto.setMappingId(mapping.getId());
            dto.setHotelId(mapping.getHotelId());
            dto.setHotelName(hotel != null ? hotel.getName() : mapping.getHotelName());
            dto.setCityId(hotel != null ? hotel.getCityId() : mapping.getCityId());
            dto.setCityName(hotel != null ? hotel.getCityName() : null);
            dto.setSupplierId(mapping.getSupplierId());
            dto.setSupplierName(supplier != null ? supplier.getName() : String.valueOf(mapping.getSupplierId()));

            for (SupplierPackagePricing pricing : pricingRepository
                    .findForCommissionCascade(mapping.getHotelId(), mapping.getSupplierId(), startDate, endDate)) {
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
            rows.add(dto);
        }

        List<ProductPromotionOffer> promos = promotionOfferRepository.findActiveForHotels(
                hotelIds, ProductPromotionOfferType.SUPPLIER_PROMOTION,
                ProductPromotionOfferStatus.ENABLED, startDate, endDate);
        Map<Long, Integer> promoCounts = new HashMap<>();
        for (ProductPromotionOffer promo : promos) {
            promoCounts.merge(promo.getHotelId(), 1, Integer::sum);
        }
        for (ContractSummaryDto row : rows) {
            row.setPromotions(promoCounts.getOrDefault(row.getHotelId(), 0));
        }
        return rows;
    }
}
