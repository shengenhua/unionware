package unionware.base.model.resp;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import unionware.base.model.bean.BarcodeBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AnalysisResp implements Serializable ,Cloneable{
    private List<Map<String, BarcodeBean>> FBillHead;
    /**
     * 单据头
     */
    private List<Map<String, BarcodeBean>> FHeadCollects;
    /**
     * 单据体
     */
    private List<Map<String, BarcodeBean>> FEntryCollects;
    /**
     * 子单据体
     */
    private List<Map<String, BarcodeBean>> FSubEntryCollects;
    private List<Map<String, BarcodeBean>> FEntity;

    @SerializedName("FormId")
    private String formId;
    @SerializedName("PrimaryId")
    private String primaryId;
    @SerializedName("CustomFilter")
    private String customFilter;

    public List<Map<String, BarcodeBean>> getFBillHead() {
        return FBillHead;
    }

    public void setFBillHead(List<Map<String, BarcodeBean>> FBillHead) {
        this.FBillHead = FBillHead;
    }

    public List<Map<String, BarcodeBean>> getFEntity() {
        return FEntity;
    }

    public void setFEntity(List<Map<String, BarcodeBean>> FEntity) {
        this.FEntity = FEntity;
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

    public List<Map<String, BarcodeBean>> getFHeadCollects() {
        return FHeadCollects;
    }

    public void setFHeadCollects(List<Map<String, BarcodeBean>> FHeadCollects) {
        this.FHeadCollects = FHeadCollects;
    }

    public List<Map<String, BarcodeBean>> getFEntryCollects() {
        return FEntryCollects;
    }

    public void setFEntryCollects(List<Map<String, BarcodeBean>> FEntryCollects) {
        this.FEntryCollects = FEntryCollects;
    }

    public List<Map<String, BarcodeBean>> getFSubEntryCollects() {
        return FSubEntryCollects;
    }

    public void setFSubEntryCollects(List<Map<String, BarcodeBean>> FSubEntryCollects) {
        this.FSubEntryCollects = FSubEntryCollects;
    }

    public String getCustomFilter() {
        return customFilter;
    }

    public void setCustomFilter(String customFilter) {
        this.customFilter = customFilter;
    }

    @NonNull
    @Override
    public AnalysisResp clone() {
        try {
            return (AnalysisResp) super.clone();
        } catch (CloneNotSupportedException e) {
            return new AnalysisResp();
        }
    }
}