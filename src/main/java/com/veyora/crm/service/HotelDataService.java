package com.veyora.crm.service;

import com.veyora.crm.constant.PolicyType;
import com.veyora.crm.constant.ProductPromotionOfferStatus;
import com.veyora.crm.constant.ProductPromotionOfferType;
import com.veyora.crm.constant.RoleType;
import com.veyora.crm.dto.PolicyRequest;
import com.veyora.crm.dto.ProductPromotionOfferRequest;
import com.veyora.crm.entity.AppUser;
import com.veyora.crm.entity.HotelPolicy;
import com.veyora.crm.entity.HotelSupplierMap;
import com.veyora.crm.entity.MarketPlaceHotel;
import com.veyora.crm.entity.ProductPromotionOffer;
import com.veyora.crm.entity.SupplierPackagePricing;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.exceptionhandler.NotFoundException;
import com.veyora.crm.repository.HotelPolicyRepository;
import com.veyora.crm.repository.HotelSupplierMapRepository;
import com.veyora.crm.repository.MarketPlaceHotelRepository;
import com.veyora.crm.repository.ProductPromotionOfferRepository;
import com.veyora.crm.repository.SupplierPackagePricingRepository;
import com.veyora.crm.utils.CsvUtil;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The extranet core, mirroring tf-main HotelDataBean: hotel/supplier scope
 * resolution, policies and promotions.
 */
@Service
public class HotelDataService {

    private static final Logger log = LoggerFactory.getLogger(HotelDataService.class);

    private final MarketPlaceHotelRepository hotelRepository;
    private final HotelSupplierMapRepository hotelSupplierMapRepository;
    private final HotelPolicyRepository hotelPolicyRepository;
    private final ProductPromotionOfferRepository promotionOfferRepository;
    private final SupplierPackagePricingRepository pricingRepository;

    public HotelDataService(MarketPlaceHotelRepository hotelRepository,
            HotelSupplierMapRepository hotelSupplierMapRepository,
            HotelPolicyRepository hotelPolicyRepository,
            ProductPromotionOfferRepository promotionOfferRepository,
            SupplierPackagePricingRepository pricingRepository) {
        this.hotelRepository = hotelRepository;
        this.hotelSupplierMapRepository = hotelSupplierMapRepository;
        this.hotelPolicyRepository = hotelPolicyRepository;
        this.promotionOfferRepository = promotionOfferRepository;
        this.pricingRepository = pricingRepository;
    }

    /* --------------------------------------------------------------------- */
    /* Scope resolution (tf-main loadHotelForManage / getApplicableSupplierId) */
    /* --------------------------------------------------------------------- */

    /** Hotels the logged-in user may manage; system users see all enabled hotels. */
    public List<MarketPlaceHotel> loadHotelsForManage(AppUser user) {
        if (isSystemUser(user)) {
            return hotelRepository.findByEnabledTrue();
        }
        List<Long> hotelIds = hotelSupplierMapRepository
                .findBySupplierIdAndMapType(user.getId(), HotelSupplierMap.TYPE_SUPPLIER)
                .stream().map(HotelSupplierMap::getHotelId).toList();
        if (hotelIds.isEmpty()) {
            return List.of();
        }
        return hotelRepository.findByIdInAndEnabledTrue(hotelIds);
    }

    /**
     * Resolve the hotel to manage: explicit id if permitted, else the first
     * mapped hotel (tf-main loadHotelForManage).
     */
    public MarketPlaceHotel loadHotelForManage(AppUser user, Long requestedHotelId) {
        List<MarketPlaceHotel> allowed = loadHotelsForManage(user);
        if (requestedHotelId != null) {
            return allowed.stream().filter(h -> h.getId().equals(requestedHotelId)).findFirst()
                    .orElseThrow(() -> new NotFoundException("Hotel " + requestedHotelId + " is not mapped to you"));
        }
        if (allowed.isEmpty()) {
            throw new NotFoundException("No hotels are mapped to your account");
        }
        return allowed.get(0);
    }

    /**
     * Which supplier the data is written under: explicit param for system
     * users, otherwise the logged-in supplier (tf-main getApplicableSupplierId).
     */
    public Long getApplicableSupplierId(AppUser user, Long requestedSupplierId) {
        if (requestedSupplierId != null && isSystemUser(user)) {
            return requestedSupplierId;
        }
        return user.getId();
    }

    public boolean isSystemUser(AppUser user) {
        RoleType r = user.getRole();
        return r == RoleType.ADMIN || r == RoleType.SUPERVISOR || r == RoleType.PRODUCT
                || r == RoleType.BUSINESS_MANAGER;
    }

    /* ------------------------------------------------------------------ */
    /* Promotions / discounts (tf-main loadPromotionsForHotel, saveProductPromotion) */
    /* ------------------------------------------------------------------ */

    public List<ProductPromotionOffer> loadPromotionsForHotel(Long hotelId, boolean isLoadPromotions) {
        ProductPromotionOfferType type = isLoadPromotions
                ? ProductPromotionOfferType.SUPPLIER_PROMOTION
                : ProductPromotionOfferType.SUPPLIER_DISCOUNT;
        return promotionOfferRepository.findByHotelIdAndTypeAndStatusIn(hotelId, type,
                List.of(ProductPromotionOfferStatus.ENABLED, ProductPromotionOfferStatus.DISABLED));
    }

    public ProductPromotionOffer loadPromotion(Long promotionId) {
        return promotionOfferRepository.findById(promotionId)
                .orElseThrow(() -> new NotFoundException("Promotion " + promotionId + " not found"));
    }

    @Transactional
    public ProductPromotionOffer saveProductPromotion(ProductPromotionOfferRequest request, AppUser user) {
        ProductPromotionOffer offer = request.getPromotionId() != null
                ? loadPromotion(request.getPromotionId())
                : new ProductPromotionOffer();

        offer.setHotelId(request.getHotelId());
        offer.setSupplierId(getApplicableSupplierId(user, request.getSupplierId()));
        offer.setType(request.getType());
        offer.setTitle(request.getTitle());
        offer.setCategory(request.getCategory());
        offer.setOfferStartDate(request.getOfferStartDate());
        offer.setOfferEndDate(request.getOfferEndDate());
        offer.setTravelStartDate(request.getTravelStartDate());
        offer.setTravelEndDate(request.getTravelEndDate());
        offer.setDiscountType(request.getDiscountType());
        offer.setDiscountValue(request.getDiscountValue());
        offer.setRoomIds(CsvUtil.join(request.getRoomIds()));
        offer.setMealPlans(CsvUtil.join(request.getMealPlans()));
        offer.setMinDuration(request.getMinDuration());
        offer.setDaysInAdvance(request.getDaysInAdvance());
        if (request.getStatus() != null) {
            offer.setStatus(request.getStatus());
        }
        return promotionOfferRepository.save(offer);
    }

    /* --------------------------------------------------------------- */
    /* Policies / commissions (tf-main loadPoliciesForHotel, savePolicy) */
    /* --------------------------------------------------------------- */

    public List<HotelPolicy> loadPoliciesForHotel(Long hotelId, PolicyType policyType) {
        return hotelPolicyRepository.findByHotelIdAndPolicyTypeAndEnabledTrue(hotelId, policyType);
    }

    public HotelPolicy loadPolicy(Long policyId) {
        return hotelPolicyRepository.findById(policyId)
                .orElseThrow(() -> new NotFoundException("Policy " + policyId + " not found"));
    }

    @Transactional
    public HotelPolicy savePolicy(PolicyRequest request, AppUser user) {
        HotelPolicy policy = request.getPolicyId() != null
                ? loadPolicy(request.getPolicyId())
                : new HotelPolicy();

        Long supplierId = getApplicableSupplierId(user, request.getSupplierId());
        policy.setHotelId(request.getHotelId());
        policy.setSupplierId(supplierId);
        policy.setPolicyType(request.getPolicyType());
        policy.setTravelStartDate(request.getTravelStartDate());
        policy.setTravelEndDate(request.getTravelEndDate());

        if (request.getPolicyType() == PolicyType.CANCELLATION) {
            if (request.getPolicyJson() == null || request.getPolicyJson().isBlank()) {
                throw new BadRequestException("Cancellation rules are required");
            }
            policy.setPolicyJson(request.getPolicyJson());

        } else if (request.getPolicyType() == PolicyType.COMMISSION) {
            if (request.getCommissionPercent() == null) {
                throw new BadRequestException("Commission percent is required");
            }
            policy.setCommissionPercent(request.getCommissionPercent());
            // Cascade to live rates in the window, as tf-main updateCommissionsForAllRates
            updateCommissionsForAllRates(request.getHotelId(), supplierId,
                    request.getTravelStartDate(), request.getTravelEndDate(),
                    request.getCommissionPercent());
        }
        return hotelPolicyRepository.save(policy);
    }

    @Transactional
    public void updateCommissionsForAllRates(Long hotelId, Long supplierId,
            LocalDate startDate, LocalDate endDate, java.math.BigDecimal commissionPercent) {
        LocalDate from = startDate != null ? startDate : LocalDate.now();
        LocalDate to = endDate != null ? endDate : from.plusYears(2);
        List<SupplierPackagePricing> rates = pricingRepository
                .findForCommissionCascade(hotelId, supplierId, from, to);
        log.debug("Cascading commission {} to {} rates", commissionPercent, rates.size());
        for (SupplierPackagePricing rate : rates) {
            rate.setCommissionPercent(commissionPercent);
        }
        pricingRepository.saveAll(rates);
    }
}
