package com.unionware.emes.viewmodel.process

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.util.ConvertUtils
import unionware.base.app.event.SingleLiveEvent
import unionware.base.model.bean.barcode.BarCodeBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
open class CalibrationViewModel @Inject constructor() : ProcessViewModel() {
    /**
     * 气体物料条码扫描
     */
    val gasBarcodeLiveData: SingleLiveEvent<BarCodeBean> = SingleLiveEvent()

    /**
     * 扫描条码
     */
    open fun queryGasBarcode(
        filtersReq: FiltersReq,
        scene: String = "MES.Process.Electronic",
        name: String = "66960F0C0DE7EE",
    ) {
        sponsorsLiveData.value?.also {
            filtersReq.sponsors = it
        }
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        barcodeRequestAny(filtersReq, scene, name, object : ICallback<Any> {
            override fun onSuccess(data: Any?) {
                val map = Gson().fromJson<CommonListDataResp<Map<String, Any>>>(
                    Gson().toJson(data),
                    (object : TypeToken<CommonListDataResp<Map<String, Any>>?>() {}).type
                )
                val barCode = Gson().fromJson<CommonListDataResp<BarCodeBean>>(
                    Gson().toJson(data),
                    (object : TypeToken<CommonListDataResp<BarCodeBean>?>() {}).type
                )
                if (barCode?.data?.isNotEmpty() == true) {
                    barCode.data?.get(0)?.also {
                        map.view?.filter { v ->
                            v.key == "code" || v.key == "MaterialId" || v.key == "materialId"
                        }?.forEach { v ->
                            v.isVisible = false
                        }
                        it.infoList = ConvertUtils.convertMapToList(map.view, map.data)
                        gasBarcodeLiveData.value = it
                    }
                } else {
                    gasBarcodeLiveData.value = BarCodeBean("")
                    mUIChangeLiveData.getShowToastViewEvent()
                        .postValue("未查询到气体物料数据")//未查询气体物料数据
                }
                mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
            }

            override fun onFailure(e: ApiException) {
                barcodeELiveData.value = filtersReq.filters["primaryCode"].toString()
                gasBarcodeLiveData.value = BarCodeBean("")
                onVMFailure(e, fFiltersReq = filtersReq)
            }
        })
    }
}