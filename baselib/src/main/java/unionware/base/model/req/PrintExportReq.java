package unionware.base.model.req;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2023/6/8 10:22
 * @Description : PrintExportReq
 */


public class PrintExportReq implements Serializable {
    private String formId;
    private List<String> billIds;
    private List<String> templateIds;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public List<String> getBillIds() {
        return billIds;
    }

    public void setBillIds(List<String> billIds) {
        this.billIds = billIds;
    }

    public List<String> getTemplateIds() {
        return templateIds;
    }

    public void setTemplateIds(List<String> templateIds) {
        this.templateIds = templateIds;
    }


}
