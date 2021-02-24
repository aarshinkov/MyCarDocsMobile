package com.atanasvasil.mobile.mycardocs.responses.policies;

import com.atanasvasil.mobile.mycardocs.responses.cars.Car;

import java.io.Serializable;
import java.sql.Timestamp;

public class Policy implements Serializable {

    private String policyId;
    private String number;
    private Integer type;
    private String insName;
    private Car car;
    private Timestamp startDate;
    private Timestamp endDate;

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getInsName() {
        return insName;
    }

    public void setInsName(String insName) {
        this.insName = insName;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
}
