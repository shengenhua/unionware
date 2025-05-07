package unionware.base.model.req;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ViewReq implements Serializable {
    private String formId;
    private String command;
    private String pageId;
    private String keyId;
    private String primaryId;

    private Map<String, Object> params;
    private List<ItemBean> items;
    /**
     * 错误码
     */
    private String[] Sponsors;

    private Boolean compact;

    private String simulate;

    public ViewReq() {
    }

    public ViewReq(Map<String, Object> params) {
        this.params = params;
    }

    public ViewReq(String formId, Map<String, Object> params, Boolean compact) {
        this.formId = formId;
        this.params = params;
        this.compact = compact;
    }

    public ViewReq(String formId, Map<String, Object> params) {
        this.formId = formId;
        this.params = params;
    }

    public ViewReq(String pageId, List<ItemBean> items) {
        this.pageId = pageId;
        this.items = items;
    }

    public ViewReq(String command, String pageId) {
        this.command = command;
        this.pageId = pageId;
    }

    public Boolean getCompact() {
        return compact;
    }

    public void setCompact(Boolean compact) {
        this.compact = compact;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public ViewReq(String pageId) {
        this.pageId = pageId;
    }

    public void setItems(List<ItemBean> items) {
        this.items = items;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<ItemBean> getItems() {
        return items;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    public String[] getSponsors() {
        return Sponsors;
    }

    public void setSponsors(String[] sponsors) {
        Sponsors = sponsors;
    }

    public String getSimulate() {
        return simulate;
    }

    public void setSimulate(String simulate) {
        this.simulate = simulate;
    }
}
