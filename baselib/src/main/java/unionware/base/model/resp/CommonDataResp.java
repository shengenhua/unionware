package unionware.base.model.resp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import unionware.base.model.resp.ActionResp;
import unionware.base.model.resp.CommonListDataResp;

/**
 * @Author : pangming
 * @Time : On 2024/7/26 18:21
 * @Description : CommonDataResp
 */

public class CommonDataResp implements Serializable {


    public CommonListDataResp<Map<String, Object>> getData() {
        return data;
    }

    public void setData(CommonListDataResp<Map<String, Object>> data) {
        this.data = data;
    }

    public List<ActionResp> getAction() {
        return action;
    }

    public void setAction(List<ActionResp> action) {
        this.action = action;
    }

    private List<ActionResp> action;
    private  CommonListDataResp<Map<String, Object>> data;
}
