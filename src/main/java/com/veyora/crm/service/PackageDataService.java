package com.veyora.crm.service;

import com.veyora.crm.dto.InventoryCellDto;
import com.veyora.crm.dto.InventoryGridResponse;
import com.veyora.crm.dto.InventoryUpdateRequest;
import com.veyora.crm.dto.SupplierPackagePricingDto;
import com.veyora.crm.entity.User;
import com.veyora.crm.entity.HotelRoom;
import com.veyora.crm.entity.PackageInventory;
import com.veyora.crm.entity.SupplierPackagePricing;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.repository.HotelRoomRepository;
import com.veyora.crm.repository.PackageInventoryRepository;
import com.veyora.crm.repository.SupplierPackagePricingRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inventory management, mirroring tf-main PackageDataBean
 * (addOrUpdateHotelRoomInventoryForDates) and the inventory-x grid load.
 */
@Service
public class PackageDataService {

    private static final Logger log = LoggerFactory.getLogger(PackageDataService.class);

    /** Days shown per page of the manage-inventory grid, as in the tf-main UI. */
    public static final int GRID_DAYS = 14;

    private final PackageInventoryRepository inventoryRepository;
    private final HotelRoomRepository hotelRoomRepository;
    private final SupplierPackagePricingRepository pricingRepository;
    private final HotelDataService hotelDataService;

    public PackageDataService(PackageInventoryRepository inventoryRepository,
            HotelRoomRepository hotelRoomRepository,
            SupplierPackagePricingRepository pricingRepository,
            HotelDataService hotelDataService) {
        this.inventoryRepository = inventoryRepository;
        this.hotelRoomRepository = hotelRoomRepository;
        this.pricingRepository = pricingRepository;
        this.hotelDataService = hotelDataService;
    }

    /**
     * Upsert per-day inventory over a date range
     * (tf-main updateHotelInventory -> addOrUpdateHotelRoomInventoryForDates).
     */
    @Transactional
    public void updateHotelInventory(InventoryUpdateRequest request, User user) {
        if (request.getToDate().isBefore(request.getFromDate())) {
            throw new BadRequestException("toDate must not be before fromDate");
        }
        Long supplierId = hotelDataService.getApplicableSupplierId(user, request.getSupplierId());

        List<PackageInventory> existing = inventoryRepository
                .findByHotelIdAndRoomIdAndSupplierIdAndStayDateBetween(
                        request.getHotelId(), request.getRoomId(), supplierId,
                        request.getFromDate(), request.getToDate());
        Map<LocalDate, PackageInventory> byDate = existing.stream()
                .collect(Collectors.toMap(PackageInventory::getStayDate, Function.identity()));

        List<PackageInventory> toSave = new ArrayList<>();
        for (LocalDate d = request.getFromDate(); !d.isAfter(request.getToDate()); d = d.plusDays(1)) {
            PackageInventory inv = byDate.get(d);
            if (inv == null) {
                inv = new PackageInventory();
                inv.setHotelId(request.getHotelId());
                inv.setRoomId(request.getRoomId());
                inv.setSupplierId(supplierId);
                inv.setStayDate(d);
            }
            if (request.getNumRooms() != null) {
                inv.setAllocated(request.getNumRooms());
            }
            if (request.getCutOffDays() != null) {
                inv.setCutOffDays(request.getCutOffDays());
            }
            if (request.getCloseForSale() != null) {
                inv.setCloseForSale(request.getCloseForSale());
            }
            toSave.add(inv);
        }
        inventoryRepository.saveAll(toSave);
        log.debug("Updated inventory for hotel {} room {} over {} days",
                request.getHotelId(), request.getRoomId(), toSave.size());
    }

    /**
     * The manage-inventory grid: rooms x dates with inventory cells and the
     * rates overlapping the window (tf-main loadHotelInventoryAndRates / inventory-x).
     */
    public InventoryGridResponse loadHotelInventoryAndRates(Long hotelId, Long supplierId,
            LocalDate fromDate) {
        LocalDate start = fromDate != null ? fromDate : LocalDate.now();
        LocalDate end = start.plusDays(GRID_DAYS - 1L);

        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            dates.add(d);
        }

        List<HotelRoom> rooms = hotelRoomRepository.findByHotelIdAndEnabledTrue(hotelId);
        Map<Long, String> roomNames = rooms.stream().collect(Collectors.toMap(
                HotelRoom::getId, HotelRoom::getRoomName, (a, b) -> a, LinkedHashMap::new));

        List<PackageInventory> inventories = inventoryRepository
                .findByHotelIdAndSupplierIdAndStayDateBetween(hotelId, supplierId, start, end);
        Map<Long, Map<LocalDate, PackageInventory>> invByRoom = new HashMap<>();
        for (PackageInventory inv : inventories) {
            invByRoom.computeIfAbsent(inv.getRoomId(), k -> new HashMap<>()).put(inv.getStayDate(), inv);
        }

        Map<Long, List<InventoryCellDto>> inventoryGrid = new LinkedHashMap<>();
        for (HotelRoom room : rooms) {
            Map<LocalDate, PackageInventory> roomInv = invByRoom.getOrDefault(room.getId(), Map.of());
            List<InventoryCellDto> cells = new ArrayList<>();
            for (LocalDate d : dates) {
                PackageInventory inv = roomInv.get(d);
                cells.add(inv != null
                        ? new InventoryCellDto(d, inv.getAllocated(), inv.getSold(),
                                inv.getCutOffDays(), inv.isCloseForSale())
                        : new InventoryCellDto(d, 0, 0, null, false));
            }
            inventoryGrid.put(room.getId(), cells);
        }

        Map<Long, List<SupplierPackagePricingDto>> rates = pricingRepository
                .findOverlapping(hotelId, start, end).stream()
                .map(SupplierPackagePricingDto::new)
                .collect(Collectors.groupingBy(SupplierPackagePricingDto::getRatePlanId));

        return new InventoryGridResponse(hotelId, dates, roomNames, inventoryGrid, rates);
    }

    /**
     * City-wide inventory position for internal users
     * (tf-main loadHotelInventoryPosition / inventory-z).
     */
    public Map<Long, List<InventoryCellDto>> loadHotelInventoryPosition(List<Long> hotelIds,
            LocalDate fromDate) {
        LocalDate start = fromDate != null ? fromDate : LocalDate.now();
        LocalDate end = start.plusDays(GRID_DAYS - 1L);
        List<PackageInventory> inventories = inventoryRepository
                .findByHotelIdInAndStayDateBetween(hotelIds, start, end);

        Map<Long, List<InventoryCellDto>> byHotel = new HashMap<>();
        for (PackageInventory inv : inventories) {
            byHotel.computeIfAbsent(inv.getHotelId(), k -> new ArrayList<>())
                    .add(new InventoryCellDto(inv.getStayDate(), inv.getAllocated(), inv.getSold(),
                            inv.getCutOffDays(), inv.isCloseForSale()));
        }
        return byHotel;
    }
}
