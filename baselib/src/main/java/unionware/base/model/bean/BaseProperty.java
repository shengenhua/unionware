package unionware.base.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseProperty implements Serializable {
    @SerializedName(value = "id", alternate = "ID")
    private String id;
    private String name;
    private String code;
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
