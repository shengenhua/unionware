package com.unionware.wms.model.resp;

import java.io.Serializable;
import java.util.List;

import unionware.base.model.resp.ActionResp;

/**
 * @Author : pangming
 * @Time : On 2024/7/26 16:45
 * @Description : AnalysisInfoResp
 */

public class AnalysisTResp<T> implements Serializable {


    public List<ActionResp> getAction() {
        return action;
    }

    public void setAction(List<ActionResp> action) {
        this.action = action;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private List<ActionResp> action;
    private T data;
}
