package com.unionware.wms.model.resp;

import java.util.List;
import java.util.Map;

import unionware.base.model.resp.ActionResp;

public class AnalysisInfoBySubmitResp {
    public List<ActionResp> getAction() {
        return action;
    }

    public void setAction(List<ActionResp> action) {
        this.action = action;
    }



    private List<ActionResp> action;
    private  List<Map<String,Object>> data;

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
