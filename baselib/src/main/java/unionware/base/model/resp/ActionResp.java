package unionware.base.model.resp;

import java.io.Serializable;

import unionware.base.model.resp.ActionDetailResp;

/**
 * @Author : pangming
 * @Time : On 2024/7/26 16:50
 * @Description : AntionResp
 */

public class ActionResp implements Serializable {
    private String name;
    private ActionDetailResp data;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ActionDetailResp getActionDetailResp() {
        return data;
    }

    public void setActionDetailResp(ActionDetailResp data) {
        this.data = data;
    }

}
