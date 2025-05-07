package com.unionware.wms.strategy;


import java.util.ArrayList;
import java.util.List;

import unionware.base.model.bean.EntityBean;

/**
 * @Author : pangming
 * @Time : On 2023/6/8 19:26
 * @Description : BDTransHasPrintTemplateStrategy
 */

public class BDTransHasPrintTemplateStrategy implements PackStrategy{
    @Override
    public List<EntityBean> initEntityProp() {
        List<EntityBean> list = new ArrayList<>();
        list.add(new EntityBean("标签模板", "label", "combox"));
        list.add(new EntityBean("子项条码", "details", "text"));
        list.add(new EntityBean("子项条码数量", "detailsQty", "num",true));
        return list;
    }
}
