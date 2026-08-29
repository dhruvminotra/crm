package com.veyora.crm.constant;

import lombok.Getter;

@Getter
public enum MealPlan {
    EP("EP", "Room Only"),
    CP("CP", "Breakfast Included"),
    MAP("MAP", "Breakfast + One Major Meal"),
    AP("AP", "All Meals"),
    AI("AI", "All Inclusive");

    private final String code;
    private final String displayName;

    MealPlan(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
}
