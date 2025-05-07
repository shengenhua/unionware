package com.unionware.wms.strategy;

import unionware.base.model.bean.EntityBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2023/5/30 15:51
 * @Description : DPUnpackingStrategy
 */
// 策略 按子项拆箱
public class DPUnpackingStrategy implements PackStrategy {
    @Override
    public List<EntityBean> initEntityProp() {
        List<EntityBean> list = new ArrayList<>();
        list.add(new EntityBean("子项条码", "details", "text"));
        list.add(new EntityBean("子项条码数量", "detailsQty", "num",true));
        return list;
    }
}
