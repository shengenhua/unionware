package com.unionware.wms.strategy;

import java.util.ArrayList;
import java.util.List;

import unionware.base.model.bean.EntityBean;

/**
 * @Author : pangming
 * @Time : On 2023/6/8 19:28
 * @Description : BDTransNotPrintTemplateStrategy
 */

public class BDTransNotPrintTemplateStrategy implements PackStrategy {
    @Override
    public List<EntityBean> initEntityProp() {
        List<EntityBean> list = new ArrayList<>();
        list.add(new EntityBean("转入箱码", "in", "text"));
        list.add(new EntityBean("子项条码", "details", "text"));
        list.add(new EntityBean("子项条码数量", "detailsQty", "num", true));
        return list;
    }
}
