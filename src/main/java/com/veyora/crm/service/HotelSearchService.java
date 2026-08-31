package com.veyora.crm.service;

import com.veyora.crm.constant.MealPlan;
import com.veyora.crm.dto.HotelSearchResultDto;
import com.veyora.crm.dto.HotelSearchRoomOptionDto;
import com.veyora.crm.entity.City;
import com.veyora.crm.entity.Country;
import com.veyora.crm.entity.HotelRoom;
import com.veyora.crm.entity.MarketPlaceHotel;
import com.veyora.crm.entity.PackageInventory;
import com.veyora.crm.entity.RatePlan;
import com.veyora.crm.entity.SupplierPackagePricing;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.repository.CityRepository;
import com.veyora.crm.repository.CountryRepository;
import com.veyora.crm.repository.HotelRoomRepository;
import com.veyora.crm.repository.MarketPlaceHotelRepository;
import com.veyora.crm.repository.PackageInventoryRepository;
import com.veyora.crm.repository.RatePlanRepository;
import com.veyora.crm.repository.SupplierPackagePricingRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Searches the hotel/room/rate/inventory data that suppliers already enter through the extranet
 * (Rate Plans + Rates + Inventory pages), instead of calling out to any live supplier API -
 * this system has no such integration, only the contracted inventory those pages capture.
 */
@Service
public class HotelSearchService {

    private final MarketPlaceHotelRepository hotelRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final HotelRoomRepository hotelRoomRepository;
    private final RatePlanRepository ratePlanRepository;
    private final SupplierPackagePricingRepository pricingRepository;
    private final PackageInventoryRepository inventoryRepository;

    public HotelSearchService(MarketPlaceHotelRepository hotelRepository,
            CityRepository cityRepository,
            CountryRepository countryRepository,
            HotelRoomRepository hotelRoomRepository,
            RatePlanRepository ratePlanRepository,
            SupplierPackagePricingRepository pricingRepository,
            PackageInventoryRepository inventoryRepository) {
        this.hotelRepository = hotelRepository;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.hotelRoomRepository = hotelRoomRepository;
        this.ratePlanRepository = ratePlanRepository;
        this.pricingRepository = pricingRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<HotelSearchResultDto> search(Integer cityId, LocalDate checkIn, LocalDate checkOut,
            int adults, int rooms) {
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new BadRequestException("checkOut must be after checkIn");
        }
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        LocalDate lastNight = checkOut.minusDays(1);

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new BadRequestException("City " + cityId + " not found"));
        Country country = countryRepository.findById(city.getCountryId()).orElse(null);

        List<HotelSearchResultDto> results = new ArrayList<>();
        for (MarketPlaceHotel hotel : hotelRepository.findByCityIdAndEnabledTrue(cityId)) {
            HotelSearchResultDto result = searchHotel(hotel, city, country, checkIn, checkOut,
                    lastNight, nights, adults, rooms);
            if (result != null) {
                results.add(result);
            }
        }
        results.sort(Comparator.comparing(HotelSearchResultDto::getLowestTotalPrice));
        return results;
    }

    private HotelSearchResultDto searchHotel(MarketPlaceHotel hotel, City city, Country country,
            LocalDate checkIn, LocalDate checkOut, LocalDate lastNight, int nights, int adults, int rooms) {
        Map<Long, HotelRoom> roomsById = hotelRoomRepository.findByHotelIdAndEnabledTrue(hotel.getId())
                .stream().collect(Collectors.toMap(HotelRoom::getId, r -> r));
        if (roomsById.isEmpty()) {
            return null;
        }

        Map<Long, SupplierPackagePricing> pricingByRatePlan = pricingRepository
                .findOverlapping(hotel.getId(), checkIn, lastNight).stream()
                .filter(p -> p.isAudited())
                .filter(p -> !p.getTravelStartDate().isAfter(checkIn) && !p.getTravelEndDate().isBefore(lastNight))
                .collect(Collectors.toMap(SupplierPackagePricing::getRatePlanId, p -> p, (a, b) -> a));

        List<HotelSearchRoomOptionDto> options = new ArrayList<>();
        for (RatePlan plan : ratePlanRepository.findByHotelIdAndEnabledTrue(hotel.getId())) {
            HotelRoom room = roomsById.get(plan.getRoomId());
            if (room == null || plan.getMaxAdults() < adults) {
                continue;
            }
            SupplierPackagePricing pricing = pricingByRatePlan.get(plan.getId());
            if (pricing == null) {
                continue;
            }
            int available = availableRoomsForStay(hotel.getId(), plan.getRoomId(), plan.getSupplierId(),
                    checkIn, lastNight);
            if (available < rooms) {
                continue;
            }

            BigDecimal perNight = pricing.getTwinSharing() != null
                    ? pricing.getTwinSharing() : pricing.getSingleSharing();
            if (perNight == null) {
                continue;
            }

            HotelSearchRoomOptionDto option = new HotelSearchRoomOptionDto();
            option.setRatePlanId(plan.getId());
            option.setRoomId(room.getId());
            option.setRoomName(room.getRoomName());
            option.setPlanName(plan.getPlanName());
            option.setMealPlan(plan.getMealPlan() != null ? plan.getMealPlan().getCode() : null);
            option.setMealPlanDisplay(plan.getMealPlan() != null ? plan.getMealPlan().getDisplayName() : null);
            option.setCurrency(pricing.getCurrency());
            option.setPerNightPrice(perNight);
            option.setTotalPrice(perNight.multiply(BigDecimal.valueOf((long) nights * rooms)));
            option.setRefundable(plan.getCancellationPolicyJson() == null);
            option.setAvailableRooms(available);
            options.add(option);
        }

        if (options.isEmpty()) {
            return null;
        }
        options.sort(Comparator.comparing(HotelSearchRoomOptionDto::getTotalPrice));

        HotelSearchResultDto result = new HotelSearchResultDto();
        result.setHotelId(hotel.getId());
        result.setHotelName(hotel.getName());
        result.setCityId(city.getId());
        result.setCityName(city.getName());
        result.setCountryCode(country != null ? country.getCode() : hotel.getCountryCode());
        result.setCountryName(country != null ? country.getName() : null);
        result.setStarRating(hotel.getStarRating());
        result.setNights(nights);
        result.setCurrency(options.get(0).getCurrency());
        result.setLowestTotalPrice(options.get(0).getTotalPrice());
        result.setLowestPerNightPrice(options.get(0).getPerNightPrice());
        result.setRoomOptions(options);
        return result;
    }

    /** Returns the lowest per-night available room count across the stay, or 0 if any night is missing/closed. */
    private int availableRoomsForStay(Long hotelId, Long roomId, Long supplierId,
            LocalDate checkIn, LocalDate lastNight) {
        List<PackageInventory> nightsInventory = inventoryRepository
                .findByHotelIdAndRoomIdAndSupplierIdAndStayDateBetween(hotelId, roomId, supplierId,
                        checkIn, lastNight);
        int expectedNights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, lastNight) + 1;
        if (nightsInventory.size() < expectedNights) {
            return 0;
        }
        int min = Integer.MAX_VALUE;
        for (PackageInventory inv : nightsInventory) {
            if (inv.isCloseForSale()) {
                return 0;
            }
            min = Math.min(min, inv.getAllocated() - inv.getSold());
        }
        return Math.max(min, 0);
    }
}
