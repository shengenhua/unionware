package unionware.base.model.resp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class DBCenterResp implements Serializable {

    private String type;
    @SerializedName(value = "id", alternate = "Id")
    private String id;
    @SerializedName(value ="number",alternate = "Number")
    private String number;
    @SerializedName(value = "name",alternate = "Name")
    private String name;

    private String tenantId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
