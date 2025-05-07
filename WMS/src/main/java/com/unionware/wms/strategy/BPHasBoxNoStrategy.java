package com.unionware.wms.strategy;

import java.util.ArrayList;
import java.util.List;

import unionware.base.model.bean.EntityBean;

// 策略 包装装箱 - 有箱号
public class BPHasBoxNoStrategy implements PackStrategy {
    @Override
    public List<EntityBean> initEntityProp() {
        List<EntityBean> list = new ArrayList<>();
        list.add(new EntityBean("包装条码", "package", "text"));
        list.add(new EntityBean("箱容量", "FCapacity", "num"));
        list.add(new EntityBean("子项条码", "details", "text"));
        list.add(new EntityBean("已装件数", "count", "onlyread"));
        return list;
    }
}
