package com.unionware.wms.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import unionware.base.model.bean.EntityBean;


public class ScanConfigBean implements Serializable {
    private String id; //配置Id
    private String code; //配置编码
    private String name; //配置名称
    private String scene; //场景码
    private String operType; //作业类型  //1.装箱 2.拆箱 3.转箱
    private String packType; //装箱类型 //  1.明细装箱-有箱号 2.明细装箱-无箱号 3.包装装箱-有箱号 4.包装装箱-无箱号
    private String unPackType; //拆箱类型 //1.按箱指定子项拆箱,2.按子项拆箱
    private String detailRepeatScanU = ""; //拆箱 明细条码重复扫描  ""什么都不做，1，不控制，2.预警，3.严格控制
    private String detailRepeatScanT = "";//转箱 明细条码重复扫描
    private String transferPackType = "";//转箱类型 1.整箱转箱，2.按箱指定子项转箱 3.按子项转箱
    private String transferInPackCode = "";//转入箱码设置  1.扫描已有箱码，2.按规则生成新箱码
    private String scanSeq; // 装箱扫描顺序 1.先包装后子项 2.先子项后包装
    @SerializedName(value = "FEntityS", alternate = "gathers")
    private List<EntityBean> entity;
    @SerializedName("isBindContainer")
    private boolean isContainer; // 是否容器条码


    public boolean isContainer() {
        return isContainer;
    }

    public void setContainer(boolean container) {
        isContainer = container;
    }

    public String getScanSeq() {
        return scanSeq;
    }

    public void setScanSeq(String scanSeq) {
        this.scanSeq = scanSeq;
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

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getPackType() {
        return packType;
    }

    public void setPackType(String packType) {
        this.packType = packType;
    }

    public String getUnPackType() {
        return unPackType;
    }

    public void setUnPackType(String unPackType) {
        this.unPackType = unPackType;
    }

    public List<EntityBean> getEntity() {
        return entity;
    }

    public void setEntity(List<EntityBean> entity) {
        this.entity = entity;
    }

    public String getDetailRepeatScanU() {
        return detailRepeatScanU;
    }

    public void setDetailRepeatScanU(String detailRepeatScan) {
        this.detailRepeatScanU = detailRepeatScan;
    }

    public String getDetailRepeatScanT() {
        return detailRepeatScanT;
    }

    public void setDetailRepeatScanT(String detailRepeatScanT) {
        this.detailRepeatScanT = detailRepeatScanT;
    }

    public String getTransferPackType() {
        return transferPackType;
    }

    public void setTransferPackType(String transferPackType) {
        this.transferPackType = transferPackType;
    }

    public String getTransferInPackCode() {
        return transferInPackCode;
    }

    public void setTransferInPackCode(String transferInPackCode) {
        this.transferInPackCode = transferInPackCode;
    }
}
