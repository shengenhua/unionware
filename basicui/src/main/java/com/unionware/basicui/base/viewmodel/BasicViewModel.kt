package com.unionware.basicui.base.viewmodel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import unionware.base.network.request
import javax.inject.Inject

@HiltViewModel
class BasicViewModel @Inject constructor() : BaseQueryListViewModel() {

    var basicLiveData: MutableLiveData<List<BaseInfoBean>> = MutableLiveData<List<BaseInfoBean>>()

    fun queryBasic(scene: String?, name: String?, req: FiltersReq?) {
        NetHelper.request(api?.getBaseInfoList(scene, name, req),
            lifecycle, object : ICallback<CommonListDataResp<BaseInfoBean>?> {
                override fun onSuccess(data: CommonListDataResp<BaseInfoBean>?) {
                    data?.data?.also {
                        if (pageIndex.value != 1) {
                            if (it.size < 20) {
                                pageIndex.value = pageIndex.value?.minus(1)
                            }
                        }
                        basicLiveData.value = it
                    }
                }

                override fun onFailure(e: ApiException) {
                    pageIndex.value = pageIndex.value?.minus(1)
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    var otherBasicLiveData: MutableLiveData<CommonListDataResp<Map<String, String>>> =
        MutableLiveData<CommonListDataResp<Map<String, String>>>()

    fun queryOtherBasic(scene: String?, name: String?, req: FiltersReq?) {
        api?.queryToMapString(scene, name, req)?.request(lifecycle) {
            success {
                otherBasicLiveData.value = it
            }
            failure {
                mUIChangeLiveData.getShowToastViewEvent().postValue(it.errorMsg)
            }
        }
    }
}