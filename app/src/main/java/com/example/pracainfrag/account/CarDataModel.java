package com.example.pracainfrag.account;

import java.io.Serializable;

public class CarDataModel implements Serializable {

    String brand;
    String model;
    String carType;
    String carColor;
    private String ownerID;

    public CarDataModel() {
    }

    public CarDataModel(String brand, String model, String carColor, String carType, String ownerID) {
        this.brand = brand;
        this.model = model;
        this.carType = carType;
        this.carColor = carColor;
        this.ownerID = ownerID;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getCarType() {
        return carType;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

}
