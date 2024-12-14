package com.example.temp.Domains;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Booking {
    private String id;
    private String name;
    private String username;
    private String filmName;
    private String selectedDate;
    private String selectedTime;
    private ArrayList<String> selectedSeats;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }

    public ArrayList<String> getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(ArrayList<String> selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private double price;
    private double discount;
    private double totalPrice;
    private String status; // e.g., "pending", "confirmed", "paid"

    // Default constructor required for Firebase
    public Booking() {}

    // Constructor with all fields
    public Booking(String name, String username, String filmName, String selectedDate, String selectedTime, ArrayList<String> selectedSeats, double price, double discount) {
        this.name = name;
        this.username = username;
        this.filmName = filmName;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
        this.selectedSeats = selectedSeats;
        this.price = price;
        this.discount = discount;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    // Convert price to VND
    public double getPriceInVND() {
        return this.price * 24000; // Assuming current exchange rate
    }

    public double getDiscountInVND() {
        return this.discount * 24000;
    }

    public double getTotalPriceInVND() {
        return this.totalPrice * 24000;
    }
}