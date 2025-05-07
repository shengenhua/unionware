package com.unionware.wms.inter.bill

import com.unionware.wms.api.PackingApi
import unionware.base.api.util.ConvertUtils
import unionware.base.app.view.base.mvp.BasePresenter
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ViewReq
import unionware.base.model.resp.CommonDataResp
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject


class BillListPresenter @Inject constructor(val api: PackingApi) :
    BasePresenter<BillListContract.View>(),
    BillListContract.Presenter {

    override fun requestList(scene: String?, name: String?, filters: FiltersReq?) {
        NetHelper.request(
            api.getCommonList(scene, name, filters),
            mView,
            object : ICallback<CommonListDataResp<Map<String, Any>>> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    if (data!!.data.isNotEmpty())
                        mView.showList(ConvertUtils.convertViewToList(data.view, data.data))
                    else
                        mView.showEmptyView()
                }

                override fun onFailure(e: ApiException) {
                    mView.showFailedView(e.errorMsg)
                }
            })
    }

    override fun delete(viewReq: ViewReq, pos: Int) {
        //deleteBarcodeDetails
        NetHelper.request(
            api.deleteBarcodeDetails(viewReq),
            mView,
            object : ICallback<CommonDataResp?> {
                override fun onSuccess(data: CommonDataResp?) {
                    //删除成功更新ui
                    mView.deleteItem(pos);
                }

                override fun onFailure(e: ApiException) {
                    mView.showFailedView(e.errorMsg)
                }
            })
    }

}
