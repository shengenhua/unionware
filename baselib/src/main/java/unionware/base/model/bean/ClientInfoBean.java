package unionware.base.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClientInfoBean implements Serializable {
    @SerializedName("ClientType")
    private Integer type;

    @SerializedName("ClientParameters")
    private String param;

    public ClientInfoBean(Integer type, String param) {
        this.type = type;
        this.param = param;
    }
}
