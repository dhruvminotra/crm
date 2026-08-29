package com.veyora.crm.controller;

import com.veyora.crm.constant.Constant;
import com.veyora.crm.dto.ApiResponse;
import com.veyora.crm.dto.RoomRequest;
import com.veyora.crm.dto.UserRequest;
import com.veyora.crm.entity.AppUser;
import com.veyora.crm.entity.HotelRoom;
import com.veyora.crm.entity.HotelSupplierMap;
import com.veyora.crm.entity.MarketPlaceHotel;
import com.veyora.crm.repository.AppUserRepository;
import com.veyora.crm.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin backend: hotel master, room master, supplier mappings and users.
 * Room save mirrors tf-main HOTEL_ROOM_ADD_UPDATE_SAVE; mapping mirrors MAP_HOTEL.
 */
@RestController
@RequestMapping(Constant.ADMIN)
@PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','PRODUCT','BUSINESS_MANAGER')")
public class AdminController {

    private final AdminService adminService;
    private final AppUserRepository appUserRepository;

    public AdminController(AdminService adminService, AppUserRepository appUserRepository) {
        this.adminService = adminService;
        this.appUserRepository = appUserRepository;
    }

    /* ---------------- hotels ---------------- */

    @GetMapping("/hotels")
    public ApiResponse<List<MarketPlaceHotel>> listHotels() {
        return ApiResponse.ok(adminService.listHotels());
    }

    @PostMapping("/hotels")
    public ApiResponse<MarketPlaceHotel> saveHotel(@RequestBody MarketPlaceHotel hotel) {
        return ApiResponse.ok("Hotel saved", adminService.saveHotel(hotel));
    }

    /* ---------------- rooms ---------------- */

    @GetMapping("/rooms")
    public ApiResponse<List<HotelRoom>> rooms(@RequestParam Long hotelId) {
        return ApiResponse.ok(adminService.getRoomsByHotel(hotelId));
    }

    @PostMapping("/rooms")
    public ApiResponse<HotelRoom> saveRoom(@Valid @RequestBody RoomRequest request) {
        return ApiResponse.ok("Room saved", adminService.saveHotelRoomDetails(request));
    }

    /* ---------------- supplier mapping (MAP_HOTEL) ---------------- */

    @PostMapping("/map-hotel")
    public ApiResponse<HotelSupplierMap> mapHotel(@RequestParam Long hotelId,
            @RequestParam Long supplierId,
            @RequestParam(required = false) String mapType) {
        return ApiResponse.ok("Hotel mapped", adminService.mapHotelToSupplier(hotelId, supplierId, mapType));
    }

    @GetMapping("/hotel-mappings")
    public ApiResponse<List<HotelSupplierMap>> hotelMappings(@RequestParam Long hotelId) {
        return ApiResponse.ok(adminService.getMappingsForHotel(hotelId));
    }

    /* ---------------- users ---------------- */

    @GetMapping("/users")
    public ApiResponse<List<AppUser>> users() {
        return ApiResponse.ok(adminService.listUsers());
    }

    @PostMapping("/users")
    public ApiResponse<AppUser> saveUser(@Valid @RequestBody UserRequest request) {
        AppUser user = request.getUserId() != null
                ? appUserRepository.findById(request.getUserId()).orElse(new AppUser())
                : new AppUser();
        user.setEmail(request.getEmail());
        user.setDisplayName(request.getDisplayName());
        user.setRole(request.getRole());
        user.setBusinessCurrency(request.getBusinessCurrency());
        return ApiResponse.ok("User saved", adminService.saveUser(user, request.getPassword()));
    }
}
