package com.unionware.wms.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @Author : pangming
 * @Time : On 2023/6/15 11:00
 * @Description : RePackBean
 */

public class RePackBean implements Serializable {
    @SerializedName("Id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
