package com.unionware.wms.strategy;

import unionware.base.model.bean.EntityBean;

import java.util.ArrayList;
import java.util.List;

public class DefaultStrategy implements PackStrategy {
    @Override
    public List<EntityBean> initEntityProp() {
        return new ArrayList<>();
    }
}
