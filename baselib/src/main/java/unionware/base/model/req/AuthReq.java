package unionware.base.model.req;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthReq implements Serializable {
    @SerializedName("MachineCode")
    private String imei;

    public AuthReq(String imei) {
        this.imei = imei;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
