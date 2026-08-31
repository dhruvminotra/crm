package com.veyora.crm.constant;

import lombok.Getter;

@Getter
public enum RoomOccupancy {
    SINGLE(1, 1, 0, 0),
    DOUBLE(2, 2, 0, 0),
    TRIPLE(3, 2, 1, 1),
    QUAD(4, 3, 1, 1),
    CUSTOM(-1, -1, -1, -1);

    private final int occupancy;
    private final int maxAdults;
    private final int maxChildWithMaxAdults;
    private final int maxChildWithoutBed;

    RoomOccupancy(int occupancy, int maxAdults, int maxChildWithMaxAdults, int maxChildWithoutBed) {
        this.occupancy = occupancy;
        this.maxAdults = maxAdults;
        this.maxChildWithMaxAdults = maxChildWithMaxAdults;
        this.maxChildWithoutBed = maxChildWithoutBed;
    }
}
