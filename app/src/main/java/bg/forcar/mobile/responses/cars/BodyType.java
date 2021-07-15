package bg.forcar.mobile.responses.cars;

import java.io.Serializable;

public class BodyType implements Serializable {

    private Integer bodyType;
    private String bodyTypeDescription;

    public Integer getBodyType() {
        return bodyType;
    }

    public void setBodyType(Integer bodyType) {
        this.bodyType = bodyType;
    }

    public String getBodyTypeDescription() {
        return bodyTypeDescription;
    }

    public void setBodyTypeDescription(String bodyTypeDescription) {
        this.bodyTypeDescription = bodyTypeDescription;
    }
}
