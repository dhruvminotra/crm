package com.veyora.crm.controller;

import com.veyora.crm.constant.Constant;
import com.veyora.crm.dto.ApiResponse;
import com.veyora.crm.dto.CityRequest;
import com.veyora.crm.dto.CountryRequest;
import com.veyora.crm.dto.HotelRequest;
import com.veyora.crm.dto.RoomRequest;
import com.veyora.crm.dto.UserRequest;
import com.veyora.crm.entity.City;
import com.veyora.crm.entity.Country;
import com.veyora.crm.entity.User;
import com.veyora.crm.entity.HotelRoom;
import com.veyora.crm.entity.HotelSupplierMap;
import com.veyora.crm.entity.MarketPlaceHotel;
import com.veyora.crm.repository.UserRepository;
import com.veyora.crm.service.AdminService;
import com.veyora.crm.service.LocationService;
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
    private final LocationService locationService;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService, LocationService locationService,
            UserRepository userRepository) {
        this.adminService = adminService;
        this.locationService = locationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/countries")
    public ApiResponse<List<Country>> listCountries() {
        return ApiResponse.ok(locationService.listCountries());
    }

    @PostMapping("/countries")
    public ApiResponse<Country> saveCountry(@Valid @RequestBody CountryRequest request) {
        return ApiResponse.ok("Country saved", locationService.saveCountry(request));
    }

    @GetMapping("/cities")
    public ApiResponse<List<City>> listCities(@RequestParam(required = false) Integer countryId) {
        return ApiResponse.ok(locationService.listCities(countryId));
    }

    @PostMapping("/cities")
    public ApiResponse<City> saveCity(@Valid @RequestBody CityRequest request) {
        return ApiResponse.ok("City saved", locationService.saveCity(request));
    }

    @GetMapping("/hotels")
    public ApiResponse<List<MarketPlaceHotel>> listHotels() {
        return ApiResponse.ok(adminService.listHotels());
    }

    @PostMapping("/hotels")
    public ApiResponse<MarketPlaceHotel> saveHotel(@Valid @RequestBody HotelRequest request) {
        return ApiResponse.ok("Hotel saved", adminService.saveHotel(request));
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
