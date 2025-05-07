package com.unionware.once.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unionware.basicui.base.viewmodel.BaseCollectViewModel
import com.unionware.once.api.OnceApi
import com.unionware.once.model.StockReq
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.app.event.SingleLiveEvent
import unionware.base.model.bean.barcode.BarCodeBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import unionware.base.network.request
import javax.inject.Inject


@HiltViewModel
open class GelatinBindViewModel @Inject constructor() : BaseCollectViewModel() {

    var gelatinBindLive: SingleLiveEvent<Any?> = SingleLiveEvent()

    @JvmField
    @Inject
    var api: OnceApi? = null


    /**
     * 货位绑定
     */
    fun stockBindAction(map: Map<String, Any>) {
        NetHelper.request(api?.StockBindAction(map),
            lifecycle, object : ICallback<Any?> {
                override fun onSuccess(data: Any?) {
                    gelatinBindLive.value = data
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    var basicLiveData: MutableLiveData<StockReq> = MutableLiveData<StockReq>()

    fun queryBasic(scene: String?, name: String?, req: FiltersReq?) {
        api?.queryToAny(scene, name, req)?.request(lifecycle){
            success {any->
                any?.let {
                    Gson().fromJson<CommonListDataResp<StockReq>>(
                        Gson().toJson(it),
                        (object : TypeToken<CommonListDataResp<StockReq>>() {}).type
                    )
                }?.also {
                    if (it.data?.isNotEmpty() == true) {
                        basicLiveData.value = it.data.first()
                    }
                }
            }
            failure {
                mUIChangeLiveData.getShowToastViewEvent().postValue(it.errorMsg)
            }
        }
    }
    /**
     * 扫描条码
     */
    override fun queryBarcode(
        filtersReq: FiltersReq,
        scene: String,
        name: String,
    ) {
        //防止短时间 请求多个
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        barcodeRequest(filtersReq,
            scene,
            name,
            object : ICallback<CommonListDataResp<BarCodeBean>> {
                override fun onSuccess(data: CommonListDataResp<BarCodeBean>?) {
                    if (data?.data?.isNotEmpty() == true) {
                        data.data?.get(0)?.also {
                            barcodeLiveData.value = it
                        }
                    } else {
                        mUIChangeLiveData.getShowToastViewEvent().postValue("无查询数据")
                    }
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    open fun barcodeRequest(
        filtersReq: FiltersReq,
        scene: String = "MES.Process.Electronic",
        name: String = "66960F0C0DE7EE",
        callback: ICallback<CommonListDataResp<BarCodeBean>>,
    ) {
        NetHelper.request(api?.queryToAny(
            scene, name, filtersReq
        ), lifecycle, object : ICallback<Any?> {
            override fun onSuccess(data: Any?) {
                val dataResp = Gson().fromJson<CommonListDataResp<BarCodeBean>>(
                    Gson().toJson(data),
                    (object : TypeToken<CommonListDataResp<BarCodeBean>?>() {}).type
                )
                callback.onSuccess(dataResp)
            }

            override fun onFailure(e: ApiException) {
                callback.onFailure(e)
            }
        })
    }
}