package com.veyora.crm.service;

import com.veyora.crm.constant.ContractType;
import com.veyora.crm.constant.PolicyType;
import com.veyora.crm.constant.RoomOccupancy;
import com.veyora.crm.dto.RatePlanRequest;
import com.veyora.crm.dto.RateUpdateRequest;
import com.veyora.crm.entity.User;
import com.veyora.crm.entity.HotelPolicy;
import com.veyora.crm.entity.HotelRoom;
import com.veyora.crm.entity.RatePlan;
import com.veyora.crm.entity.SupplierPackagePricing;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.exceptionhandler.NotFoundException;
import com.veyora.crm.repository.HotelPolicyRepository;
import com.veyora.crm.repository.HotelRoomRepository;
import com.veyora.crm.repository.RatePlanRepository;
import com.veyora.crm.repository.SupplierPackagePricingRepository;
import com.veyora.crm.utils.CsvUtil;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Rate plans and dated rates, mirroring tf-main SupplierPackageManager plus the
 * HotelDataBean methods addRatePlan / updateHotelRates / loadRatePlansForManage.
 */
@Service
public class SupplierPackageService {

    private static final Logger log = LoggerFactory.getLogger(SupplierPackageService.class);

    private final RatePlanRepository ratePlanRepository;
    private final SupplierPackagePricingRepository pricingRepository;
    private final HotelRoomRepository hotelRoomRepository;
    private final HotelPolicyRepository hotelPolicyRepository;
    private final HotelDataService hotelDataService;

    public SupplierPackageService(RatePlanRepository ratePlanRepository,
            SupplierPackagePricingRepository pricingRepository,
            HotelRoomRepository hotelRoomRepository,
            HotelPolicyRepository hotelPolicyRepository,
            HotelDataService hotelDataService) {
        this.ratePlanRepository = ratePlanRepository;
        this.pricingRepository = pricingRepository;
        this.hotelRoomRepository = hotelRoomRepository;
        this.hotelPolicyRepository = hotelPolicyRepository;
        this.hotelDataService = hotelDataService;
    }

    /* --------------------------------------------------------- */
    /* Rate plans (tf-main loadRatePlansForManage / addRatePlan)  */
    /* --------------------------------------------------------- */

    public List<RatePlan> loadRatePlansForManage(Long hotelId) {
        return ratePlanRepository.findByHotelIdAndEnabledTrue(hotelId);
    }

    public RatePlan loadRatePlan(Long ratePlanId) {
        return ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new NotFoundException("Rate plan " + ratePlanId + " not found"));
    }

    /**
     * Create or update a room + meal-plan rate plan. The plan name is derived
     * as "&lt;room&gt; - &lt;meal code&gt;" (+ " - NRF" for non-refundable),
     * exactly as tf-main addRatePlan builds it.
     */
    @Transactional
    public RatePlan addOrUpdateRatePlan(RatePlanRequest request, User user) {
        HotelRoom room = hotelRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new NotFoundException("Room " + request.getRoomId() + " not found"));

        RoomOccupancy occ = request.getRoomOccupancy();
        int occupancy;
        int maxAdults;
        Integer maxChildWithMaxAdults;
        Integer maxChildWithoutBed;
        if (occ != null && occ != RoomOccupancy.CUSTOM) {
            occupancy = occ.getOccupancy();
            maxAdults = occ.getMaxAdults();
            maxChildWithMaxAdults = occ.getMaxChildWithMaxAdults();
            maxChildWithoutBed = occ.getMaxChildWithoutBed();
        } else {
            if (request.getOccupancy() == null || request.getMaxAdults() == null) {
                throw new BadRequestException(
                        "Max room occupancy and max adults and child are not defined properly. Please add the same.");
            }
            occupancy = request.getOccupancy();
            maxAdults = request.getMaxAdults();
            maxChildWithMaxAdults = request.getMaxChildWithMaxAdults();
            maxChildWithoutBed = request.getMaxChildWithoutBed();
        }

        RatePlan plan = request.getRatePlanId() != null
                ? loadRatePlan(request.getRatePlanId())
                : new RatePlan();

        String planName = room.getRoomName() + " - " + request.getMealPlan().getCode();
        if (isCompletelyNonRefundable(request.getCancellationPolicyJson())) {
            planName += " - NRF";
        }

        plan.setHotelId(room.getHotelId());
        plan.setRoomId(room.getId());
        plan.setSupplierId(hotelDataService.getApplicableSupplierId(user, request.getSupplierId()));
        plan.setPlanName(planName);
        plan.setMealPlan(request.getMealPlan());
        plan.setCurrency(request.getCurrency());
        plan.setMaxOccupancy(occupancy);
        plan.setMaxAdults(maxAdults);
        plan.setMaxChildWithMaxAdults(maxChildWithMaxAdults != null ? maxChildWithMaxAdults
                : Math.max(occupancy - maxAdults, 0));
        plan.setMaxChildWithoutBed(maxChildWithoutBed);
        plan.setMinChildWithBedAge(request.getMinChildWithBedAge());
        plan.setMinChildWithoutBedAge(request.getMinChildWithoutBedAge());
        plan.setMinAdultAge(request.getMinAdultAge());
        plan.setMinLengthOfStay(request.getMinLengthOfStay());
        plan.setPromoCode(request.getPromoCode());
        plan.setInclusions(request.getInclusions());
        if (request.getContractType() != null) {
            plan.setContractType(request.getContractType());
        }
        plan.setCommissionPercent(request.getCommissionPercent());
        plan.setCancellationPolicyJson(request.getCancellationPolicyJson());
        return ratePlanRepository.save(plan);
    }

    /* ------------------------------------------------ */
    /* Dated rates (tf-main updateHotelRates)            */
    /* ------------------------------------------------ */

    public List<SupplierPackagePricing> loadRatesForRatePlan(Long ratePlanId) {
        return pricingRepository.findByRatePlanIdAndEnabledTrue(ratePlanId);
    }

    /**
     * Create/replace the rate for a travel window. An existing pricing with
     * the same window is updated in place, otherwise a new one is stored -
     * tf-main updateHotelRates + storeAndUpdateAvailablePackagePricing.
     */
    @Transactional
    public SupplierPackagePricing updateHotelRates(RateUpdateRequest request, User user) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("endDate must not be before startDate");
        }
        RatePlan ratePlan = loadRatePlan(request.getRatePlanId());
        Long supplierId = hotelDataService.getApplicableSupplierId(user, request.getSupplierId());

        Optional<SupplierPackagePricing> oldPricing = pricingRepository
                .findByRatePlanIdAndTravelStartDateAndTravelEndDateAndEnabledTrue(
                        ratePlan.getId(), request.getStartDate(), request.getEndDate());
        SupplierPackagePricing pricing = oldPricing.orElseGet(SupplierPackagePricing::new);

        pricing.setRatePlanId(ratePlan.getId());
        pricing.setHotelId(ratePlan.getHotelId());
        pricing.setSupplierId(supplierId);
        pricing.setTravelStartDate(request.getStartDate());
        pricing.setTravelEndDate(request.getEndDate());
        pricing.setApplicableDays(CsvUtil.join(request.getApplicableDays()));
        pricing.setCurrency(ratePlan.getCurrency());
        pricing.setSingleSharing(request.getSingleSharing());
        pricing.setTwinSharing(request.getTwinSharing());
        pricing.setExtraAdult(request.getExtraAdult());
        pricing.setChildWithBed(request.getChildWithBed());
        pricing.setChildWithoutBed(request.getChildWithoutBed());
        pricing.setInfant(request.getInfant());
        pricing.setMinStay(request.getMinStay() != null ? request.getMinStay()
                : ratePlan.getMinLengthOfStay());
        pricing.setCutOffDays(request.getCutOffDays());
        pricing.setPromoCode(request.getPromoCode() != null ? request.getPromoCode()
                : ratePlan.getPromoCode());

        // Commission from the applicable COMMISSION policy, as in tf-main.
        if (ratePlan.getContractType() == ContractType.COMMISSIONABLE) {
            pricing.setCommissionPercent(resolveCommission(ratePlan));
        }
        // Supplier-loaded rates may require auditing before going live.
        pricing.setAudited(hotelDataService.isSystemUser(user));

        log.debug("Storing rate for plan {} window {} - {}", ratePlan.getId(),
                request.getStartDate(), request.getEndDate());
        return pricingRepository.save(pricing);
    }

    private java.math.BigDecimal resolveCommission(RatePlan ratePlan) {
        List<HotelPolicy> policies = hotelPolicyRepository
                .findByHotelIdAndSupplierIdAndPolicyTypeAndEnabledTrue(
                        ratePlan.getHotelId(), ratePlan.getSupplierId(), PolicyType.COMMISSION);
        if (!policies.isEmpty() && policies.get(0).getCommissionPercent() != null) {
            return policies.get(0).getCommissionPercent();
        }
        return ratePlan.getCommissionPercent();
    }

    private boolean isCompletelyNonRefundable(String cancellationPolicyJson) {
        // tf-main checks CancellationPolicy.isCompletelyNonRefunable(); here the
        // JSON convention is {"nonRefundable": true, ...}
        return cancellationPolicyJson != null
                && cancellationPolicyJson.replaceAll("\\s", "")
                        .contains("\"nonRefundable\":true");
    }
}
