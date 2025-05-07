package com.unionware.wms.ui.activity

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.unionware.wms.R
import com.unionware.wms.databinding.ActivityWmsBoxSplitBinding
import com.unionware.wms.inter.baseview.ViewViewModel
import unionware.base.model.req.FiltersReq
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WMSBoxSplitActivity : BaseViewDataActivity<ActivityWmsBoxSplitBinding, ViewViewModel>() {

    @JvmField
    @Autowired(name = "primaryId")
    var primaryId: String? = null

    @JvmField
    @Autowired(name = "scene")
    var scene: String? = null

    @JvmField
    @Autowired(name = "title")
    var title: String? = null

    @JvmField
    @Autowired(name = "name")
    var name: String? = null

    @JvmField
    @Autowired(name = "formId")
    var formId: String? = null

    override fun onBindVariableId(): MutableList<Pair<Int, Any>> {
        return ArrayList()
    }

    override fun initViewObservable() {

    }

    override fun onBindLayout(): Int {
        return R.layout.activity_wms_box_split
    }

    override fun initView() {
        val params: MutableMap<String, Any> = HashMap()
        params["schemaId"] = this.primaryId!!
        val req = FiltersReq(params)
        req.pageIndex = 1
        req.setIndex(1)
        mViewModel.getBoxStateId(scene, name, req)
    }

    override fun initData() {
    }
}