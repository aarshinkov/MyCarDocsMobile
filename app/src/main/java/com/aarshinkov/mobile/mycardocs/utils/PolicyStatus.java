package com.aarshinkov.mobile.mycardocs.utils;

public enum PolicyStatus {

    ALL(-1),
    ACTIVE(0),
    INACTIVE(1);

    private final Integer status;

    private PolicyStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
