package com.unionware.once.viewmodel

import androidx.lifecycle.MutableLiveData
import com.unionware.once.viewmodel.process.ProcessViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.model.bean.BarcodeMapBean
import unionware.base.model.bean.CommonListBean
import unionware.base.model.bean.ViewBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.model.resp.ReportResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject


@HiltViewModel
open class PackProcessViewModel @Inject constructor() : ProcessViewModel() {

    /**
     * 扫描箱码,返回需要扫描的数据
     */
    val packBarcodeLiveData: MutableLiveData<List<BarcodeMapBean>?> =
        MutableLiveData<List<BarcodeMapBean>?>()

    /**
     * 扫描箱码,返回需要扫描的数据
     */
    val checkPackLiveData: MutableLiveData<ReportResp?> =
        MutableLiveData<ReportResp?>()


    private fun barcodeList(
        tagKey: String,
        dataMap: List<Map<String, Any>>?,
        views: List<ViewBean>?,
    ): List<BarcodeMapBean>? {
        var tagName = ""
        val viewMap = views?.filter {
            if (it.key == tagKey) {
                tagName = it.name
            }
            it.isVisible && it.key != tagKey && "packCode" != it.key// packCode 隐藏包装条码
        }?.associate {
            it.key to it.name
        }
        return dataMap?.map { data ->
            BarcodeMapBean(data[tagKey].toString()).apply {
                this.tagName = tagName
                this.value = data
                list = viewMap?.map {
                    CommonListBean(it.value, data[it.key].toString())
                }
            }
        }
    }

    fun packQuery(scene: String, name: String = "66EBCD0EFE87D8", filtersReq: FiltersReq) {
        NetHelper.request(api?.query(
            scene, name, filtersReq
        ), lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
            override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                packBarcodeLiveData.value = barcodeList("barCode", data?.data, data?.view)
            }

            override fun onFailure(e: ApiException) {
                onVMFailure(e, fFiltersReq = filtersReq)
            }
        })
    }

    fun checkPackRecord(filters: Map<String, String>) {
        NetHelper.request(
            api?.checkPackRecord(filters),
            lifecycle,
            object : ICallback<ReportResp?> {
                override fun onSuccess(data: ReportResp?) {
                    checkPackLiveData.value = data
                }

                override fun onFailure(e: ApiException) {
                    checkPackLiveData.value = null
                    onVMFailure(e)
                }
            })
    }
}