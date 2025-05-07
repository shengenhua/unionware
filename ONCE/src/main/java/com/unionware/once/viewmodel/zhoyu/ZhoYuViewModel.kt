package com.unionware.once.viewmodel.zhoyu

import androidx.lifecycle.MutableLiveData
import com.unionware.once.api.OnceApi
import com.unionware.once.model.BarcodeResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.util.ConvertUtils
import unionware.base.app.event.SingleLiveEvent
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.model.bean.BillBean
import unionware.base.model.bean.ViewBean
import unionware.base.model.req.BarcodePrintExportReq
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
open class ZhoYuViewModel @Inject constructor() : BaseViewModel() {
    @JvmField
    @Inject
    var api: OnceApi? = null

    /**
     * 记录当前页数
     */
    var pageIndexData: MutableLiveData<Int> = MutableLiveData<Int>(1)

    /**
     * 显示的列表
     */
    var dataLiveData: MutableLiveData<List<BillBean>> = MutableLiveData<List<BillBean>>()

    /**
     * 打印的数据
     */
    var printLiveData: SingleLiveEvent<String> = SingleLiveEvent()

    open fun getScanBarcode(
        scene: String = "UserDefine.ZY.DPEndEdReport",
        name: String = "6747EB0580E39B",
        filtersReq: FiltersReq = FiltersReq(),
    ) {
        pageIndexData.value?.also { filtersReq.pageIndex = it }

        NetHelper.request<CommonListDataResp<Map<String, Any>>?>(api!!.query(
            scene, name, filtersReq
        ), lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
            override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                if (data != null && !data.data.isNullOrEmpty()) {
                    dataLiveData.value = ConvertUtils.convertViewToList(data.view, data.data)
                } else {
                    dataLiveData.value = mutableListOf()
                    if (pageIndexData.value == 1) {
                        mUIChangeLiveData.getShowToastViewEvent()
                            .postValue("找不到数据，请查看条码是否正确")
                    } else {
                        mUIChangeLiveData.getShowToastViewEvent().postValue("没有更多数据")
                    }
                }
            }

            override fun onFailure(e: ApiException) {
                pageIndexData.value = pageIndexData.value?.minus(1)
                dataLiveData.value = mutableListOf()
                mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
            }
        })
    }

    fun print(
        scene: String = "UserDefine.ZY.DPEndEdReport",
        reportId: Int,
    ) {
        postShowTransLoadingViewEvent(true)
        NetHelper.request(api?.barCodeExport(mutableMapOf(Pair("ReportId", reportId as Any))),
            lifecycle,
            object : ICallback<BarcodeResponse> {
                override fun onSuccess(data: BarcodeResponse) {
                    if (data.jarray.isEmpty()) {
                        mUIChangeLiveData.getShowToastViewEvent().postValue("打印失败")
                        postShowTransLoadingViewEvent(false)
                        return
                    }
                    printExport(scene, BarcodePrintExportReq().apply {
                        /*items = mutableListOf<Map<String, Any>>().apply {
                            data.jarray[0].items.forEach {
                                val map = mutableMapOf<String, Any>()
                                it.forEach { entry ->
                                    if (entry.key == "code") {
                                        map["id"] = entry.value
                                    } else {
                                        map[entry.key] = entry.value.bigDecimalToZeros()
                                    }
                                }
                                data.jarray[0].params["template"]?.apply {
                                    map["template"] = this
                                }
                                add(map)
                            }
                        }
                        params = mutableMapOf<String?, Any?>().apply {
                            data.jarray[0].params.forEach { (t, u) ->
                                if (t != "template") {
                                    this[t] = u
                                }
                            }
                        }*/
                        items = data.jarray[0].items
                        params = data.jarray[0].params

                        /*data.jarray[0].formId?.let {
                            formId = it
                        }*/
                        formId = "BD_BARCODEMAINFILE"
                        data.jarray[0].server?.let {
                            server = it
                        }
                    })
                }

                override fun onFailure(e: ApiException?) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e?.errorMsg)
                    postShowTransLoadingViewEvent(false)
                }
            })
    }

    private fun printExport(
        scene: String = "UserDefine.ZY.DPEndEdReport",
        printReq: BarcodePrintExportReq = BarcodePrintExportReq(),
    ) {
        NetHelper.request(
            api?.getPrintExport(scene, printReq),
            lifecycle,
            object : ICallback<String> {
                override fun onSuccess(data: String) {
                    if ("!PrinterRemoteService" != data) {
                        printLiveData.postValue(data)
//                        mUIChangeLiveData.getShowToastViewEvent().postValue("打印成功")
                    } else {
                        mUIChangeLiveData.getShowToastViewEvent().postValue("已发送打印指令")
                        postShowTransLoadingViewEvent(false)
                    }
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                    postShowTransLoadingViewEvent(false)
                }
            })
    }


    open fun empPieceSearch(
        map: MutableMap<String?, Any?> = mutableMapOf(),
    ) {
        NetHelper.request(api!!.empPieceSearch(map),
            lifecycle,
            object : ICallback<BarcodeResponse?> {
                override fun onSuccess(data: BarcodeResponse?) {
                    if (data == null || data.jarray.isEmpty()) {
                        dataLiveData.value = mutableListOf()
                        mUIChangeLiveData.getShowToastViewEvent().postValue("没有更多数据")
                        return
                    }
                    data.jarray[0].items.apply {
                        dataLiveData.value = ConvertUtils.convertViewToList(getEmpPieceView(), this)
                    }
                }

                override fun onFailure(e: ApiException) {
                    dataLiveData.value = mutableListOf()
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    private fun getEmpPieceView(): List<ViewBean> {
        return mutableListOf<ViewBean>().apply {
            add(ViewBean("empNumber", "员工编号", true))
            add(ViewBean("empName", "员工姓名", true))
            add(ViewBean("postEmpName", "岗位", true))
            add(ViewBean("className", "班次", true))
            add(ViewBean("makeType", "加工方式", true))
            add(ViewBean("date", "报工时间", true))
            add(ViewBean("sumLen", "产量（米）", true))
            add(ViewBean("amount", "计件工资（元）", true))
        }

    }
}