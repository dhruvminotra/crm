package com.veyora.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequest {

    private Long roomId;

    @NotNull
    private Long hotelId;

    @NotBlank
    private String roomName;

    private String description;
    private Integer maxOccupancy;
    private Integer maxAdults;
    private Integer totalRooms;
}
