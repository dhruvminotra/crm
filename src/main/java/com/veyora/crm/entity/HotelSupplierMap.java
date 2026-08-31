package com.veyora.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "city_id")
    private Integer cityId;

    @Column(name = "hotel_name", length = 200)
    private String hotelName;
}
