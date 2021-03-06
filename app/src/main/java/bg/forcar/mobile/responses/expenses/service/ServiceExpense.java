package bg.forcar.mobile.responses.expenses.service;

import bg.forcar.mobile.responses.cars.Car;

import java.io.Serializable;
import java.sql.Timestamp;

public class ServiceExpense implements Serializable {

    private String serviceExpenseId;
    private ServiceExpenseType type;
    private Car car;
    private Double price;
    private String notes;
    private Long mileage;
    private Timestamp createdOn;
    private Timestamp editedOn;

    public String getServiceExpenseId() {
        return serviceExpenseId;
    }

    public void setServiceExpenseId(String serviceExpenseId) {
        this.serviceExpenseId = serviceExpenseId;
    }

    public ServiceExpenseType getType() {
        return type;
    }

    public void setType(ServiceExpenseType type) {
        this.type = type;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
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
