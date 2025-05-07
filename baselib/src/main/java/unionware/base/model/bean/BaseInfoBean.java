package unionware.base.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BaseInfoBean implements Serializable {
    private String id;
    private String code;
    private String name;
    private String specification;
    @SerializedName("isBatchEnabled")
    private boolean isBatch;
    @SerializedName("isPeriodEnabled")
    private boolean isPeriod;
    @SerializedName("isSerialEnabled")
    private boolean isSerial;
    @SerializedName("isAuxpropEnabled")
    private boolean isAuxprop;
    private int pos; // 为了逐条编辑的跳转用EventBus的pos
    private int index;
    private String tare;
    private String materialId;
    private String billCode;
    private String key;

    private List<Map<String,Object>> FStockFlexItem;//用于过滤仓位的维度 "FStockFlexItem":[{"flexId":100001},{"flexId":100002}]
    public List<Map<String, Object>> getFStockFlexItem() {
        return FStockFlexItem;
    }

    public BaseInfoBean(){
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void setFStockFlexItem(List<Map<String, Object>> FStockFlexItem) {
        this.FStockFlexItem = FStockFlexItem;
    }

    public String getTare() {
        return tare;
    }

    public void setTare(String tare) {
        this.tare = tare;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
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

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public boolean isBatch() {
        return isBatch;
    }

    public void setBatch(boolean batch) {
        isBatch = batch;
    }

    public boolean isPeriod() {
        return isPeriod;
    }

    public void setPeriod(boolean period) {
        isPeriod = period;
    }

    public boolean isSerial() {
        return isSerial;
    }

    public void setSerial(boolean serial) {
        isSerial = serial;
    }

    public boolean isAuxprop() {
        return isAuxprop;
    }

    public void setAuxprop(boolean auxprop) {
        isAuxprop = auxprop;
    }
}
