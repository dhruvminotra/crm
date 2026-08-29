package com.veyora.crm.controller;

import com.veyora.crm.constant.Constant;
import com.veyora.crm.constant.PolicyType;
import com.veyora.crm.dto.ApiResponse;
import com.veyora.crm.dto.ContractStepRequest;
import com.veyora.crm.dto.ContractSummaryDto;
import com.veyora.crm.dto.InventoryGridResponse;
import com.veyora.crm.dto.InventoryUpdateRequest;
import com.veyora.crm.dto.PolicyRequest;
import com.veyora.crm.dto.ProductPromotionOfferRequest;
import com.veyora.crm.dto.RatePlanRequest;
import com.veyora.crm.dto.RateUpdateRequest;
import com.veyora.crm.entity.User;
import com.veyora.crm.entity.HotelPolicy;
import com.veyora.crm.entity.MarketPlaceHotel;
import com.veyora.crm.entity.ProductPromotionOffer;
import com.veyora.crm.entity.RatePlan;
import com.veyora.crm.entity.SupplierPackagePricing;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.service.AuthService;
import com.veyora.crm.service.ContractService;
import com.veyora.crm.service.HotelDataService;
import com.veyora.crm.service.PackageDataService;
import com.veyora.crm.service.SupplierPackageService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The hotel extranet API. Paths and role gating mirror tf-main
 * HotelNavigation / HotelAction (see /hotels/&lt;action&gt; URLs).
 */
@RestController
@RequestMapping(Constant.API_V1 + "/hotels")
public class HotelController {

    /** tf-main MANAGE_INVENTORY / RATE_PLANS / PROMOTIONS / POLICIES gating. */
    private static final String EXTRANET_ROLES =
            "hasAnyRole('ADMIN','HOTELIER','TOUR_OPERATOR','PRODUCT','SUPERVISOR','BUSINESS_MANAGER')";

    /** tf-main COMMISSIONS gating (no hotelier). */
    private static final String COMMISSION_ROLES =
            "hasAnyRole('ADMIN','PRODUCT','BUSINESS_MANAGER','SUPERVISOR')";

    /** tf-main HOTEL_CONTRACTS_MANAGE gating. */
    private static final String CONTRACTS_ROLES =
            "hasAnyRole('ADMIN','FINANCE','PRODUCT','BUSINESS_MANAGER','SUPERVISOR')";

    /** tf-main INVENTORY / INVENTORY_Z gating. */
    private static final String INVENTORY_POSITION_ROLES =
            "hasAnyRole('ADMIN','PRODUCT','SUPERVISOR','BUSINESS_MANAGER','EXPERT','CALLCENTER')";

    private final HotelDataService hotelDataService;
    private final PackageDataService packageDataService;
    private final SupplierPackageService supplierPackageService;
    private final ContractService contractService;

    public HotelController(HotelDataService hotelDataService,
            PackageDataService packageDataService,
            SupplierPackageService supplierPackageService,
            ContractService contractService) {
        this.hotelDataService = hotelDataService;
        this.packageDataService = packageDataService;
        this.supplierPackageService = supplierPackageService;
        this.contractService = contractService;
    }

    private User user() {
        User user = AuthService.getLoggedInUser();
        if (user == null) {
            throw new BadRequestException("No logged-in user");
        }
        return user;
    }

    /* ------------- hotel selection (select_hotel.jsp) ------------- */

    @GetMapping("/manage")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<List<MarketPlaceHotel>> loadHotelsForManage() {
        return ApiResponse.ok(hotelDataService.loadHotelsForManage(user()));
    }

    /* ------------- inventory (manage-inventory / inventory-x) ------------- */

    @GetMapping("/manage-inventory")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<InventoryGridResponse> manageInventory(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate) {
        User user = user();
        MarketPlaceHotel hotel = hotelDataService.loadHotelForManage(user, hotelId);
        Long applicableSupplier = hotelDataService.getApplicableSupplierId(user, supplierId);
        return ApiResponse.ok(packageDataService.loadHotelInventoryAndRates(
                hotel.getId(), applicableSupplier, fromDate));
    }

    @PostMapping("/update-inventory")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<Void> updateInventory(@Valid @RequestBody InventoryUpdateRequest request) {
        packageDataService.updateHotelInventory(request, user());
        return ApiResponse.ok("Inventory updated", null);
    }

    @GetMapping("/inventory-z")
    @PreAuthorize(INVENTORY_POSITION_ROLES)
    public ApiResponse<Map<Long, List<com.veyora.crm.dto.InventoryCellDto>>> inventoryPosition(
            @RequestParam List<Long> hotelIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate) {
        return ApiResponse.ok(packageDataService.loadHotelInventoryPosition(hotelIds, fromDate));
    }

    /* ------------- rates (update-rates) ------------- */

    @PostMapping("/update-rates")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<SupplierPackagePricing> updateRates(@Valid @RequestBody RateUpdateRequest request) {
        return ApiResponse.ok("Rates updated", supplierPackageService.updateHotelRates(request, user()));
    }

    /* ------------- rate plans (rate-plans / edit-rate-plans / edit-rate-plan) ------------- */

    @GetMapping("/rate-plans")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<List<RatePlan>> ratePlans(@RequestParam(required = false) Long hotelId) {
        MarketPlaceHotel hotel = hotelDataService.loadHotelForManage(user(), hotelId);
        return ApiResponse.ok(supplierPackageService.loadRatePlansForManage(hotel.getId()));
    }

    @PostMapping("/edit-rate-plans")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<RatePlan> editRatePlans(@Valid @RequestBody RatePlanRequest request) {
        return ApiResponse.ok("Rate plan saved", supplierPackageService.addOrUpdateRatePlan(request, user()));
    }

    @GetMapping("/edit-rate-plan")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<RatePlan> editRatePlan(@RequestParam("rpid") Long ratePlanId) {
        return ApiResponse.ok(supplierPackageService.loadRatePlan(ratePlanId));
    }

    @GetMapping("/rate-plan-rates")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<List<SupplierPackagePricing>> ratePlanRates(@RequestParam("rpid") Long ratePlanId) {
        return ApiResponse.ok(supplierPackageService.loadRatesForRatePlan(ratePlanId));
    }

    /* ------------- promotions and discounts ------------- */

    @GetMapping("/promotions")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<List<ProductPromotionOffer>> promotions(@RequestParam(required = false) Long hotelId) {
        MarketPlaceHotel hotel = hotelDataService.loadHotelForManage(user(), hotelId);
        return ApiResponse.ok(hotelDataService.loadPromotionsForHotel(hotel.getId(), true));
    }

    @GetMapping("/discounts")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<List<ProductPromotionOffer>> discounts(@RequestParam(required = false) Long hotelId) {
        MarketPlaceHotel hotel = hotelDataService.loadHotelForManage(user(), hotelId);
        return ApiResponse.ok(hotelDataService.loadPromotionsForHotel(hotel.getId(), false));
    }

    @PostMapping("/promotions-save")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<ProductPromotionOffer> promotionsSave(
            @Valid @RequestBody ProductPromotionOfferRequest request) {
        return ApiResponse.ok("Promotion saved", hotelDataService.saveProductPromotion(request, user()));
    }

    @GetMapping("/promotions-edit")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<ProductPromotionOffer> promotionsEdit(@RequestParam("pid") Long promotionId) {
        return ApiResponse.ok(hotelDataService.loadPromotion(promotionId));
    }

    /* ------------- policies and commissions ------------- */

    @GetMapping("/policies")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<List<HotelPolicy>> policies(@RequestParam(required = false) Long hotelId) {
        MarketPlaceHotel hotel = hotelDataService.loadHotelForManage(user(), hotelId);
        return ApiResponse.ok(hotelDataService.loadPoliciesForHotel(hotel.getId(), PolicyType.CANCELLATION));
    }

    @PostMapping("/policies-save")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<HotelPolicy> policiesSave(@Valid @RequestBody PolicyRequest request) {
        return ApiResponse.ok("Policy saved", hotelDataService.savePolicy(request, user()));
    }

    @GetMapping("/policies-edit")
    @PreAuthorize(EXTRANET_ROLES)
    public ApiResponse<HotelPolicy> policiesEdit(@RequestParam("pid") Long policyId) {
        return ApiResponse.ok(hotelDataService.loadPolicy(policyId));
    }

    @GetMapping("/commissions")
    @PreAuthorize(COMMISSION_ROLES)
    public ApiResponse<List<HotelPolicy>> commissions(@RequestParam(required = false) Long hotelId) {
        MarketPlaceHotel hotel = hotelDataService.loadHotelForManage(user(), hotelId);
        return ApiResponse.ok(hotelDataService.loadPoliciesForHotel(hotel.getId(), PolicyType.COMMISSION));
    }

    @PostMapping("/commissions-save")
    @PreAuthorize(COMMISSION_ROLES)
    public ApiResponse<HotelPolicy> commissionsSave(@Valid @RequestBody PolicyRequest request) {
        return ApiResponse.ok("Commission saved", hotelDataService.savePolicy(request, user()));
    }

    /* ------------- contracts (hotel-contracts-manage / contract-x) ------------- */

    @GetMapping("/hotel-contracts-manage")
    @PreAuthorize(CONTRACTS_ROLES)
    public ApiResponse<List<ContractSummaryDto>> hotelContractsManage(
            @RequestParam(required = false) Integer cityId) {
        return ApiResponse.ok(contractService.loadHotelContractsForManage(cityId));
    }

    @PostMapping("/contract-x")
    @PreAuthorize("hasAnyRole('ADMIN','PRODUCT','SUPERVISOR','BUSINESS_MANAGER')")
    public ApiResponse<Map<String, Object>> contractStep(@Valid @RequestBody ContractStepRequest request) {
        return ApiResponse.ok(contractService.moveContractStep(request, user()));
    }
}
