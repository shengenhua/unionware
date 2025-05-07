package com.unionware.mes.view.basics

import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.basicui.base.viewmodel.BaseDetailsViewModel
import com.unionware.mes.app.RouterMESPath
import com.unionware.path.RouterPath
import dagger.hilt.android.AndroidEntryPoint


/**
 * 详情数据界面
 */
@AndroidEntryPoint
@Route(path = RouterMESPath.MES.PATH_MES_BILL_DETAILS)
open class DetailsActivity : MESDetailsActivity<BaseDetailsViewModel>() {

}