package com.veyora.crm.controller;

import com.veyora.crm.constant.Constant;
import com.veyora.crm.constant.RoleType;
import com.veyora.crm.dto.ApiResponse;
import com.veyora.crm.entity.MarketPlaceHotel;
import com.veyora.crm.entity.User;
import com.veyora.crm.repository.MarketPlaceHotelRepository;
import com.veyora.crm.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.API_V1 + "/gen")
public class GenController {

    private static final List<RoleType> SUPPLIER_ROLES =
            List.of(RoleType.HOTELIER, RoleType.TOUR_OPERATOR, RoleType.GUEST_HOUSE);

    private final MarketPlaceHotelRepository hotelRepository;
    private final UserRepository userRepository;

    public GenController(MarketPlaceHotelRepository hotelRepository, UserRepository userRepository) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/msc/city-suggest")
    public ApiResponse<List<Map<String, Object>>> citySuggest(@RequestParam("q") String query) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (Object[] row : hotelRepository.suggestCities(query.trim().toLowerCase() + "%")) {
            results.add(Map.of("id", row[0], "name", row[1]));
        }
        return ApiResponse.ok(results);
    }

    @GetMapping("/msc/hotel-suggest")
    public ApiResponse<List<Map<String, Object>>> hotelSuggest(@RequestParam("q") String query,
            @RequestParam(name = "city", required = false) Integer cityId) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (MarketPlaceHotel h : hotelRepository
                .findTop10ByNameStartingWithIgnoreCaseAndEnabledTrue(query.trim())) {
            if (cityId == null || cityId <= 0 || cityId.equals(h.getCityId())) {
                results.add(Map.of("id", h.getId(), "name", h.getName(),
                        "cityName", h.getCityName() != null ? h.getCityName() : ""));
            }
        }
        return ApiResponse.ok(results);
    }

    @GetMapping("/usermanage/suggest-users")
    public ApiResponse<List<Map<String, Object>>> suggestUsers(@RequestParam("q") String query,
            @RequestParam(name = "supRls", defaultValue = "true") boolean supplierRoles) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (User u : userRepository.suggestUsers(SUPPLIER_ROLES, query.trim().toLowerCase() + "%")) {
            results.add(Map.of("id", u.getUserId(), "name", u.getName(),
                    "email", u.getEmail() != null ? u.getEmail() : ""));
        }
        return ApiResponse.ok(results);
    }
}
