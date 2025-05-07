package unionware.base.model.req;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FiltersReq implements Serializable {
    /**
     * 第几页
     */
    private Integer pageIndex;
    /**
     * 每页数据
     */
    private Integer pageSize;
    private Boolean isRuleConverter;
    /**
     * 是否分页
     */
    private Boolean isPageEnabled;
    private Map<String, Object> filters;
    private Map<String, Object> params;
    private List<String> sponsors;
    private String ruleId;
    /**
     * 虚拟视图 筛选条件
     */
    private String custom;


    public FiltersReq() {
    }

    public FiltersReq(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public FiltersReq(Integer pageIndex, Integer pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public FiltersReq(Integer pageIndex, Integer pageSize, Map<String, Object> filters) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.filters = filters;
    }

    public FiltersReq(Integer pageIndex, Map<String, Object> filters) {
        this.pageIndex = pageIndex;
        this.filters = filters;
    }

    /**
     * @param pageIndex 页码
     * @param pageSize 页数
     * @param isRuleConverter 是否支持单据转换选单条件（true）
     * @param filters
     */
    public FiltersReq(Integer pageIndex, Integer pageSize, Boolean isRuleConverter, String ruleId, Map<String, Object> filters) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.isRuleConverter = isRuleConverter;
        this.ruleId = ruleId;
        this.filters = filters;
    }

    public FiltersReq(Integer pageIndex, Boolean isRuleConverter, String ruleId, Map<String, Object> filters) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.isRuleConverter = isRuleConverter;
        this.ruleId = ruleId;
        this.filters = filters;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public FiltersReq(Map<String, Object> filters) {
        this.filters = filters;
    }


    public Boolean getRuleConverter() {
        return isRuleConverter;
    }

    public void setRuleConverter(Boolean ruleConverter) {
        isRuleConverter = ruleConverter;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    public void setIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public List<String> getSponsors() {
        return sponsors;
    }

    public void setSponsors(List<String> sponsors) {
        this.sponsors = sponsors;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public Boolean getPageEnabled() {
        return isPageEnabled;
    }

    public void setPageEnabled(Boolean pageEnabled) {
        isPageEnabled = pageEnabled;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }
}

