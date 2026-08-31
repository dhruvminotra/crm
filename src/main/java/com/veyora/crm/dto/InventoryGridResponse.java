package com.veyora.crm.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InventoryGridResponse {

    private final Long hotelId;
    private final List<java.time.LocalDate> dates;

    private final Map<Long, String> rooms;

    private final Map<Long, List<InventoryCellDto>> inventory;

    private final Map<Long, List<SupplierPackagePricingDto>> rates;
}
