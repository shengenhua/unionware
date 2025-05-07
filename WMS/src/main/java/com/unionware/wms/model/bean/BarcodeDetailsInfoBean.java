package com.unionware.wms.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BarcodeDetailsInfoBean implements Serializable {
    @SerializedName("Id")
    private int id; // 条码内码Id
    @SerializedName("BarCodeType")
    private String type;
    @SerializedName("Qty")
    private String qty;


    @SerializedName("BarCode")
    private String barCode;


    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
