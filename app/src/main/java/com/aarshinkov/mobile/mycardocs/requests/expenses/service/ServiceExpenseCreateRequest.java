package com.aarshinkov.mobile.mycardocs.requests.expenses.service;

import java.io.Serializable;

public class ServiceExpenseCreateRequest implements Serializable {

    private Integer type;
    private String carId;
    private Double price;
    private String notes;
    private Long mileage;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getMileage() {
        return mileage;
    }

    public void setMileage(Long mileage) {
        this.mileage = mileage;
    }
}
