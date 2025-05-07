package unionware.base.model.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import unionware.base.model.bean.CommonListBean;

public class BillBean implements Serializable {
    /**
     * 执行单号
     */
    private String code;
    private String codeName;
    private String id;
    private String primaryId;
    private String flowId;
    private String formId; // 源单id
    private String barcode;
    private String materialId;
    private String planId;
    private String taskStatus;
    private String jobId;
    private String planNo;
    private List<CommonListBean> list;
    private Map<String, Object> dataMap;
    private Boolean isSelect;
    private String LinkId;//源单分录id
    private Long creatorId;//创建人

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Boolean getSelect() {
        return isSelect;
    }

    public String getFBoxCode() {
        return FBoxCode;
    }

    public void setFBoxCode(String FBoxCode) {
        this.FBoxCode = FBoxCode;
    }

    private String FBoxCode;//箱码

    public String getLinkId() {
        return LinkId;
    }

    public void setLinkId(String linkId) {
        LinkId = linkId;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public Boolean isSelect() {
        return isSelect != null && isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }

    public String getPlanNo() {
        return planNo;
    }

    public void setPlanNo(String planNo) {
        this.planNo = planNo;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public BillBean(String code, List<CommonListBean> list) {
        this.code = code;
        this.list = list;
    }


    public BillBean(String id, String code, List<CommonListBean> list) {
        this.code = code;
        this.id = id;
        this.list = list;
    }


    public BillBean(List<CommonListBean> list) {
        this.list = list;
    }

    public BillBean() {

    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CommonListBean> getList() {
        return list;
    }

    public void setList(List<CommonListBean> list) {
        this.list = list;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }
}
