package com.unionware.virtual.viewmodel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.SimulateApi
import unionware.base.api.basic.BasicApi
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.bean.BillBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.api.util.ConvertUtils
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
open class QueryListViewModel @Inject constructor() : BaseViewModel() {
    var basicLiveData: MutableLiveData<List<BaseInfoBean>> = MutableLiveData<List<BaseInfoBean>>()


    var viewLiveData: MutableLiveData<List<BillBean>> = MutableLiveData<List<BillBean>>()
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
    fun query(scene: String, searchId: String, configId: String, keyword: String?) {
        val filtersReq = FiltersReq(pageIndex.value)
        filtersReq.filters = mapOf(
            Pair("configId", configId),
            Pair("keyword", keyword)
        )

        NetHelper.request<CommonListDataResp<Map<String, Any>>>(api?.query(
            scene,
            searchId,
            filtersReq
        ),
            lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    data?.data?.also {
                        if (pageIndex.value == 1) {
                            dataLiveData.value = it
                        } else {
                            if (it.size < 20) {
                                pageIndex.value = pageIndex.value?.minus(1)
                            }
                            val dataList = dataLiveData.value
                            dataLiveData.value = dataList?.plus(it)
                        }
                    }
                    viewLiveData.value =
                        if (data == null) mutableListOf() else ConvertUtils.convertViewToList(
                            data.view, data.data
                        )
                }

                override fun onFailure(e: ApiException) {
                    pageIndex.value = pageIndex.value?.minus(1)
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }


    fun queryBasic(scene: String?, name: String?, req: FiltersReq?) {
        req?.apply {
            this@QueryListViewModel.pageIndex.value?.also {
                pageIndex = it
            }
        }
        NetHelper.request(api?.getBaseInfoList(scene, name, req),
            lifecycle, object : ICallback<CommonListDataResp<BaseInfoBean>?> {
                override fun onSuccess(data: CommonListDataResp<BaseInfoBean>?) {
                    data?.data?.also {
                        basicLiveData.value = it
                        if (it.isEmpty()) {
                            pageIndex.value = pageIndex.value?.minus(1)
                        }
                    }

                }

                override fun onFailure(e: ApiException) {
                    pageIndex.value = pageIndex.value?.minus(1)
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }
}