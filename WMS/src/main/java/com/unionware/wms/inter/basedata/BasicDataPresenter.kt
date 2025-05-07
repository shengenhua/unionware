package com.unionware.wms.inter.basedata
import com.unionware.wms.api.PackingApi
import com.unionware.wms.inter.basedata.BasicDataContract
import unionware.base.app.view.base.mvp.BasePresenter
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

class BasicDataPresenter @Inject constructor(val api: PackingApi) :
    BasePresenter<BasicDataContract.View>(), BasicDataContract.Presenter {
    override fun queryBasicData(scene: String?, name: String?, filters: FiltersReq?) {
        NetHelper.request(
            api.getBaseInfoList(scene, name, filters),
            mView,
            object : ICallback<CommonListDataResp<BaseInfoBean>> {
                override fun onSuccess(data: CommonListDataResp<BaseInfoBean>?) {
                    //基础数据
                    mView.showBasicDataList(data?.data)
                }

                override fun onFailure(e: ApiException?) {
                    mView.showFailedView(e?.errorMsg)
                }
            })
    }

}