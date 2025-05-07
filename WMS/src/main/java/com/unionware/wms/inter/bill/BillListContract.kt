package com.unionware.wms.inter.bill

import unionware.base.app.view.base.mvp.IPresenter
import unionware.base.app.view.base.mvp.IView
import unionware.base.model.bean.BillBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ViewReq

class BillListContract {
    interface View : IView {
        fun showFailedView(msg: String)

        fun showList(list: List<BillBean>)

        /**
         * 显示空白布局
         */
        fun showEmptyView()

        /**
         * 删除成功
         */
        fun deleteItem(pos: Int)
    }

    interface Presenter : IPresenter<View> {
        fun requestList(scene: String?, name: String?, filters: FiltersReq?)

        /**
         * 删除明细
         */
        fun delete(viewReq: ViewReq, pos: Int)
    }
}