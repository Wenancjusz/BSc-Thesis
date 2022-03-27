package com.example.pracainfrag.adverts;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class AdvertModel implements Serializable {


    private String departureCity;
    private String destinationCity;
    private com.google.firebase.Timestamp departureTime;

    private String advertID;
    private String driverID;
    private String brand;
    private String model;
    private String color;
    private String driver;
    private String type;

    private String driverPhone;
    private int price, seats;

    public AdvertModel(String advertID, String driverID, String departureCity, String destinationCity,
                       com.google.firebase.Timestamp departureTime, String driverPhone, String brand, String model, String color,
                       String driver, String type, int price, int seats){
        this.advertID = advertID;
        this.driverID = driverID;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.driver = driver;
        this.price = price;
        this.seats = seats;
        this.type = type;
        this.departureTime = departureTime;
        this.driverPhone = driverPhone;
    }

    public AdvertModel(){

    }



    public String getDriverPhone() { return driverPhone; }

    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public String getDriverID() {
        return driverID;
    }

    public String getId() {
        return advertID;
    }

    public com.google.firebase.Timestamp getDepartureTime(){return departureTime;}

    public String getDepartureCity() {
        return departureCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public String getType() {
        return type;
    }

    public String getDriver() {
        return driver;
    }

    public int getPrice() {
        return price;
    }

    public int getSeats() {
        return seats;
    }


    public void setId(String advertID) { this.advertID = advertID; }

    public void setDepartureTime(Timestamp departureTime){ this.departureTime = departureTime;}

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

}
