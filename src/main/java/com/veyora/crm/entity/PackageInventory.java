package com.veyora.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Per-day room allocation, the equivalent of tf-main PackageInventory
 * (written by addOrUpdateHotelRoomInventoryForDates).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "package_inventory",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "room_id", "supplier_id", "stay_date"}))
public class PackageInventory extends BaseEntity {

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "stay_date", nullable = false)
    private LocalDate stayDate;

    /** Number of rooms allocated for sale on this date. */
    @Column(nullable = false)
    private int allocated;

    @Column(nullable = false)
    private int sold;

    /** Booking cut-off in days for this date. */
    @Column(name = "cut_off_days")
    private Integer cutOffDays;

    /** Stop-sell flag without changing the allocation (tf-main close-for-sale). */
    @Column(name = "close_for_sale", nullable = false)
    private boolean closeForSale = false;
}
