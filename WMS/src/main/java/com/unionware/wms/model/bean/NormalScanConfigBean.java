package com.unionware.wms.model.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author : pangming
 * @Time : On 2024/7/9 16:42
 * @Description : 1.普通扫描应用配置列表
 * 2.查询作业流程配置详情
 *
 */

public class NormalScanConfigBean implements Serializable {
    private String id; //配置Id
    private String code; //配置编码
    private String name; //配置名称
    private String scene; //场景码
    private Integer typeId; //作业类型Id
    private Integer jobFlowId;
    private String mode;//1：关联源单生成模式；3：无源单生成模式；4：校验模式
    private String srcFormId;//源单Id
    private String tarFormId;//目标单Id
    private String convetRuleId;//转换规则
    private Boolean multiCombineScan = false;//启用多单合并扫描
    private String multiMode;//多单模式 1:手工选择源单；2：扫描条码自动获取源单信息
    private Boolean matchByIndex;//手工指定分录扫描
    private String serialSource;//序列号来源 1:条码携带;2:与条码值相同;3:扫描界面补充
    private String formEventMode;//1.PDA提交(任意人)2.客户端操作 3.PDA提交(创建人)；
    private Boolean IsPointEntryParse = false;//是否启用指定分录建档
    private Boolean IsNoCode = false;//无条码制单
    private String NoCodeMode;//无条码采集模式 FullBill 整单； ByMaterial 按产品档案
    /**
     * 是批量填充
     */
    private Boolean IsBatchFill = false;
    private List<PropertyInfo> gathers;
    /**
     * 修改
     */
    private List<PropertyInfo> batchFields;
    /**
     * 任务明细存在勾选的字段，PDA界面才显示库存查询的按钮
     */
    private List<String> InventoryFilterItems;

    public Boolean getNoCode() {
        return IsNoCode;
    }

    public void setNoCode(Boolean noCode) {
        IsNoCode = noCode;
    }
    public String getNoCodeMode() {
        return NoCodeMode;
    }

    public void setNoCodeMode(String noCodeMode) {
        NoCodeMode = noCodeMode;
    }

    public String getFormEventMode() {
        return formEventMode;
    }

    public void setFormEventMode(String formEventMode) {
        this.formEventMode = formEventMode;
    }

    public Boolean getPointEntryParse() {
        return IsPointEntryParse;
    }

    public void setPointEntryParse(Boolean pointEntryParse) {
        IsPointEntryParse = pointEntryParse;
    }

    public List<PropertyInfo> getGathers() {
        return gathers;
    }

    public void setGathers(List<PropertyInfo> gathers) {
        this.gathers = gathers;
    }

    public Integer getJobFlowId() {
        return jobFlowId;
    }

    public void setJobFlowId(Integer jobFlowId) {
        this.jobFlowId = jobFlowId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSrcFormId() {
        return srcFormId;
    }

    public void setSrcFormId(String srcFormId) {
        this.srcFormId = srcFormId;
    }

    public String getTarFormId() {
        return tarFormId;
    }

    public void setTarFormId(String tarFormId) {
        this.tarFormId = tarFormId;
    }

    public String getConvetRuleId() {
        return convetRuleId;
    }

    public void setConvetRuleId(String convetRuleId) {
        this.convetRuleId = convetRuleId;
    }

    public Boolean getMultiCombineScan() {
        return multiCombineScan;
    }

    public void setMultiCombineScan(Boolean multiCombineScan) {
        this.multiCombineScan = multiCombineScan;
    }

    public String getMultiMode() {
        return multiMode;
    }

    public void setMultiMode(String multiMode) {
        this.multiMode = multiMode;
    }

    public Boolean getMatchByIndex() {
        return matchByIndex;
    }

    public void setMatchByIndex(Boolean matchByIndex) {
        this.matchByIndex = matchByIndex;
    }

    public String getSerialSource() {
        return serialSource;
    }

    public void setSerialSource(String serialSource) {
        this.serialSource = serialSource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public Boolean getBatchFill() {
        return IsBatchFill;
    }

    public void setBatchFill(Boolean batchFill) {
        IsBatchFill = batchFill;
    }

    public List<PropertyInfo> getBatchFields() {
        return batchFields;
    }

    public void setBatchFields(List<PropertyInfo> batchFields) {
        this.batchFields = batchFields;
    }

    public List<String> getInventoryFilterItems() {
        return InventoryFilterItems;
    }

    public void setInventoryFilterItems(List<String> inventoryFilterItems) {
        InventoryFilterItems = inventoryFilterItems;
    }

    public static class PropertyInfo implements Serializable {
        private boolean isMatch;
        private boolean isRewrite;
        private boolean isRequired;
        private boolean isSuggestMatch;
        private boolean isEdit;
        private String field;
        private Property property;

        public boolean isMatch() {
            return isMatch;
        }

        public void setMatch(boolean match) {
            isMatch = match;
        }

        public boolean isRewrite() {
            return isRewrite;
        }

        public void setRewrite(boolean rewrite) {
            isRewrite = rewrite;
        }

        public boolean isRequired() {
            return isRequired;
        }

        public void setRequired(boolean required) {
            isRequired = required;
        }

        public boolean isSuggestMatch() {
            return isSuggestMatch;
        }

        public void setSuggestMatch(boolean suggestMatch) {
            isSuggestMatch = suggestMatch;
        }

        public boolean isEdit() {
            return isEdit;
        }

        public void setEdit(boolean edit) {
            isEdit = edit;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Property getProperty() {
            return property;
        }

        public void setProperty(Property property) {
            this.property = property;
        }
    }

    public static class Property implements Serializable {
        private String Key;
        private String Name;
        private String Source;
        private String Entity;
        private Integer EntityId;
        private String Type;
        private String LookupId;
        private Integer FlexId;
        private String Related;
        private List<Map<String, String>> Enums;//字段类型COMBOBOX使用

        public List<Map<String, String>> getEnums() {
            return Enums;
        }

        public void setEnums(List<Map<String, String>> enums) {
            Enums = enums;
        }

        public String getKey() {
            return Key;
        }

        public void setKey(String key) {
            this.Key = key;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getSource() {
            return Source;
        }

        public void setSource(String source) {
            Source = source;
        }

        public String getEntity() {
            return Entity;
        }

        public void setEntity(String entity) {
            Entity = entity;
        }

        public Integer getEntityId() {
            return EntityId;
        }

        public void setEntityId(Integer entityId) {
            EntityId = entityId;
        }

        public String getType() {
            return Type;
        }

        public void setType(String type) {
            Type = type;
        }

        public String getLookupId() {
            return LookupId;
        }

        public void setLookupId(String lookupId) {
            LookupId = lookupId;
        }

        public Integer getFlexId() {
            return FlexId;
        }

        public void setFlexId(Integer flexId) {
            FlexId = flexId;
        }

        public String getRelated() {
            return Related;
        }

        public void setRelated(String related) {
            Related = related;
        }
    }

}
