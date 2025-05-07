package com.unionware.wms.strategy;

import unionware.base.model.bean.EntityBean;

import java.util.List;

public class PackConfig {
    private PackStrategy strategy; // 装箱策略
    private static PackConfig instance;

    public PackConfig(PackStrategy strategy) {
        this.strategy = strategy;
    }

    public List<EntityBean> getLocalScanConfigList() {
        return strategy.initEntityProp();
    }


    public static PackConfig getInstance(String operType,String type,String unPackType) {
        switch (operType){
            case "1":
                switch (type) {
                    case "1": // 明细装箱-有箱号
                        instance = new PackConfig(new DPHasBoxNoStrategy());
                        break;
                    case "2": // 明细装箱-无箱号
                        instance = new PackConfig(new DPNoBoxNoStrategy());
                        break;
                    case "3": // 包装装箱-有箱号
                        instance = new PackConfig(new BPHasBoxNoStrategy());
                        break;
                    case "4": // 包装装箱-有箱号
                        instance = new PackConfig(new BPNoBoxNoStrategy());
                        break;
                    default:
                        instance = new PackConfig(new DefaultStrategy());
                        break;
                }
                break;
            case "2":
                switch (unPackType){
                    case "1":
                        instance = new PackConfig(new BPUnpackingStrategy());
                        break;
                    case "2":
                        instance = new PackConfig(new DPUnpackingStrategy());
                        break;
                }
                break;
        }

        return instance;
    }
    public static PackConfig getInstanceByTrans(String transferPackType,String transferInPackCode){
        switch (transferPackType){
            case "":
            case "1":
                if("2".equals(transferInPackCode)){
                    instance = new PackConfig(new BTransHasPrintTemplateStrategy());
                }else {
                    instance = new PackConfig(new BTransNotPrintTemplateStrategy());
                }
                break;
            case "2":
                if("2".equals(transferInPackCode)){
                    instance = new PackConfig(new BBDTransHasPrintTemplateStrategy());
                }else {
                    instance = new PackConfig(new BBDTransNotPrintTemplateStrategy());
                }
                break;
            case "3":
                if("2".equals(transferInPackCode)){
                    instance = new PackConfig(new BDTransHasPrintTemplateStrategy());
                }else {
                    instance = new PackConfig(new BDTransNotPrintTemplateStrategy());
                }
                break;
        }
        return instance;
    }
}
