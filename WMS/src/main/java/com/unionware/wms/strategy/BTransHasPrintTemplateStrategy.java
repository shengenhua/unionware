package com.unionware.wms.strategy;

import unionware.base.model.bean.EntityBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2023/6/7 16:21
 * @Description : BTransHasPrintTemplateStrategy
 */

public class BTransHasPrintTemplateStrategy implements PackStrategy{
    @Override
    public List<EntityBean> initEntityProp() {
        List<EntityBean> list = new ArrayList<>();
        list.add(new EntityBean("标签模板", "label", "combox"));
        list.add(new EntityBean("转出箱码", "out", "text"));

        return list;
    }
}
