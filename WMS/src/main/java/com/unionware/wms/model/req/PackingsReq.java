package com.unionware.wms.model.req;

import com.unionware.wms.model.bean.BoxPackingsBean;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2023/5/26 18:42
 * @Description : PackingsRey
 */

public class PackingsReq implements Serializable {

    private String type;// 类型(装箱-pack， 拆箱-unPack, 转箱(重装箱)-rePack, 清箱-clearPack)
    private String setId;
    private List<BoxPackingsBean> Packings;


    public List<BoxPackingsBean> getPackings() {
        return this.Packings;
    }

    public void setPackings(List<BoxPackingsBean> Packings) {
        this.Packings = Packings;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

}
