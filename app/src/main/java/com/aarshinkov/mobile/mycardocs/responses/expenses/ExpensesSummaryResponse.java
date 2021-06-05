package com.aarshinkov.mobile.mycardocs.responses.expenses;

import java.io.Serializable;
import java.util.List;

public class ExpensesSummaryResponse implements Serializable {

    private String userId;
    private String carId;
    private Integer year;
    private List<ExpenseSummaryItem> fuel;
    private List<ExpenseSummaryItem> service;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<ExpenseSummaryItem> getFuel() {
        return fuel;
    }

    public void setFuel(List<ExpenseSummaryItem> fuel) {
        this.fuel = fuel;
    }

    public List<ExpenseSummaryItem> getService() {
        return service;
    }

    public void setService(List<ExpenseSummaryItem> service) {
        this.service = service;
    }
}
