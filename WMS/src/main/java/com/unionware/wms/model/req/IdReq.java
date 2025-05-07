package com.unionware.wms.model.req;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IdReq implements Serializable {
    @SerializedName("Setid")
    private String sId;

    private Integer Id;

    public IdReq() {

    }

    public IdReq(String sId) {
        this.sId = sId;
    }

    public IdReq(Integer id) {
        Id = id;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }
}
