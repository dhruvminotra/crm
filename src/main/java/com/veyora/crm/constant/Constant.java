package com.veyora.crm.constant;

public final class Constant {

    private Constant() {
    }

    public static final String API_V1 = "/api/v1";
    public static final String EXTRANET = API_V1 + "/extranet";
    public static final String ADMIN = API_V1 + "/admin";
    public static final String AUTH = API_V1 + "/auth";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final int CONTRACT_SUMMARY_WINDOW_DAYS = 180;
}
