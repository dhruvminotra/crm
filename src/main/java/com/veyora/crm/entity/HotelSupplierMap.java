package com.veyora.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Hotel-to-supplier ownership mapping - the prerequisite for a hotel to be
 * manageable in the extranet (tf-main HotelSupplierMap).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "hotel_supplier_map",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "supplier_id", "map_type"}))
public class HotelSupplierMap extends BaseEntity {

    public static final String TYPE_SUPPLIER = "SUPPLIER";
    public static final String TYPE_CONTRACTING = "CONTRACTING";

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "map_type", nullable = false, length = 20)
    private String mapType = TYPE_SUPPLIER;

    /** City of the mapped hotel (tf-main map.setCityId). */
    @Column(name = "city_id")
    private Integer cityId;

    /** Denormalized hotel name (tf-main map.setHotelName). */
    @Column(name = "hotel_name", length = 200)
    private String hotelName;
}
