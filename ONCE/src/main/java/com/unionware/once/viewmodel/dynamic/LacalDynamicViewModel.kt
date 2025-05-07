package com.unionware.once.viewmodel.dynamic

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.basicui.base.viewmodel.BaseMESViewModel
import com.unionware.once.api.OnceApi
import com.unionware.virtual.model.ErrorSponsors
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import unionware.base.api.SimulateApi
import unionware.base.app.event.SingleLiveEvent
import unionware.base.model.bean.BarcodeBean
import unionware.base.model.bean.EntryAndSubBean
import unionware.base.model.bean.PropertyBean
import unionware.base.model.bean.barcode.DynamicEntryBean
import unionware.base.model.req.ItemBean
import unionware.base.model.req.ViewReq
import unionware.base.model.resp.AnalysisInfoResp
import unionware.base.model.resp.AnalysisResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import unionware.base.network.response.BaseResponse
import javax.inject.Inject


@HiltViewModel
open class LacalDynamicViewModel @Inject constructor() : BaseMESViewModel() {
    @JvmField
    @Inject
    var api: OnceApi? = null

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
         * 控制当前显示的是单据体还是子单据体
         * true 单据体  false 子单据体
         */
        var entryOrSubLiveData: MutableLiveData<Boolean> = MutableLiveData(true)

        /**
         * 控制当前单据体和子单据体 的显示 以及扫描框显示
         */
        var entryAndSubViewLiveData: MutableLiveData<EntryAndSubBean?> = MutableLiveData()

        /**
         * 当前的 pageId 视图 id
         */
        var pageIdLiveData: SingleLiveEvent<String> = SingleLiveEvent()

        /**
         * 最新的 View 虚拟视图 的数据
         */
        internal var viewLiveData: MutableLiveData<List<PropertyBean>> = MutableLiveData()

        /**
         * 最新的 View 虚拟视图 的数据
         */
        internal var dataLiveData: SingleLiveEvent<AnalysisResp> = SingleLiveEvent()

        /**
         * 提交
         */
        var submitLiveData: SingleLiveEvent<AnalysisInfoResp> = SingleLiveEvent()

        /**
         * 单据头区域字段，构建单据头采集区域，否则隐藏该区域
         */
        val headCollectsLiveData: MutableLiveData<List<PropertyBean>> =
            MutableLiveData<List<PropertyBean>>()

        /**
         * 单据体区域字段,构建单据体采集区域。否则隐藏该区域。
         *
         * 单据体和子单据体数据新增有两种模式，扫码新增或手工新增。
         * 如果其中存在 Key=FBarQty，则构建条码数量框，并固定Key=FBarQtyScan
         * 当对应区域字段中存在 Key=FBarCode则属于扫描新增模式，构建条码扫描框,并固定Key=FBarCodeScan
         * 否则显示新增按钮，属于手工新增模式。
         *
         */
        val entryCollectsLiveData: MutableLiveData<List<DynamicEntryBean>> =
            MutableLiveData<List<DynamicEntryBean>>()

        /**
         * 子单据体区域字段，显示切换子单据体按钮，否则隐藏该按钮
         */
        val subEntryCollectsLiveData: MutableLiveData<List<DynamicEntryBean>> =
            MutableLiveData<List<DynamicEntryBean>>()


        /**
         * 单据体当前选择跟更新的位置
         */
        val entryPositionLiveData: MutableLiveData<Int> = MutableLiveData<Int>()

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
            // 数据更新渲染

            /* 单据头 */
            val headCollects =
                view?.filter { it.entity == "FBillHead" }
                    ?.associateBy { it.key }
            val headData =
                data?.fBillHead?.get(0)?.plus(data.fEntity?.get(0) ?: mutableMapOf())
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

            /**
             * 保存 单据体 和 子单据体 显示信息
             */
            val entryAndSub = EntryAndSubBean()
            entryAndSub.subEntry = EntryAndSubBean.EntryView(false)
            /* 子单据体 */
            arrangeViewData(
                mutableListOf("FEntity"),
                data?.fEntity,
                view
            )?.apply {
                val entry = EntryAndSubBean.EntryView(
                    true,
                )
                data?.fEntity?.filter { it.containsKey("FBarCode") || it.containsKey("FBarQty") }
                    ?.map {
                        entry.hasBarQty = it.containsKey("FBarQty")
                        entry.hasBarCode = it.containsKey("FBarCode")
                    }
                entryAndSub.entry = entry
                entryCollectsLiveData.postValue(this)
            }

            /* 单据体 */
            /*arrangeViewData(mutableListOf("fEntity"), data?.fEntity, view).apply {
                entryCollectsLiveData.postValue(this)
            }*/

            if (entryAndSubViewLiveData.value == null) {
                entryAndSubViewLiveData.value = entryAndSub
            }
            mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
        }

        private fun arrangeViewData(
            entity: MutableList<String>,
            dataEntry: List<Map<String, BarcodeBean>>?,
            view: List<PropertyBean>?,
        ): List<DynamicEntryBean>? {
            //获取单据体的 view
            val entityView = view?.filter { entity.contains(it.entity) }?.associateBy { it.key }
            if (entityView.isNullOrEmpty()) {
                return null
            }

            if (dataEntry.isNullOrEmpty()) {
                return mutableListOf()
            }

            dataEntry.withIndex().map { indexValue ->//多条数据
                val collects = indexValue.value
                DynamicEntryBean().apply {
                    tag = indexValue.index.toString()
                    //获取 需要显示的 data
                    val showCollects = collects.filter { it.value.isVisible }
                    entityView?.filter {
                        showCollects.containsKey(it.key)
                    }?.map { (key, value) ->
                        value.clone().apply {
                            showCollects[key]?.also {
                                this.valueName = it.name
                                this.valueNumber = it.number
                                this.value = when (type) {
                                    "ASSISTANT", "BASEDATA" -> {
                                        it.number
                                    }

                                    else -> {
                                        if (it.value.isNullOrEmpty()) it.name else it.value
                                    }
                                }
                                this.isEnable = it.isEnabled
                            }
                        }
                    }.let {
                        viewList = it
                    }
                }
            }.also {
                return it
            }
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
            NetHelper.request(viewApi?.getSimpleModel(ViewReq().apply {
                pageId = virtualView.pageIdLiveData.value
            }), lifecycle, object : ICallback<AnalysisResp?> {
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
                object : ICallback<List<PropertyBean>?> {
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
        fun updateView(key: String?, value: String?, row: Int = -1) {
            updateVirtualView(ViewReq().apply {
                items = listOf(ItemBean(key, value, row))
            })
        }

        /**
         * 获取作业视图视图模型 数据
         */
        fun updateVirtualView(viewReq: ViewReq) {
            viewReq.pageId = virtualView.pageIdLiveData.value
            request(viewApi?.updateView(viewReq), object : ICallback<AnalysisInfoResp?> {
                override fun onSuccess(data: AnalysisInfoResp?) {
                    data?.apply {
                        virtualView.dataLiveData.value = this.data
                    }
                    viewReq.items?.get(0)?.also {
                        virtualView.entryPositionLiveData.value = it.row
                    }
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
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
            commandViewData(ViewReq(virtualView.pageIdLiveData.value).apply {
                this.command = command
                params = mutableMapOf<String, Any>("entity" to entity, "row" to (row.toString()))
            })
        }

        fun command(command: String, entity: String? = null, row: Int? = null) {
            commandViewData(ViewReq(virtualView.pageIdLiveData.value).apply {
                this.command = command
                params = mutableMapOf<String?, Any?>().apply {
                    entity?.let {
                        this["entity"] = it
                    }
                    row?.let {
                        this["row"] = it.toString()
                    }
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
                "INVOKE_DELETEENTRYROW"/*删除行事件*/ -> {}
                "INVOKE_BARCODECONFIRM"/*条码确认事件*/ -> {}
                "Save", "INVOKE_SUBMIT"/*提交事件*/ -> {
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
                }
            }
            request(viewApi?.commandViewData(viewReq), object : ICallback<AnalysisInfoResp?> {
                override fun onSuccess(data: AnalysisInfoResp?) {
                    data?.apply {
                        virtualView.dataLiveData.value = this.data
                    }
                    when (viewReq.command) {
                        "INVOKE_DELETEENTRYROW"/*删除行事件*/ -> {}
                        "INVOKE_BARCODECONFIRM"/*条码确认事件*/ -> {}
                        "Save", "INVOKE_SUBMIT"/*提交事件*/ -> {
                            mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                            showToast(3, "提交成功")
                            virtualView.submitLiveData.value = data
//                            postFinishActivityEvent()
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
                viewApi?.getSimpleModel(ViewReq(virtualView.pageIdLiveData.value)),
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


        protected open fun onVMFailure(e: ApiException, viewReq: ViewReq) {
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

        open fun request(
            observable: Observable<BaseResponse<Any>>?,
            callback: ICallback<AnalysisInfoResp?>,
        ) {
            NetHelper.request(observable, lifecycle, object : ICallback<Any?> {
                override fun onSuccess(data: Any?) {
                    try {
                        val dataResp = Gson().fromJson<AnalysisInfoResp>(
                            Gson().toJson(data), (object : TypeToken<AnalysisInfoResp?>() {}).type
                        )
                        callback.onSuccess(dataResp)
                        analysisAction(dataResp)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        callback.onSuccess(null)
                    }
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
            })
        }


        fun analysisAction(resp: AnalysisInfoResp?) {
            resp?.apply {
                action.forEach {
                    when (it.name) {
                        //提示显示
                        "TOAST" -> showToast(it.actionDetailResp.type, it.actionDetailResp.message)
                        // 图片查看
                        "WMS_OPENIMAGEVIEWER" -> {
                        }

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
            mUIChangeLiveData.getShowToastViewEvent().postValue(message)
        }
    }
}
