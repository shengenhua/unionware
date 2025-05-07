package com.unionware.wms.strategy;

import unionware.base.model.bean.EntityBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2023/6/5 15:16
 * @Description : WRePackingStrategy
 */

public class WRePackingStrategy implements PackStrategy {
    @Override
    public List<EntityBean> initEntityProp() {
        List<EntityBean> list = new ArrayList<>();
        list.add(new EntityBean("转入箱码", "into", "text"));
        list.add(new EntityBean("转出箱码", "out", "text"));
        return list;
    }
}
