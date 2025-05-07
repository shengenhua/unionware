package com.unionware.query.view.base

import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.query.viewmodel.DynamicQueryViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Author: sheng
 * Date:2025/3/6
 */
@AndroidEntryPoint
@Route(path = "/query/dyamic")
class QueryDyActivity : QueryDynamicActivity<DynamicQueryViewModel>() {
}