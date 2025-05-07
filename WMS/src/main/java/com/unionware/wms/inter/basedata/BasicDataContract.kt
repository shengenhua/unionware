package com.unionware.wms.inter.basedata

import unionware.base.app.view.base.mvp.IPresenter
import unionware.base.app.view.base.mvp.IView
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.req.FiltersReq


class BasicDataContract {
    interface View : IView {
        fun showFailedView(msg: String?)

        fun showBasicDataList(list: List<BaseInfoBean?>?)
    }

    interface Presenter : IPresenter<View> {
        /**
         * @param scene 场景码
         * @param name
         * @param filters 查询条件
         */
        fun queryBasicData(scene: String?, name: String?, filters: FiltersReq?)
    }
}