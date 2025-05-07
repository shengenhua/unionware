package com.unionware.wms.model.req;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DeleteReq implements Serializable {
    @SerializedName("Id")
    private Integer id;
    @SerializedName("EntryId")
    private Integer entryId;

    @SerializedName("Codes")
    private List<String> codes;


    public DeleteReq(Integer id, Integer entryId) {
        this.id = id;
        this.entryId = entryId;
    }

    public DeleteReq(Integer id) {
        this.id = id;
    }

    public DeleteReq(Integer id, List<String> codes) {
        this.id = id;
        this.codes = codes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }
}
