package com.unionware.basicui.base.viewmodel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.basic.BasicApi
import unionware.base.api.util.ConvertUtils
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
open class BaseQueryListViewModel @Inject constructor() : BaseViewModel() {

    var viewLiveData: MutableLiveData<List<unionware.base.model.bean.BillBean>> = MutableLiveData<List<unionware.base.model.bean.BillBean>>()
    var dataLiveData: MutableLiveData<List<Map<String, Any>>> =
        MutableLiveData<List<Map<String, Any>>>()

    var pageIndex: MutableLiveData<Int> = MutableLiveData<Int>(1)

    @JvmField
    @Inject
    var api: BasicApi? = null

    /**
     *     "configId" : 应用配置Id （可选）
     *         "keyword" : 模糊搜索（可选）
     */
    fun query(
        scene: String = "MES.Process.Electronic",
        name: String = "661779469DFB27",
        configId: String,
        keyword: String?,
    ) {
        val filtersReq = unionware.base.model.req.FiltersReq(pageIndex.value)
        filtersReq.filters = mutableMapOf<String?, Any?>().apply {
            if(configId.isNotEmpty()){
                put("configId", configId)
            }
            if(!keyword.isNullOrEmpty()){
                put("keyword", keyword)
            }
        }
        query(scene = scene, name, filtersReq = filtersReq)
    }

    fun query(scene: String, name: String = "661779469DFB27", filtersReq: unionware.base.model.req.FiltersReq) {
        NetHelper.request<unionware.base.model.resp.CommonListDataResp<Map<String, Any>>>(api?.query(
            scene,
            name,
            filtersReq
        ),
            lifecycle, object : ICallback<unionware.base.model.resp.CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: unionware.base.model.resp.CommonListDataResp<Map<String, Any>>?) {
                    data?.data?.also {
                        if (pageIndex.value == 1) {
                            dataLiveData.value = it
                            viewLiveData.value =
                                ConvertUtils.convertViewToList(data.view, data.data)
                        } else {
                            viewLiveData.value =
                                ConvertUtils.convertViewToList(data.view, data.data)
                            if (it.size < 20) {
                                pageIndex.value = pageIndex.value?.minus(1)
                            }
                            val dataList = dataLiveData.value
                            dataLiveData.value = dataList?.plus(it)
                        }
                    }
                    if (data == null) {
                        viewLiveData.value = mutableListOf()
                    }
                }

                override fun onFailure(e: ApiException) {
                    pageIndex.value = pageIndex.value?.minus(1)
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }
}