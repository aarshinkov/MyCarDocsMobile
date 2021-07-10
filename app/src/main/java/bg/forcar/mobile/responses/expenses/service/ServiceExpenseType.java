package bg.forcar.mobile.responses.expenses.service;

import java.io.Serializable;

public class ServiceExpenseType implements Serializable {

    private Integer type;
    private String typeDescription;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }
}
