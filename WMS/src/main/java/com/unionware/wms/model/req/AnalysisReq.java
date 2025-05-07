package com.unionware.wms.model.req;

import java.io.Serializable;
import java.util.Map;

public class AnalysisReq implements Serializable {
    private Integer taskId; //临时装箱清单内码
    private String setId; //装拆箱应用配置内码
    private Integer codeId; // 解析返回的条码内码
    private String code; // 条码
    private String type; // 扫描类型 Pack-箱，Details-明细
    private boolean AutoComfirmCode; //扫描自动确认录入
    private String[] interactions; //0001-箱内重复，90002-跨箱重复，900003-箱与明细装箱混装, 90004-明细间装箱混装
    private Map<String, String> ExtendItems; // 拓展字段

    private String packcode;//在指定的箱码

    private String PackCodeType;//箱码类型(in-转入箱码，out-转出箱码)

    public AnalysisReq(int taskId, String setId) {
        this.taskId = taskId;
        this.setId = setId;
    }

    public AnalysisReq(int taskId, String setId, Integer codeId, String type) {
        this.taskId = taskId;
        this.setId = setId;
        this.codeId = codeId;
        this.type = type;
    }


    public int getCodeId() {
        return codeId;
    }

    public void setCodeId(int codeId) {
        this.codeId = codeId;
    }

    public Map<String, String> getExtendItems() {
        return ExtendItems;
    }

    public void setExtendItems(Map<String, String> extendItems) {
        ExtendItems = extendItems;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAutoComfirmCode() {
        return AutoComfirmCode;
    }

    public void setAutoComfirmCode(boolean autoComfirmCode) {
        AutoComfirmCode = autoComfirmCode;
    }

    public String[] getInteractions() {
        return interactions;
    }

    public void setInteractions(String[] interactions) {
        this.interactions = interactions;
    }
    public String getPackcode() {
        return packcode;
    }

    public void setPackcode(String packcode) {
        this.packcode = packcode;
    }
    public String getPackCodeType() {
        return PackCodeType;
    }

    public void setPackCodeType(String packCodeType) {
        PackCodeType = packCodeType;
    }
}
