package com.veyora.crm.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The manage-inventory grid: rooms x dates with rates per rate plan
 * (tf-main loadHotelInventoryAndRates / inventory-x).
 */
@Getter
@AllArgsConstructor
public class InventoryGridResponse {

    private final Long hotelId;
    private final List<java.time.LocalDate> dates;

    /** roomId -> room name. */
    private final Map<Long, String> rooms;

    /** roomId -> inventory cells for the window. */
    private final Map<Long, List<InventoryCellDto>> inventory;

    /** ratePlanId -> overlapping rates for the window. */
    private final Map<Long, List<SupplierPackagePricingDto>> rates;
}
