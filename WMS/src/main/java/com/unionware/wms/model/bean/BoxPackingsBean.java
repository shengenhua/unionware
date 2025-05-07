package com.unionware.wms.model.bean;

import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2023/6/2 16:06
 * @Description : BoxPackingsBean
 */

public class BoxPackingsBean {
    private String	PackCode;
    private List<BoxDetailsBean> Details;


    public String getPackCode() {
        return this.PackCode;
    }

    public void setPackCode(String PackCode) {
        this.PackCode = PackCode;
    }

    public List<BoxDetailsBean> getDetails() {
        return this.Details;
    }

    public void setDetails(List<BoxDetailsBean> Details) {
        this.Details = Details;
    }
}
