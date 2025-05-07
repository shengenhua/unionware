package unionware.base.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import unionware.base.model.bean.PropertyBean;

public class EntityBean implements Serializable {
    private Integer id; // 资料id属性
    private Integer typeId;
    private String srcFormId;
    private String tarFormId;
    private String convertId;
    @SerializedName("extKey")
    private String key;
    @SerializedName(value = "extName", alternate = "name")
    private String name;
    @SerializedName("extRequired")
    private String required;
    @SerializedName("extRelateEntry")
    private String barcodeProperty; // 表示条码解析的条码属性 0 表示 包装 1表示明细（单据头）
    private PropertyBean properties;
    private String type; // 类型 num 数字 text 文本
    private String value;
    private boolean isEdit;
    private boolean isDisplay;
    private boolean isMatch;
    private boolean isRequired;
    private boolean isSuggestMatch;
    private PropertyBean property;
    private boolean defalut; // 是否默认
    private boolean lock; // 是否锁定
    @SerializedName("isBindContainer")
    private boolean isContainer; // 是否容器条码
    private boolean isEnable;
    private String tag;
    private int index;

    public EntityBean(PropertyBean property) {
        this.property = property;
    }

    public EntityBean(String name, String key) {
        this.key = key;
        this.name = name;
    }

    public EntityBean(String name, String key, String type) {
        this.key = key;
        this.name = name;
        this.type = type;
    }

    public EntityBean(String name, String key, String type, boolean lock) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.lock = lock;
    }

    public String getTag() {
        return tag;
    }


    public int getIndex() {
        return index;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isContainer() {
        return isContainer;
    }

    public void setContainer(boolean container) {
        isContainer = container;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isDisplay() {
        return isDisplay;
    }

    public void setDisplay(boolean display) {
        isDisplay = display;
    }

    public boolean isMatch() {
        return isMatch;
    }

    public void setMatch(boolean match) {
        isMatch = match;
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

    public PropertyBean getProperty() {
        return property;
    }

    public void setProperty(PropertyBean property) {
        this.property = property;
    }

    public boolean isDefalut() {
        return defalut;
    }

    public void setDefalut(boolean defalut) {
        this.defalut = defalut;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }


    public PropertyBean getProperties() {
        return properties;
    }

    public void setProperties(PropertyBean properties) {
        this.properties = properties;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
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

    public String getConvertId() {
        return convertId;
    }

    public void setConvertId(String convertId) {
        this.convertId = convertId;
    }

    public String getBarcodeProperty() {
        return barcodeProperty;
    }

    public void setBarcodeProperty(String barcodeProperty) {
        this.barcodeProperty = barcodeProperty;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }
}
