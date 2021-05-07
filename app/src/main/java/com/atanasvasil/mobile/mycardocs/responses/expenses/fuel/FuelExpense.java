package com.atanasvasil.mobile.mycardocs.responses.expenses.fuel;

import com.atanasvasil.mobile.mycardocs.responses.cars.Car;

import java.io.Serializable;
import java.sql.Timestamp;

public class FuelExpense implements Serializable {

    private String fuelExpenseId;
    private Double pricePerLitre;
    private Double litres;
    private Double discount;
    private Long mileage;
    private Car car;
    private Timestamp createdOn;
    private Timestamp editedOn;

    public String getFuelExpenseId() {
        return fuelExpenseId;
    }

    public void setFuelExpenseId(String fuelExpenseId) {
        this.fuelExpenseId = fuelExpenseId;
    }

    public Double getPricePerLitre() {
        return pricePerLitre;
    }

    public void setPricePerLitre(Double pricePerLitre) {
        this.pricePerLitre = pricePerLitre;
    }

    public Double getLitres() {
        return litres;
    }

    public void setLitres(Double litres) {
        this.litres = litres;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Long getMileage() {
        return mileage;
    }

    public void setMileage(Long mileage) {
        this.mileage = mileage;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getEditedOn() {
        return editedOn;
    }

    public void setEditedOn(Timestamp editedOn) {
        this.editedOn = editedOn;
    }
}
