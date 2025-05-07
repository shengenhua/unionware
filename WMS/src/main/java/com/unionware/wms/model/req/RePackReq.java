package com.unionware.wms.model.req;


import com.unionware.wms.model.bean.BoxPackingsBean;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2023/6/7 19:39
 * @Description : RePackReq
 */

public class RePackReq implements Serializable {
    private String InPackCode;
    private String setId;
    private List<BoxPackingsBean> Packings;

    public String getInPackCode() {
        return InPackCode;
    }

    public void setInPackCode(String inPackCode) {
        InPackCode = inPackCode;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public List<BoxPackingsBean> getPackings() {
        return Packings;
    }

    public void setPackings(List<BoxPackingsBean> packings) {
        Packings = packings;
    }
}
