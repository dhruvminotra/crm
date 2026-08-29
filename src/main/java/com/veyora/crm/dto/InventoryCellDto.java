package com.veyora.crm.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** One date cell in the manage-inventory grid. */
@Getter
@AllArgsConstructor
public class InventoryCellDto {

    private final LocalDate date;
    private final int allocated;
    private final int sold;
    private final Integer cutOffDays;
    private final boolean closeForSale;
}
