package unionware.base.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ParameterBean implements Serializable {
    @SerializedName("KickoutControlMode")
    private String controlMode;

    public ParameterBean(String controlMode) {
        this.controlMode = controlMode;
    }
}
