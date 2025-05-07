package com.unionware.wms.strategy;

import unionware.base.model.bean.EntityBean;

import java.util.ArrayList;
import java.util.List;

// 策略 包装装箱 - 无箱号
public class BPNoBoxNoStrategy implements PackStrategy {
    @Override
    public List<EntityBean> initEntityProp() {
        List<EntityBean> list = new ArrayList<>();
        list.add(new EntityBean("模板标签", "TemplateTag", "combox"));
        list.add(new EntityBean("子项条码", "details", "text"));
        list.add(new EntityBean("箱容量", "FCapacity", "num"));
        list.add(new EntityBean("已装件数", "count", "onlyread"));
        return list;
    }
}
