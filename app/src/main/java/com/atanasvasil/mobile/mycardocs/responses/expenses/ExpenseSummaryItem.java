package com.atanasvasil.mobile.mycardocs.responses.expenses;

import java.io.Serializable;

public class ExpenseSummaryItem implements Serializable {

    private Integer year;
    private Integer month;
    private Double total;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
