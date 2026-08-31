package com.veyora.crm.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class InventoryUpdateRequest {

    @NotNull
    private Long hotelId;

    @NotNull
    private Long roomId;

    private Long supplierId;

    @NotNull
    private LocalDate fromDate;

    @NotNull
    private LocalDate toDate;

    private Integer numRooms;

    private Integer cutOffDays;

    private Boolean closeForSale;
}
