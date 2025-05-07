package unionware.base.model.req;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author : pangming
 * @Time : On 2024/7/31 10:41
 * @Description : PrintExportReq
 */

public class BarcodePrintExportReq implements Serializable {

    private List<Map<String,Object>> items;
    private Map<String,Object> params;
    private String formId;
    private String server;//server=remote，使用远端打印服务 server=local，使用PDA便携式打印,server=auto，由系统参数决定

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
