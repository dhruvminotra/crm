package com.veyora.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Mirrors tf-main HOTEL_ROOM_ADD_UPDATE_SAVE. */
@Data
public class RoomRequest {

    private Long roomId; // null = create

    @NotNull
    private Long hotelId;

    @NotBlank
    private String roomName;

    private String description;
    private Integer maxOccupancy;
    private Integer maxAdults;
    private Integer totalRooms;
}
