package com.unionware.query.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.query.model.AnalysisListResp
import com.unionware.virtual.model.ErrorSponsors
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import unionware.base.api.SimulateApi
import unionware.base.api.util.ConvertUtils
import unionware.base.app.event.SingleLiveEvent
import unionware.base.ext.bigDecimalToZeros
import unionware.base.ext.tryBigDecimalToZeros
import unionware.base.model.bean.BillBean
import unionware.base.model.bean.PropertyBean
import unionware.base.model.req.ItemBean
import unionware.base.model.req.ViewReq
import unionware.base.model.resp.ActionResp
import unionware.base.model.resp.AnalysisInfoResp
import unionware.base.model.resp.AnalysisResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import unionware.base.network.response.BaseResponse
import javax.inject.Inject


@HiltViewModel
open class DynamicQueryViewModel @Inject constructor() : BaseQueryViewModel() {
    @JvmField
    @Inject
    var viewApi: SimulateApi? = null

    var virtualView = VirtualView()
    var virtualViewRequest = VirtualViewRequest()


    override fun onDestroy() {
        super.onDestroy()
        //如果 pageIdLiveData 还有值 没有关闭掉 在调用一次接口
        virtualView.pageIdLiveData.value?.apply {
            virtualViewRequest.closeView(this, false)
        }
    }


    inner class VirtualView : SingleLiveEvent<Any>() {
        /**
         *  焦点位置，防止抢焦点
         *  -1 : 单据头
         *  0 ： 扫描框
         *  1 : 单据体
         *  2 : 子单据体
         */
        var focusPosition: MutableLiveData<Int> = MutableLiveData(-1)

        /**
         * 控制 是否显示 筛选条件
         */
        var entryAndSubViewLiveData: MutableLiveData<Boolean?> = MutableLiveData(false)

        /**
         * 当前的 pageId 视图 id
         */
        var pageIdLiveData: SingleLiveEvent<String?> = SingleLiveEvent()

        /**
         * 获取参数 运行时候
         */
        var optionsLiveData: SingleLiveEvent<MutableMap<String, Any>> =
            SingleLiveEvent()


        /**
         * INVOKE_CLIENTCLICKROW 点击行事件
         */
        var clickRowLiveData: SingleLiveEvent<AnalysisListResp> = SingleLiveEvent()

        /**
         * 最新的 View 虚拟视图 的数据
         */
        internal var viewLiveData: MutableLiveData<List<PropertyBean>> =
            MutableLiveData()

        /**
         * 最新的 View 虚拟视图 的数据
         */
        internal var dataListLiveData: MutableLiveData<List<BillBean>> = MutableLiveData()

        /**
         * 当前 data list 参数信息
         */
        var dataListOptionsLiveData: SingleLiveEvent<Map<String, String>> = SingleLiveEvent()

        /**
         * 最新的 View 虚拟视图 的数据
         */
        internal var dataLiveData: SingleLiveEvent<AnalysisResp> =
            SingleLiveEvent()

        /**
         * 单据头区域字段，构建单据头采集区域，否则隐藏该区域
         */
        val headCollectsLiveData: MutableLiveData<List<PropertyBean>> =
            MutableLiveData<List<PropertyBean>>()

        init {
            viewLiveData.observeForever {
                if (dataLiveData.value != null) {
                    combineViewData(it, dataLiveData.value)
                }
            }
            dataLiveData.observeForever {
                if (viewLiveData.value != null) {
                    combineViewData(viewLiveData.value, it)
                }
            }
            pageIdLiveData.observeForever {
                if (it == null) {
                    return@observeForever
                }
                virtualViewRequest.getViewData()
                virtualViewRequest.getView()
            }
        }


        /**
         * 数据整合
         */
        private fun combineViewData(
            /**数据模型*/
            view: List<PropertyBean>?,
            /**数据*/
            data: AnalysisResp?,
        ) {
            /* 单据头 */
            val headCollects =
                view?.filter { it.entity == "FBillHead" || it.entity == "FHeadCollects" }
                    ?.associateBy { it.key }
            val headData =
                data?.fBillHead?.get(0)?.plus(data.fHeadCollects?.get(0) ?: mutableMapOf())
                    ?.filter { it.value.isVisible }
            headCollects?.filter {
                headData?.containsKey(it.key) ?: false
            }?.map { (key, value) ->
                value.clone().apply {
                    headData?.get(key)?.also {
                        this.valueName = it.name
                        this.valueNumber = it.number
                        this.value = when (this.type) {
                            "ASSISTANT", "BASEDATA" -> {
                                it.number
                            }

                            "FLEXVALUE" -> {
                                it.name //it.number
                            }

                            else -> {
                                if (it.value.isNullOrEmpty()) it.name else it.value
                            }
                        }
                        this.isEnable = it.isEnabled == true
                    }
                }
            }.also {
                headCollectsLiveData.postValue(it)
            }

            if (entryAndSubViewLiveData.value == null) {
                entryAndSubViewLiveData.value = false
            }
            mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
        }
    }

    open inner class VirtualViewRequest {
        /**
         * 创建虚拟视图
         */
        fun createView(viewReq: ViewReq) {
            mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
            NetHelper.request(viewApi?.createView(viewReq), lifecycle, object : ICallback<String?> {
                override fun onSuccess(data: String?) {
                    data?.apply {
                        virtualView.pageIdLiveData.value = this
                    }
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
        }

        fun getViewData() {
            NetHelper.request(
                viewApi?.getSimpleModel(
                    ViewReq().apply {
                        pageId = virtualView.pageIdLiveData.value
                    }),
                lifecycle,
                object : ICallback<AnalysisResp?> {
                    override fun onSuccess(data: AnalysisResp?) {
                        data?.apply {
                            virtualView.dataLiveData.value = this
                        }
                    }

                    override fun onFailure(e: ApiException) {
                        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                        mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                    }
                })
        }

        fun getView() {
            NetHelper.request(
                viewApi?.getSimpleView(mutableMapOf("pageId" to virtualView.pageIdLiveData.value)),
                lifecycle,
                object :
                    ICallback<List<PropertyBean>?> {
                    override fun onSuccess(data: List<PropertyBean>?) {
                        data?.apply {
                            virtualView.viewLiveData.value = this
                            virtualView.viewLiveData.value?.let {
//                                combineViewData(it, dataLiveData.value)
                            }
                        }
                    }

                    override fun onFailure(e: ApiException) {
                        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                        mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                    }
                })
        }


        /**
         * 更新 字段 设置数据
         * UnionWare.Simulate.UpdateValue
         * 修改数据采集区域字段时，发送值更新事件。
         * @param key 更新的参数id
         * @param value 更新的值
         * @param row 更新单据体和子单据体字段时，需指定行索引(行号-1) 单据头字段行索引为 0。
         */
        fun updateView(key: String?, value: String?, row: Int = -1, call: (() -> Unit)? = null) {
            updateVirtualView(ViewReq().apply {
                items = listOf(
                    ItemBean(
                        key,
                        value,
                        row
                    )
                )
            }, call)
        }

        /**
         * 获取作业视图视图模型 数据
         */
        fun updateVirtualView(viewReq: ViewReq, call: (() -> Unit)? = null) {
            viewReq.pageId = virtualView.pageIdLiveData.value
            request(
                viewApi?.updateView(viewReq),
                object : ICallback<AnalysisInfoResp?> {
                    override fun onSuccess(data: AnalysisInfoResp?) {
                        data?.apply {
                            virtualView.dataLiveData.value = this.data
                        }
                        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                        call?.invoke()
                    }

                    override fun onFailure(e: ApiException) {
                        viewReq.simulate = "UpdateValue"
                        onVMFailure(e, viewReq)
                        getSimulateData()
                    }
                })
        }

        /**
         * @param entity FEntryCollects = 单据体标识， FSubEntryCollects  = 子单据体标识
         */
        fun commandRow(command: String, entity: String, row: Int) {
            commandViewData(
                ViewReq(virtualView.pageIdLiveData.value)
                    .apply {
                        this.command = command
                        params =
                            mutableMapOf<String, Any>("entity" to entity, "row" to (row.toString()))
                    })
        }

        fun command(command: String, commandParams: Map<String, Any>? = null) {
            when (command) {
                "INVOKE_CLIENTQUERY"/*查询功能*/ -> {
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
                    commandQueryData(ViewReq(command, virtualView.pageIdLiveData.value))
                }

                "INVOKE_CLIENTQUERYNEXT"/*查询功能 下一页*/ -> {
                    commandQueryData(ViewReq(command, virtualView.pageIdLiveData.value))
                }

                "INVOKE_CLIENTOPTIONS"/*运行时参数*/ -> {
                    virtualView.optionsLiveData.value.apply {
                        if (this.isNullOrEmpty()) {
                            commandMapData(ViewReq(command, virtualView.pageIdLiveData.value)) {
                                it?.data?.apply {
                                    virtualView.optionsLiveData.value = this.options
                                }
                            }
                        }
                    }
                }

                "INVOKE_CLIENTCLICKROW"/*运行时参数*/ -> {
                    commandMapData(ViewReq(command, virtualView.pageIdLiveData.value)) {
                        /*it?.data?.apply {
                            virtualView.clickRowLiveData.value = this.options
                        }*/
                        virtualView.clickRowLiveData.value = it
                    }
                }

                else -> {//"INVOKE_CLIENTCLICKROW"/*点击方法*/
                    commandViewData(ViewReq(command, virtualView.pageIdLiveData.value).apply {
                        commandParams?.apply {
                            params = this.mapValues { it.value.toString().tryBigDecimalToZeros() }
                        }
                    })
                }
            }
        }

        private fun commandMapData(viewReq: ViewReq, unit: ((AnalysisListResp?) -> Unit)? = null) {
            requestData(
                viewApi?.commandViewData(viewReq),
                object : ICallback<AnalysisListResp?> {
                    override fun onSuccess(data: AnalysisListResp?) {
                        unit?.invoke(data)
                        /*data?.data?.apply {
                            virtualView.optionsLiveData.value = this.options
                        }*/
                    }

                    override fun onFailure(e: ApiException) {
                        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                        viewReq.simulate = "Command"
                        onVMFailure(e, viewReq)
                        getSimulateData()
                    }
                })
        }

        private fun commandQueryData(viewReq: ViewReq) {
            requestData(
                viewApi?.commandViewData(viewReq),
                object : ICallback<AnalysisListResp?> {
                    override fun onSuccess(data: AnalysisListResp?) {
                        data?.data?.apply {
                            this.options?.apply {
                                virtualView.dataListOptionsLiveData.value =
                                    this.mapValues { it.value?.bigDecimalToZeros() ?: "" }
                            }

                            virtualView.dataListLiveData.value =
                                if (this.data == null) {
                                    mutableListOf()
                                } else {
                                    ConvertUtils.convertViewToListNoCode(this.view, this.data)
                                }
                            mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
//                                virtualView.pageIndexLiveData.value =
//                                    this["pageIndex"].bigDecimalToZeros().toInt()
                            /*virtualView.pageIndexLiveData.value =
                                this["pageIndex"].bigDecimalToZeros().toInt()*/
                        }
                    }

                    override fun onFailure(e: ApiException) {
                        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                        viewReq.simulate = "Command"
                        onVMFailure(e, viewReq)
                        getSimulateData()
                    }
                })
        }
        fun commandQuery(
            command: String,
            bean: PropertyBean? = null,
            callback: (AnalysisResp?) -> Unit,
        ) {
            if (bean?.type != "BASEDATA") {
                callback.invoke(null)
                return
            }
            val viewReq = ViewReq(virtualView.pageIdLiveData.value).apply {
                this.command = command
                params = mutableMapOf<String?, Any?>().apply {
                    bean.key?.let {
                        this["key"] = it
                    }
                }
            }
            request(viewApi?.commandViewData(viewReq), object : ICallback<AnalysisInfoResp?> {
                override fun onSuccess(data: AnalysisInfoResp?) {
                    data?.apply {
                        callback.invoke(this.data)
                    }
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                    viewReq.simulate = "Command"
                    onVMFailure(e, viewReq)
                    getSimulateData()
                }
            })
        }

        /**
         * 操作动作， 提交，确认，删除
         * 更新最新数据
         */
        fun commandViewData(viewReq: ViewReq) {
            when (viewReq.command) {
                "INVOKE_CLIENTCLICKROW"/*点击行事件*/ -> {
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
                }
            }
            request(
                viewApi?.commandViewData(viewReq),
                object : ICallback<AnalysisInfoResp?> {
                    override fun onSuccess(data: AnalysisInfoResp?) {
                        data?.apply {
                            virtualView.dataLiveData.value = this.data
                        }
                        when (viewReq.command) {
                            "INVOKE_CLIENTCLICKROW"/*点击行事件*/ -> {
                                mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                            }
                        }
                    }

                    override fun onFailure(e: ApiException) {
                        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                        viewReq.simulate = "Command"
                        onVMFailure(e, viewReq)
                        getSimulateData()
                    }
                })
        }

        /**
         * 刷新数据
         * 更新最新数据
         */
        fun getSimulateData() {
            NetHelper.request(
                viewApi?.getSimpleModel(
                    ViewReq(
                        virtualView.pageIdLiveData.value
                    )
                ),
                lifecycle,
                object : ICallback<AnalysisResp?> {
                    override fun onSuccess(data: AnalysisResp?) {
                        data?.apply {
                            virtualView.dataLiveData.value = this
                        }
                        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                    }

                    override fun onFailure(e: ApiException) {
                        mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                    }
                })
        }

        /**
         * 关闭虚拟视图
         */
        fun closeView(
            pageId: String,
            finish: Boolean = true,
        ) {
            postShowTransLoadingViewEvent(true)
            NetHelper.request(viewApi?.closeView(HashMap<String?, String?>().apply {
                put("pageId", pageId)
            }), lifecycle, object : ICallback<String?> {
                override fun onSuccess(data: String?) {
                    virtualView.pageIdLiveData.value = null
                    virtualView.entryAndSubViewLiveData.value = null
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


        protected open fun onVMFailure(
            e: ApiException,
            viewReq: ViewReq,
        ) {
            val code = e.code.toIntOrNull()
            if (code != null && code >= 90000 && code <= 99999) {
                viewReq.also {
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
            mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
        }

        open fun requestData(
            observable: Observable<BaseResponse<Any>>?,
            callback: ICallback<AnalysisListResp?>,
        ) {
            NetHelper.request(observable, lifecycle, object : ICallback<Any?> {
                override fun onSuccess(data: Any?) {
                    try {
                        val dataResp =
                            Gson().fromJson<AnalysisListResp>(
                                Gson().toJson(data),
                                (object :
                                    TypeToken<AnalysisListResp>() {}).type
                            )
                        callback.onSuccess(dataResp)
                        analysisAction(dataResp?.action)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        callback.onSuccess(null)
                    }
                }

                override fun onFailure(e: ApiException) {
                    callback.onFailure(e)
                    try {
                        e.data?.apply {
                            Gson().fromJson(this, AnalysisInfoResp::class.java).apply {
                                analysisAction(this?.action)
                            }
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            })
        }

        open fun request(
            observable: Observable<BaseResponse<Any>>?,
            callback: ICallback<AnalysisInfoResp?>,
        ) {
            NetHelper.request(observable, lifecycle, object : ICallback<Any?> {
                override fun onSuccess(data: Any?) {
                    try {
                        val dataResp =
                            Gson().fromJson<AnalysisInfoResp>(
                                Gson().toJson(data),
                                (object :
                                    TypeToken<AnalysisInfoResp?>() {}).type
                            )
                        callback.onSuccess(dataResp)
                        analysisAction(dataResp?.action)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        callback.onSuccess(null)
                    }
                }

                override fun onFailure(e: ApiException) {
                    callback.onFailure(e)
                    try {
                        e.data?.apply {
                            Gson().fromJson(this, AnalysisInfoResp::class.java).apply {
                                analysisAction(this?.action)
                            }
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            })
        }

        fun analysisAction(action: List<ActionResp>?) {
            action?.forEach {
                when (it.name) {
                    //提示显示
                    "TOAST" -> showToast(it.actionDetailResp.type, it.actionDetailResp.message)
                    // 图片查看
                    "WMS_OPENIMAGEVIEWER" -> {
//                            it.actionDetailResp.uri
//                            it.actionDetailResp.name
                    }

                    else -> Unit
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
            mUIChangeLiveData.getShowToastViewEvent().postValue(message)
        }
    }
}
