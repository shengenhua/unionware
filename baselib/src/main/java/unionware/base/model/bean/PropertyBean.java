package unionware.base.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PropertyBean implements Serializable, Cloneable {
    @SerializedName("Key")
    private String key;
    @SerializedName("Name")
    private String name;
    @SerializedName("Source")
    private String source;
    @SerializedName("Entity")
    private String entity;
    @SerializedName("EntityId")
    private String entityId;
    @SerializedName("Type")
    private String type;
    @SerializedName("LookupId")
    private String tag;
    private String ParentId;
    @SerializedName("Value")
    private String value;
    @SerializedName("Enabled")
    private boolean enable;
    private String code;
    private boolean lock; // 是否锁定
    private List<Map<String, String>> Enums;//字段类型COMBOBOX使用

    private String Related;//ITEMCLASS使用 关联COMBOBOX类型某个字段
    private String FlexId;//FLEXVALUE 维度id 仓位使用
    private boolean Visible;//是否显示


    /**
     * 虚拟视图显示名字
     */
    private String valueName;
    /**
     * 虚拟视图显示code
     */
    private String valueNumber;
    /**
     * 显示格式
     */
    @SerializedName("Display")
    private String Display;


    private String id;
    private boolean isDefault;//是否默认
    private List<Map<String, Object>> FStockFlexItem;//用于过滤仓位的维度 "FStockFlexItem":[{"flexId":100001},{"flexId":100002}]

    public List<Map<String, Object>> getFStockFlexItem() {
        return FStockFlexItem;
    }

    public void setFStockFlexItem(List<Map<String, Object>> FStockFlexItem) {
        this.FStockFlexItem = FStockFlexItem;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isVisible() {
        return Visible;
    }

    public void setVisible(boolean visible) {
        Visible = visible;
    }

    public String getFlexId() {
        return FlexId;
    }

    public void setFlexId(String flexId) {
        FlexId = flexId;
    }

    public String getRelated() {
        return Related;
    }

    public void setRelated(String related) {
        Related = related;
    }

    public List<Map<String, String>> getEnums() {
        return Enums;
    }

    public void setEnums(List<Map<String, String>> enums) {
        Enums = enums;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public PropertyBean(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public PropertyBean(String key, String name, String parentId) {
        this.key = key;
        this.name = name;
        ParentId = parentId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplay() {
        return Display;
    }

    public void setDisplay(String display) {
        Display = display;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getValueNumber() {
        return valueNumber;
    }

    public void setValueNumber(String valueNumber) {
        this.valueNumber = valueNumber;
    }

    @Override
    public PropertyBean clone() {
        try {
            return (PropertyBean) super.clone();
        } catch (Exception e) {
            return new PropertyBean(key,name);
        }
    }
}
