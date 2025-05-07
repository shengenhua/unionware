package unionware.base.model.resp;

import java.io.Serializable;
import java.util.List;

import unionware.base.model.resp.ActionResp;
import unionware.base.model.resp.AnalysisResp;

/**
 * @Author : pangming
 * @Time : On 2024/7/26 16:45
 * @Description : AnalysisInfoResp
 */

public class AnalysisInfoResp implements Serializable {


    public List<ActionResp> getAction() {
        return action;
    }

    public void setAction(List<ActionResp> action) {
        this.action = action;
    }
    public AnalysisResp getData() {
        return data;
    }

    public void setData(AnalysisResp data) {
        this.data = data;
    }


    private List<ActionResp> action;
    private AnalysisResp data;
}
