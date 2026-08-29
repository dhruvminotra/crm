package com.veyora.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Room master, equivalent of tf-main HotelRoom. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "hotel_room")
public class HotelRoom extends BaseEntity {

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "room_name", nullable = false, length = 150)
    private String roomName;

    @Column(length = 1000)
    private String description;

    @Column(name = "max_occupancy")
    private Integer maxOccupancy;

    @Column(name = "max_adults")
    private Integer maxAdults;

    @Column(name = "total_rooms")
    private Integer totalRooms;

    @Column(nullable = false)
    private boolean enabled = true;
}
