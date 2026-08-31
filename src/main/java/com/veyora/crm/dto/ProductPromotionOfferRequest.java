package com.veyora.crm.dto;

import com.veyora.crm.constant.DiscountType;
import com.veyora.crm.constant.ProductPromotionOfferStatus;
import com.veyora.crm.constant.ProductPromotionOfferType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class ProductPromotionOfferRequest {

    private Long promotionId;

    @NotNull
    private Long hotelId;

    private Long supplierId;

    @NotNull
    private ProductPromotionOfferType type;

    @NotBlank
    private String title;

    private String category;

    @NotNull
    private LocalDate offerStartDate;

    @NotNull
    private LocalDate offerEndDate;

    @NotNull
    private LocalDate travelStartDate;

    @NotNull
    private LocalDate travelEndDate;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private BigDecimal discountValue;

    private List<Long> roomIds;
    private List<String> mealPlans;
    private Integer minDuration;
    private Integer daysInAdvance;

    private ProductPromotionOfferStatus status;
}
