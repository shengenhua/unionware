package com.unionware.basicui.base.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.basic.BasicApi
import unionware.base.api.util.ConvertUtils
import unionware.base.app.event.SingleLiveEvent
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.model.bean.DynamicConfigBean
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
open class BaseDetailsViewModel @Inject constructor(@JvmField @Inject var api: BasicApi) :
    BaseViewModel() {

    var viewLiveData: MutableLiveData<List<unionware.base.model.bean.CommonListBean>> =
        MutableLiveData<List<unionware.base.model.bean.CommonListBean>>()
    var dataLiveData: MutableLiveData<Map<String, Any>> =
        MutableLiveData<Map<String, Any>>()
    var viewBeanLive: MutableLiveData<List<unionware.base.model.bean.ViewBean>> =
        MutableLiveData<List<unionware.base.model.bean.ViewBean>>()
    var dyConfigLiveData: SingleLiveEvent<DynamicConfigBean?> = SingleLiveEvent()
    var dyConfigItemLiveData: SingleLiveEvent<DynamicConfigBean?> = SingleLiveEvent()

    //固定 name 6617982A9DFDAC
    fun query(scene: String, name: String = "6617982A9DFDAC", primaryId: String) {
        val filtersReq = unionware.base.model.req.FiltersReq()
        filtersReq.filters = mapOf(
            Pair("primaryId", primaryId)
        )
        NetHelper.request<unionware.base.model.resp.CommonListDataResp<Map<String, Any>>>(api.query(
            scene,
            name,
            filtersReq
        ),
            lifecycle, object : ICallback<unionware.base.model.resp.CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: unionware.base.model.resp.CommonListDataResp<Map<String, Any>>?) {
                    viewLiveData.value =
                        if (data == null) mutableListOf() else ConvertUtils.convertMapToList(
                            data.view, data.data
                        )
                    viewBeanLive.value = data?.view
                    dataLiveData.value = data?.data?.get(0)
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }
            })
    }

    /**
     * 用于判断 是否使用动态汇报
     * 获取工序参数 接口
     * 673AD6F10C66AA
     */
    fun getDynamicConfig(scene: String, name: String = "673AD6F10C66AA", filtersReq: unionware.base.model.req.FiltersReq) {
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        NetHelper.request(api.query(scene, name, filtersReq),
            lifecycle, object : ICallback<unionware.base.model.resp.CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: unionware.base.model.resp.CommonListDataResp<Map<String, Any>>?) {
                    //DynamicConfigBean
                    Gson().fromJson<DynamicConfigBean>(
                        Gson().toJson(data?.data?.get(0)),
                        (object : TypeToken<DynamicConfigBean?>() {}).type
                    )?.also {
                        dyConfigLiveData.value = it
                    }
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }

                override fun onFailure(e: ApiException?) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e?.errorMsg)
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }
            })
    }

    fun getItemDyConfig(scene: String, name: String = "673AD6F10C66AA", filtersReq: unionware.base.model.req.FiltersReq) {
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        NetHelper.request(api.query(scene, name, filtersReq),
            lifecycle, object : ICallback<unionware.base.model.resp.CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: unionware.base.model.resp.CommonListDataResp<Map<String, Any>>?) {
                    Gson().fromJson<DynamicConfigBean>(
                        Gson().toJson(data?.data?.get(0)),
                        (object : TypeToken<DynamicConfigBean?>() {}).type
                    )?.also {
                        dyConfigItemLiveData.value = it
                    }
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }

                override fun onFailure(e: ApiException?) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e?.errorMsg)
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }
            })
    }
}