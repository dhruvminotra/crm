package com.veyora.crm.controller;

import com.veyora.crm.constant.Constant;
import com.veyora.crm.dto.ApiResponse;
import com.veyora.crm.dto.RoomRequest;
import com.veyora.crm.dto.UserRequest;
import com.veyora.crm.entity.User;
import com.veyora.crm.entity.HotelRoom;
import com.veyora.crm.entity.HotelSupplierMap;
import com.veyora.crm.entity.MarketPlaceHotel;
import com.veyora.crm.repository.UserRepository;
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

@RestController
@RequestMapping(Constant.ADMIN)
@PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','PRODUCT','BUSINESS_MANAGER')")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService, UserRepository userRepository) {
        this.adminService = adminService;
        this.userRepository = userRepository;
    }

    @GetMapping("/hotels")
    public ApiResponse<List<MarketPlaceHotel>> listHotels() {
        return ApiResponse.ok(adminService.listHotels());
    }

    @PostMapping("/hotels")
    public ApiResponse<MarketPlaceHotel> saveHotel(@RequestBody MarketPlaceHotel hotel) {
        return ApiResponse.ok("Hotel saved", adminService.saveHotel(hotel));
    }

    @GetMapping("/rooms")
    public ApiResponse<List<HotelRoom>> rooms(@RequestParam Long hotelId) {
        return ApiResponse.ok(adminService.getRoomsByHotel(hotelId));
    }

    @PostMapping("/rooms")
    public ApiResponse<HotelRoom> saveRoom(@Valid @RequestBody RoomRequest request) {
        return ApiResponse.ok("Room saved", adminService.saveHotelRoomDetails(request));
    }

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

    @GetMapping("/users")
    public ApiResponse<List<User>> users() {
        return ApiResponse.ok(adminService.listUsers());
    }

    @PostMapping("/users")
    public ApiResponse<User> saveUser(@Valid @RequestBody UserRequest request) {
        User user = request.getUserId() != null
                ? userRepository.findById(request.getUserId()).orElse(new User())
                : new User();
        user.setEmail(request.getEmail());
        user.setName(request.getDisplayName());
        user.setRoleType(request.getRole());
        user.setRole(request.getRole().getRoleChar());
        user.setBaseCurrency(request.getBusinessCurrency());
        return ApiResponse.ok("User saved", adminService.saveUser(user, request.getPassword()));
    }
}
