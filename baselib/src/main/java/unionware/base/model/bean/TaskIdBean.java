package unionware.base.model.bean;

import java.io.Serializable;

public class TaskIdBean implements Serializable {
    private String formId;
    private String primaryId;


    public TaskIdBean(String formId, String primaryId) {
        this.formId = formId;
        this.primaryId = primaryId;
    }
    public TaskIdBean(String formId) {
        this.formId = formId;
    }
    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }
}
