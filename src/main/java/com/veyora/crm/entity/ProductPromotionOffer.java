package com.veyora.crm.entity;

import com.veyora.crm.constant.DiscountType;
import com.veyora.crm.constant.ProductPromotionOfferStatus;
import com.veyora.crm.constant.ProductPromotionOfferType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_promotion_offer")
public class ProductPromotionOffer extends BaseEntity {

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductPromotionOfferType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 50)
    private String category;

    @Column(name = "offer_start_date", nullable = false)
    private LocalDate offerStartDate;

    @Column(name = "offer_end_date", nullable = false)
    private LocalDate offerEndDate;

    @Column(name = "travel_start_date", nullable = false)
    private LocalDate travelStartDate;

    @Column(name = "travel_end_date", nullable = false)
    private LocalDate travelEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType = DiscountType.PERCENTAGE;

    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "room_ids", length = 500)
    private String roomIds;

    @Column(name = "meal_plans", length = 100)
    private String mealPlans;

    @Column(name = "min_duration")
    private Integer minDuration;

    @Column(name = "days_in_advance")
    private Integer daysInAdvance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductPromotionOfferStatus status = ProductPromotionOfferStatus.ENABLED;
}
