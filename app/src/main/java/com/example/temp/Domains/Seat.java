package com.example.temp.Domains;

public class Seat {
    // Enum for seat status
    public enum SeatStatus {
        AVAILABLE,
        SELECTED,
        UNAVAILABLE
    }

    private String seatId;
    private SeatStatus status;

    // Constructor
    public Seat(String seatId, SeatStatus status) {
        this.seatId = seatId;
        this.status = status;
    }

    // Getters and Setters
    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    // Method to get seat name (assuming seat name is the same as seatId)
    public String getName() {
        return seatId;
    }
}