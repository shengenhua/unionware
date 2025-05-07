package unionware.base.model.bean;

import com.google.gson.annotations.SerializedName;

import unionware.base.model.bean.PropertyBean;
import unionware.base.model.resp.AnalysisResp;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2024/7/15 13:47
 * @Description : SimpleViewAndModelBean
 */

public class SimpleViewAndModelBean implements Serializable {


    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public AnalysisResp getAnalysisResp() {
        return analysisResp;
    }

    public void setAnalysisResp(AnalysisResp analysisResp) {
        this.analysisResp = analysisResp;
    }

    private String pageId;
    @SerializedName("data")
      private AnalysisResp analysisResp;
    @SerializedName("view")
    private List<PropertyBean> propertyBeans;

    public List<PropertyBean> getPropertyBeans() {
        return propertyBeans;
    }

    public void setPropertyBeans(List<PropertyBean> propertyBeans) {
        this.propertyBeans = propertyBeans;
    }

}
