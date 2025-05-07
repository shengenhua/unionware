package com.unionware.wms.strategy;

import unionware.base.model.bean.EntityBean;

import java.util.ArrayList;
import java.util.List;

// 策略 明细装箱 - 有箱号
public class DPHasBoxNoStrategy implements PackStrategy {
    @Override
    public List<EntityBean> initEntityProp() {
        List<EntityBean> list = new ArrayList<>();
        list.add(new EntityBean("包装条码", "package", "text"));
        list.add(new EntityBean("箱容量", "FCapacity", "num"));
        list.add(new EntityBean("子项条码", "details", "text"));
        list.add(new EntityBean("子项条码数量", "FQTY", "num"));
        list.add(new EntityBean("已装件数", "count", "onlyread"));
        return list;
    }
}
