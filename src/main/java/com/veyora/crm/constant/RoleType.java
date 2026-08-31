package com.veyora.crm.constant;

import lombok.Getter;

@Getter
public enum RoleType {
    USER(0, "U"),
    ADMIN(1, "A"),
    SUPERVISOR(2, "V"),
    CALLCENTER(3, "T"),
    PRODUCT(4, "Z"),
    BUSINESS_MANAGER(5, "Z"),
    FINANCE(6, "Z"),
    EXPERT(7, "Z"),
    HOTELIER(8, "E"),
    TOUR_OPERATOR(9, "E"),
    TRAVEL_AGENT(10, "S"),
    GUEST_HOUSE(11, "E"),
    DESK_USER(12, "D"),
    CONTENT_WRITER(13, "Z");

    private final int id;
    private final String roleChar;

    RoleType(int id, String roleChar) {
        this.id = id;
        this.roleChar = roleChar;
    }

    public static RoleType fromId(Integer id) {
        if (id == null) {
            return USER;
        }
        for (RoleType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return USER;
    }
}
