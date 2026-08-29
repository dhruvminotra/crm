package com.veyora.crm.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

/** Mirrors the tf-main /hotels/update-inventory form (invUpdateForm). */
@Data
public class InventoryUpdateRequest {

    @NotNull
    private Long hotelId;

    @NotNull
    private Long roomId;

    /** Optional for system users acting on behalf of a supplier (tf-main userId param). */
    private Long supplierId;

    @NotNull
    private LocalDate fromDate;

    @NotNull
    private LocalDate toDate;

    /** Rooms allocated; null leaves allocation unchanged (close/open only). */
    private Integer numRooms;

    private Integer cutOffDays;

    /** Stop sell / open for sale without touching allocation (tf-main -2/-3 codes). */
    private Boolean closeForSale;
}
