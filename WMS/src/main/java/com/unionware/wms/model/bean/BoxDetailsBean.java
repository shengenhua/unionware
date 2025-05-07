package com.unionware.wms.model.bean;

/**
 * @Author : pangming
 * @Time : On 2023/6/2 16:07
 * @Description : BoxDetailsBean
 */

public class BoxDetailsBean {
    private String	BarCode;
    private String	entryid;
    private String	Qty;


    public String getBarCode() {
        return this.BarCode;
    }

    public void setBarCode(String BarCode) {
        this.BarCode = BarCode;
    }

    public String getEntryid() {
        return this.entryid;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getQty() {
        return this.Qty;
    }

    public void setQty(String Qty) {
        this.Qty = Qty;
    }
}
