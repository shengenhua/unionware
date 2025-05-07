package com.unionware.wms.strategy;

import unionware.base.model.bean.EntityBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2023/6/7 16:21
 * @Description : BTransNoPrintTemplateStrategy
 */

public class BTransNotPrintTemplateStrategy implements PackStrategy{
    @Override
    public List<EntityBean> initEntityProp() {
        List<EntityBean> list = new ArrayList<>();
        list.add(new EntityBean("转入箱码", "in", "text"));
        list.add(new EntityBean("转出箱码", "out", "text"));

        return list;
    }
}
