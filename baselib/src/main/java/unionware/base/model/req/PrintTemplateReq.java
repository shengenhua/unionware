package unionware.base.model.req;

import java.io.Serializable;

/**
 * @Author : pangming
 * @Time : On 2023/6/7 14:08
 * @Description : PrintTemplateReq
 */

public class PrintTemplateReq implements Serializable {
    private String formId;

    public PrintTemplateReq(String formId) {
        this.formId = formId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }


}
