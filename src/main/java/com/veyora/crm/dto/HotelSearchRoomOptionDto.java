package com.veyora.crm.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class HotelSearchRoomOptionDto {

    private Long ratePlanId;
    private Long roomId;
    private String roomName;
    private String planName;
    private String mealPlan;
    private String mealPlanDisplay;
    private String currency;
    private BigDecimal perNightPrice;
    private BigDecimal totalPrice;
    private boolean refundable;
    private int availableRooms;
}
