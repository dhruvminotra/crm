package com.veyora.crm.service;

import com.veyora.crm.dto.RoomRequest;
import com.veyora.crm.entity.User;
import com.veyora.crm.entity.HotelRoom;
import com.veyora.crm.entity.HotelSupplierMap;
import com.veyora.crm.entity.MarketPlaceHotel;
import com.veyora.crm.exceptionhandler.BadRequestException;
import com.veyora.crm.exceptionhandler.NotFoundException;
import com.veyora.crm.repository.UserRepository;
import com.veyora.crm.repository.HotelRoomRepository;
import com.veyora.crm.repository.HotelSupplierMapRepository;
import com.veyora.crm.repository.MarketPlaceHotelRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Admin features: hotel master, room master, users and supplier mappings
 * (the tf-main admin/product side of the extranet).
 */
@Service
public class AdminService {

    private final MarketPlaceHotelRepository hotelRepository;
    private final HotelRoomRepository hotelRoomRepository;
    private final HotelSupplierMapRepository hotelSupplierMapRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(MarketPlaceHotelRepository hotelRepository,
            HotelRoomRepository hotelRoomRepository,
            HotelSupplierMapRepository hotelSupplierMapRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.hotelRepository = hotelRepository;
        this.hotelRoomRepository = hotelRoomRepository;
        this.hotelSupplierMapRepository = hotelSupplierMapRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ---------------- hotels ---------------- */

    public List<MarketPlaceHotel> listHotels() {
        return hotelRepository.findByEnabledTrue();
    }

    @Transactional
    public MarketPlaceHotel saveHotel(MarketPlaceHotel hotel) {
        return hotelRepository.save(hotel);
    }

    /* ---------------- rooms (tf-main saveHotelRoomDetails) ---------------- */

    public List<HotelRoom> getRoomsByHotel(Long hotelId) {
        return hotelRoomRepository.findByHotelIdAndEnabledTrue(hotelId);
    }

    @Transactional
    public HotelRoom saveHotelRoomDetails(RoomRequest request) {
        HotelRoom room = request.getRoomId() != null
                ? hotelRoomRepository.findById(request.getRoomId())
                        .orElseThrow(() -> new NotFoundException("Room " + request.getRoomId() + " not found"))
                : new HotelRoom();
        hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new NotFoundException("Hotel " + request.getHotelId() + " not found"));

        room.setHotelId(request.getHotelId());
        room.setRoomName(request.getRoomName());
        room.setDescription(request.getDescription());
        room.setMaxOccupancy(request.getMaxOccupancy());
        room.setMaxAdults(request.getMaxAdults());
        room.setTotalRooms(request.getTotalRooms());
        return hotelRoomRepository.save(room);
    }

    /* ---------------- supplier mapping (tf-main MAP_HOTEL) ---------------- */

    @Transactional
    public HotelSupplierMap mapHotelToSupplier(Long hotelId, Long supplierId, String mapType) {
        String type = mapType != null ? mapType : HotelSupplierMap.TYPE_SUPPLIER;
        hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NotFoundException("Hotel " + hotelId + " not found"));
        userRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("User " + supplierId + " not found"));
        if (hotelSupplierMapRepository.existsByHotelIdAndSupplierIdAndMapType(hotelId, supplierId, type)) {
            throw new BadRequestException("Mapping already exists");
        }
        HotelSupplierMap map = new HotelSupplierMap();
        map.setHotelId(hotelId);
        map.setSupplierId(supplierId);
        map.setMapType(type);
        return hotelSupplierMapRepository.save(map);
    }

    public List<HotelSupplierMap> getMappingsForHotel(Long hotelId) {
        return hotelSupplierMapRepository.findByHotelIdAndMapType(hotelId, HotelSupplierMap.TYPE_SUPPLIER);
    }

    /* ---------------- users ---------------- */

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User saveUser(User user, String rawPassword) {
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        return userRepository.save(user);
    }
}
