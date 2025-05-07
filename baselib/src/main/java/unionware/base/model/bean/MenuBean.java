package unionware.base.model.bean;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MenuBean implements Serializable,Cloneable {
    /**
     *  应用地址
     */
    private String url;
    /**
     * primaryId
     */
    private String id;
    private String code;
    /**
     * 标签名
     */
    private String name;
    @SerializedName("operType")
    private String type;
    private String srcFormId;
    @SerializedName("transferPackType")
    private String transType;
    private String packType;
    private String mode;
    private String flowId;
    /**
     * 判断跳转什么界面
     */
    private String typeId;
    /**
     * 场景码
     */
    private String scene; // 场景码
    private String convertId;
    /**
     *  报表 表单标识
     */
    private String reportFormId;
    /**
     * 拆箱并转箱
     */
    private boolean unpackAndTransfer;
    private String allowPackCodeCreate;//"0" 不允许创建包装条码
    /**
     * 列表显示 id
     */
    private String listSearchId;
    /**
     * 明细显示id
     */
    private String itemSearchId;
    /**
     * 生产订单 列表显示 id
     */
    private String orderSearchId;
    /**
     * 功能id
     */
    private String searchId;
    /**
     * 应用场景
     *   1:任务列表>任务详情>汇报（默认）
     *   2:生产订单>任务列表>任务详情>汇报
     */
    private String useStyleId;

    public String getAllowPackCodeCreate() {
        return allowPackCodeCreate;
    }

    public void setAllowPackCodeCreate(String allowPackCodeCreate) {
        this.allowPackCodeCreate = allowPackCodeCreate;
    }

    //    private String autoPrintBoxCode;// 是否打印 目前不用了
//    private String boxCodePrintLabel;//打印模板 目前不用了
    public String getConvertId() {
        return convertId;
    }

    public void setConvertId(String convertId) {
        this.convertId = convertId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
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

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getPackType() {
        return packType;
    }

    public void setPackType(String packType) {
        this.packType = packType;
    }

    public MenuBean(String name) {
        this.name = name;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isUnpackAndTransfer() {
        return unpackAndTransfer;
    }

    public void setUnpackAndTransfer(boolean unpackAndTransfer) {
        this.unpackAndTransfer = unpackAndTransfer;
    }

    public String getListSearchId() {
        return listSearchId;
    }

    public void setListSearchId(String listSearchId) {
        this.listSearchId = listSearchId;
    }

    public String getItemSearchId() {
        return itemSearchId;
    }

    public void setItemSearchId(String itemSearchId) {
        this.itemSearchId = itemSearchId;
    }

    public String getSearchId() {
        return searchId;
    }


    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getUseStyleId() {
        return useStyleId;
    }

    public void setUseStyleId(String useStyleId) {
        this.useStyleId = useStyleId;
    }

    public String getOrderSearchId() {
        return orderSearchId;
    }

    public void setOrderSearchId(String orderSearchId) {
        this.orderSearchId = orderSearchId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReportFormId() {
        return reportFormId;
    }

    public void setReportFormId(String reportFormId) {
        this.reportFormId = reportFormId;
    }

    @NonNull
    @Override
    public MenuBean clone() {
        try {
            return (MenuBean) super.clone();
        } catch (CloneNotSupportedException e) {
            return new MenuBean(name);
        }
    }
}
