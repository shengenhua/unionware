package unionware.base.model.req;


import unionware.base.model.bean.TaskIdBean;

import java.io.Serializable;
import java.util.List;

public class TaskReq implements Serializable {
    private String setId; // 扫描配置Id
    private Integer flowId; // 作业流程Id
    private List<TaskIdBean> items;

    public TaskReq(String setId, Integer flowId, List<TaskIdBean> items) {
        this.setId = setId;
        this.flowId = flowId;
        this.items = items;
    }

    public TaskReq(String setId, Integer flowId) {
        this.setId = setId;
        this.flowId = flowId;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public Integer getFlowId() {
        return flowId;
    }

    public void setFlowId(Integer flowId) {
        this.flowId = flowId;
    }

    public List<TaskIdBean> getItems() {
        return items;
    }

    public void setItems(List<TaskIdBean> items) {
        this.items = items;
    }
}
