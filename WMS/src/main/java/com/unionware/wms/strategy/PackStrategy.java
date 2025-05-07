package com.unionware.wms.strategy;


import java.util.List;

import unionware.base.model.bean.EntityBean;

public interface PackStrategy {
    // 初始化本地配置项
    List<EntityBean> initEntityProp();
}
