package com.unionware.wms.inter.baseview

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.unionware.wms.api.PackingApi
import com.unionware.wms.model.bean.ScanConfigBean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject


class ViewViewModel : BaseViewModel() {

    val viewDataLiveData = MutableLiveData<ScanConfigBean>()
    val errorStateFlow = MutableStateFlow("")

    @JvmField
    @Inject
    var api: PackingApi? = null

    fun initData() {

    }

    override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
        super.onAny(owner, event)
    }

    fun getBoxStateId(scene: String?, name: String?, req: FiltersReq?) {
        NetHelper.request<CommonListDataResp<ScanConfigBean>>(
            api!!.getBoxStateId(scene, name, req),
            lifecycle, object : ICallback<CommonListDataResp<ScanConfigBean>> {
                override fun onSuccess(data: CommonListDataResp<ScanConfigBean>) {
                    viewDataLiveData.value = data.data[0]
                }

                override fun onFailure(e: ApiException) {
                    runBlocking {
                        errorStateFlow.emit(e.errorMsg)
                    }
                }
            })
    }
}