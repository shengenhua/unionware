package com.unionware.virtual.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unionware.base.lib_common.model.resp.ListDataViewResp
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.virtual.model.ErrorSponsors
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import unionware.base.api.SimulateApi
import unionware.base.api.basic.BasicApi
import unionware.base.app.event.SingleLiveEvent
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.model.bean.PropertyBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ViewReq
import unionware.base.model.resp.AnalysisInfoResp
import unionware.base.model.resp.AnalysisResp
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import unionware.base.network.request
import unionware.base.network.response.BaseResponse
import javax.inject.Inject

/**
 * 虚拟视图的 viewModel 类
 * 使用 SingleLiveEvent 的liveData 确保 使用最新的数据不会调用多次使用旧数据
 *
 */
@HiltViewModel
open class VirtualViewModel @Inject constructor() : BaseViewModel() {
    @JvmField
    @Inject
    var basicApi: BasicApi? = null

    @JvmField
    @Inject
    var viewApi: SimulateApi? = null
    var showErrorDialogViewEvent: SingleLiveEvent<ErrorSponsors> = SingleLiveEvent()


    var virtualLiveEvent = VirtualViewLiveData()

    inner class VirtualViewLiveData : SingleLiveEvent<Any>() {
        /**
         * 配置信息
         */
        var configLiveData: SingleLiveEvent<MutableMap<String, String>> = SingleLiveEvent()

        /**
         * 当前的 pageId 视图 id
         */
        var pageIdLiveData: SingleLiveEvent<String> = SingleLiveEvent()

        /**
         * 最新的 View 虚拟视图
         */
        var viewLiveData: MutableLiveData<List<PropertyBean>> =
            MutableLiveData<List<PropertyBean>>()

        /**
         * 最新的 View 虚拟视图 的数据
         */
        var dataLiveData: SingleLiveEvent<AnalysisResp?> =
            SingleLiveEvent()

        /**
         * 合并数据后的 虚拟视图
         */
        var viewDataLiveData =
            MediatorLiveData<List<PropertyBean>>()

        /**
         * 关闭当前的 View 虚拟视图
         */
        var closeTaskLiveData: MutableLiveData<AnalysisInfoResp> =
            MutableLiveData<AnalysisInfoResp>()

        init {
            configLiveData.value = mutableMapOf()
            viewDataLiveData.addSource(viewLiveData) {
                if (dataLiveData.value != null) {
                    combineViewData(it, dataLiveData.value)
                }
            }
            viewDataLiveData.addSource(dataLiveData) {
                if (viewLiveData.value != null) {
                    combineViewData(viewLiveData.value, it)
                }
            }
        }

        private fun combineViewData(
            view: List<PropertyBean>?,
            data: AnalysisResp?,
        ) {
            // 数据更新渲染
            view?.onEach {
                val bean = data?.fBillHead?.get(0)?.get(it.key)
                it.isEnable = bean?.isEnabled == true

                it.value = if (it.type == "ASSISTANT" || it.type == "BASEDATA") bean?.number
                else bean?.value
            }.also {
                viewDataLiveData.postValue(it)
            }
        }
    }

    /**
     * 获取配置信息 和 查询是否存在已经创建的虚拟视图
     */
    fun getConfig(scene: String, name: String, filters: FiltersReq) {
        basicApi?.queryToMapString(scene, name, filters)?.request(lifecycle) {
            success {
                it?.data.also {
//                        virtualLiveEvent.configLiveData.value = it?.get(0)
                }
                postShowTransLoadingViewEvent(false)
            }
            failure {
                mUIChangeLiveData.getShowToastViewEvent().postValue(it.errorMsg)
            }
        }
    }

    /**
     * 查询是否存在已经创建的虚拟视图
     */
    fun queryHasView(scene: String, name: String, filters: FiltersReq) {
        NetHelper.request(
            basicApi?.queryToMapString(scene, name, filters),
            lifecycle,
            object : ICallback<CommonListDataResp<Map<String, String>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, String>>?) {
                    val req = ViewReq(
                        virtualLiveEvent.configLiveData.value?.get("formId") ?: "", mutableMapOf()
                    ).apply {
                        compact = true
                    }
                    //存在 显示  不存在 创建
                    if (data == null || data.data.isNullOrEmpty() || data.data[0].isNullOrEmpty() || data.data[0]["id"].isNullOrEmpty()) {
                        //不存在 创建
                        req.params["appSetId"] =
                            virtualLiveEvent.configLiveData.value?.get("appSetId") ?: ""
                    } else {
                        //存在
                        req.primaryId = data.data[0]["id"]
                    }
                    createViewGetData(req)
                    postShowTransLoadingViewEvent(false)
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                    postShowTransLoadingViewEvent(false)
                }
            })
    }

    /**
     * 创建虚拟视图
     */
    fun caretVirtualView(viewReq: ViewReq) {
        NetHelper.request(viewApi?.createView(viewReq), lifecycle, object : ICallback<String?> {
            override fun onSuccess(pageId: String?) {
                pageId?.apply {
                    virtualLiveEvent.pageIdLiveData.value = this@apply
                }
                postShowTransLoadingViewEvent(false)
            }

            override fun onFailure(e: ApiException) {
                mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
            }
        })
    }

    /**
     * 获取虚拟视图
     */
    fun getVirtualView(pageId: String) {
        NetHelper.request(
            viewApi?.getSimpleView(HashMap<String?, String?>().apply {
                put("pageId", pageId)
            }),
            lifecycle,
            object : ICallback<List<PropertyBean>?> {
                override fun onSuccess(pageId: List<PropertyBean>?) {
                    pageId?.apply {
                        virtualLiveEvent.viewLiveData.value = this@apply
                    }
                    postShowTransLoadingViewEvent(false)
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }


    /**
     * 创建虚拟视图
     */
    fun createViewGetData(viewReq: ViewReq) {
        postShowTransLoadingViewEvent(true)
        NetHelper.request(
            viewApi?.createViewGetData(viewReq),
            lifecycle,
            object :
                ICallback<ListDataViewResp<AnalysisResp, PropertyBean>?> {
                override fun onSuccess(data: ListDataViewResp<AnalysisResp, PropertyBean>?) {
                    data?.apply {
                        virtualLiveEvent.viewLiveData.value = view
                        virtualLiveEvent.dataLiveData.value = this.data
                        virtualLiveEvent.pageIdLiveData.value = this.pageId
                    }
                    postShowTransLoadingViewEvent(false)
                }

                override fun onFailure(e: ApiException) {
                    postShowTransLoadingViewEvent(false)
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }


    /**
     * 获取作业视图视图模型 数据
     */
    fun updateVirtualView(viewReq: ViewReq) {
        viewReq.pageId = virtualLiveEvent.pageIdLiveData.value
        request(
            viewApi?.updateView(viewReq),
            object : ICallback<AnalysisInfoResp?> {
                override fun onSuccess(data: AnalysisInfoResp?) {
                    data?.apply {
                        virtualLiveEvent.dataLiveData.value = this.data
                    }
                    postShowTransLoadingViewEvent(false)
                }

                override fun onFailure(e: ApiException) {
                    viewReq.simulate = "UpdateValue"
                    onVMFailure(e, viewReq)
                    getVirtualViewData()
                }
            })
    }

    /**
     * 关闭虚拟视图
     */
    fun closeVirtualView(pageId: String, finish: Boolean = true) {
        postShowTransLoadingViewEvent(true)
        NetHelper.request(viewApi?.closeView(HashMap<String?, String?>().apply {
            put("pageId", pageId)
        }), lifecycle, object : ICallback<String?> {
            override fun onSuccess(data: String?) {
                //
                virtualLiveEvent.pageIdLiveData.value = null
                if (finish) {
                    postFinishActivityEvent()
                }
                postShowTransLoadingViewEvent(true)
            }

            override fun onFailure(e: ApiException) {
                mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                postShowTransLoadingViewEvent(false)
            }
        })
    }


    /**
     * 获取作业视图数据模型  数据
     */
    fun getVirtualViewData() {
        NetHelper.request(
            viewApi?.getSimpleModel(
                ViewReq(
                    virtualLiveEvent.pageIdLiveData.value
                )
            ),
            lifecycle,
            object : ICallback<AnalysisResp?> {
                override fun onSuccess(data: AnalysisResp?) {
                    data?.apply {
                        virtualLiveEvent.dataLiveData.value = this
                    }
                    postShowTransLoadingViewEvent(false)
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                    postShowTransLoadingViewEvent(false)
                }
            })
    }


    /**
     * 确认步骤
     */
    fun commandViewData(command: String) {
        postShowTransLoadingViewEvent(true)
        val viewReq =
            ViewReq(virtualLiveEvent.pageIdLiveData.value)
        viewReq.command = command
        request(
            viewApi?.commandViewData(viewReq),
            object : ICallback<AnalysisInfoResp?> {
                override fun onSuccess(data: AnalysisInfoResp?) {
                    //更新数据
                    data?.apply {
                        virtualLiveEvent.dataLiveData.value = this.data
                    }
                    postShowTransLoadingViewEvent(false)
                }

                override fun onFailure(e: ApiException) {
                    viewReq.simulate = "Command"
                    onVMFailure(e, viewReq)
                    getVirtualViewData()
                }
            })
    }


    /**
     * 提交动作 自动调用  INVOKE_SUBMITTASK 提交任务流程
     */
    open fun confirmViewData(command: String) {//INVOKE_CONFIRMBARCODE 产品确认 INVOKE_SUBMITTASK 提交任务 结束自动关闭视图
        postShowTransLoadingViewEvent(true)
        val viewReq =
            ViewReq(virtualLiveEvent.pageIdLiveData.value)
        viewReq.command = command
        request(
            viewApi?.commandViewData(viewReq),
            object : ICallback<AnalysisInfoResp?> {
                override fun onSuccess(data: AnalysisInfoResp?) {
                    //更新数据
                    if ("INVOKE_SUBMITTASK" == viewReq.command) {
                        postShowTransLoadingViewEvent(false)
                        postFinishActivityEvent()
                        postShowToastViewEvent("提交成功")
                    } else {
                        confirmViewData("INVOKE_SUBMITTASK")
                        data?.apply {
                            virtualLiveEvent.dataLiveData.value = this.data
                        }
                    }
                }

                override fun onFailure(e: ApiException) {
                    viewReq.simulate = "Command"
                    onVMFailure(e, viewReq)
                    getVirtualViewData()
                }
            })
    }


    /**
     * 提交动作 自动调用  INVOKE_SUBMITTASK 提交任务流程
     * 不返回界面
     */
    open fun confirmSubmitTask() {//INVOKE_CONFIRMBARCODE 产品确认 INVOKE_SUBMITTASK 提交任务 结束自动关闭视图
        postShowTransLoadingViewEvent(true)
        val viewReq =
            ViewReq(virtualLiveEvent.pageIdLiveData.value)
        viewReq.command = "INVOKE_SUBMITTASK"
        request(
            viewApi?.commandViewData(viewReq),
            object : ICallback<AnalysisInfoResp?> {
                override fun onSuccess(data: AnalysisInfoResp?) {
                    postShowTransLoadingViewEvent(false)
                    virtualLiveEvent.closeTaskLiveData.value = data
//                postFinishActivityEvent()
                    postShowToastViewEvent("提交成功")
                }

                override fun onFailure(e: ApiException) {
                    viewReq.simulate = "Command"
                    onVMFailure(e, viewReq)
                    getVirtualViewData()
                }
            })
    }

    fun commandViewData(viewReq: ViewReq) {
        request(
            viewApi?.commandViewData(viewReq),
            object : ICallback<AnalysisInfoResp?> {
                override fun onSuccess(data: AnalysisInfoResp?) {
                    //更新数据
                    if ("INVOKE_SUBMITTASK" == viewReq.command) {
                        postFinishActivityEvent()
                        postShowToastViewEvent("提交成功")
                    } else {
                        data?.apply {
                            virtualLiveEvent.dataLiveData.value = this.data
                        }
                    }
                    postShowTransLoadingViewEvent(false)
                }

                override fun onFailure(e: ApiException) {
                    if ("INVOKE_SUBMITTASK" == viewReq.command) {
                        mUIChangeLiveData.getTTSSucOrFailEvent().postValue(false)
                    }
                    viewReq.simulate = "Command"
                    onVMFailure(e, viewReq)
                    getVirtualViewData()
                }
            })
    }

    /**
     * 关闭当前任务 （作废）
     */
    fun closeTaskViewData(command: String = "INVOKE_CANCELTASK") {
        mUIChangeLiveData.getShowLoadingViewEvent().postValue("正在作废任务...")
        val viewReq =
            ViewReq(virtualLiveEvent.pageIdLiveData.value)
        viewReq.command = command
        request(
            viewApi?.commandViewData(viewReq),
            object : ICallback<AnalysisInfoResp?> {
                override fun onSuccess(data: AnalysisInfoResp?) {
                    virtualLiveEvent.pageIdLiveData.value = null
                    virtualLiveEvent.closeTaskLiveData.value = data
                    mUIChangeLiveData.getShowLoadingViewEvent().postValue("")
                }

                override fun onFailure(e: ApiException) {
                    viewReq.simulate = "Command"
                    onVMFailure(e, viewReq)
                    getVirtualViewData()
                }
            })
    }


    open fun request(
        observable: Observable<BaseResponse<Any>>?,
        callback: ICallback<AnalysisInfoResp?>,
    ) {
        observable?.request(lifecycle) {
            success {
                val dataResp =
                    Gson().fromJson<AnalysisInfoResp>(
                        Gson().toJson(it),
                        (object :
                            TypeToken<AnalysisInfoResp?>() {}).type
                    )
                callback.onSuccess(dataResp)
                analysisAction(dataResp)
            }
            failure {
                callback.onFailure(it)
                try {
                    it.data?.apply {
                        analysisAction(
                            Gson().fromJson(
                                this,
                                AnalysisInfoResp::class.java
                            )
                        )
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        /*NetHelper.request(observable, lifecycle, object : ICallback<Any?> {
            override fun onSuccess(data: Any?) {
                val dataResp = Gson().fromJson<AnalysisInfoResp>(
                    Gson().toJson(data), (object : TypeToken<AnalysisInfoResp?>() {}).type
                )
                callback.onSuccess(dataResp)
                analysisAction(dataResp)
            }

            override fun onFailure(e: ApiException) {
                callback.onFailure(e)
                try {
                    e.data?.apply {
                        analysisAction(Gson().fromJson(this, AnalysisInfoResp::class.java))
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        })*/
    }

    fun analysisAction(resp: AnalysisInfoResp?) {
        resp?.apply {
            action.forEach {
                when (it.name) {
                    "TOAST" -> showToast(it.actionDetailResp.type, it.actionDetailResp.message)

                    else -> Unit
                }
            }
        }
    }

    /**
     *后台返回提示
     */
    fun showToast(type: Int, message: String) {
        when (type) {
            1 -> mUIChangeLiveData.getTTSEvent().postValue(SoundType.Default.SUCCESS)
            2, 4 -> mUIChangeLiveData.getTTSEvent().postValue(SoundType.Default.ERROR)
            3 -> mUIChangeLiveData.getTTSEvent().postValue(SoundType.Default.SUBMIT_SUCCESS)
        }
        postShowToastViewEvent(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        //如果 pageIdLiveData 还有值 没有关闭掉 在调用一次接口
        virtualLiveEvent.pageIdLiveData.value?.apply {
            closeVirtualView(this, false)
        }
    }


    protected open fun onVMFailure(
        e: ApiException, viewReq: ViewReq? = null,
    ) {
        val code = e.code.toIntOrNull()
        if (code != null && code >= 90000 && code <= 99999) {
            viewReq?.also {
                if (it.sponsors == null) {
                    it.sponsors = arrayOf()
                }
                val codes: MutableList<String> = it.sponsors.toMutableList()
                codes.add(e.data)
                it.sponsors = codes.toTypedArray()
            }
            //弹出对话框
            showErrorDialogViewEvent.value = ErrorSponsors(e.errorMsg).also {
                it.viewReq = viewReq
            }
        } else {
            mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
        }
        postShowTransLoadingViewEvent(false)
    }
}