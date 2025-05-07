package com.unionware.mes.view.basics

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.unionware.basicui.base.activity.BaseDetailsActivity
import com.unionware.mes.MESPath
import com.unionware.mes.adapter.JobMenuBtnAdapter
import com.unionware.basicui.base.viewmodel.BaseDetailsViewModel
import com.unionware.virtual.view.adapter.ButtonAdapter
import unionware.base.ext.bigDecimalToZeros
import unionware.base.ext.showToast


/**
 * 详情数据界面
 */
open class MESDetailsActivity<VM : BaseDetailsViewModel> : BaseDetailsActivity<VM>() {
    @JvmField
    @Autowired(name = "appId")
    var appId: String = ""

    /**
     * 动态 子工序
     */
    private var jobItemBtnAdapter: JobMenuBtnAdapter = JobMenuBtnAdapter(this@MESDetailsActivity)

    override fun initView() {
        super.initView()
    }

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            dyConfigLiveData.observe(this@MESDetailsActivity) {
                if (it?.params?.get("isDynamicReport") == true) {
                    it.items?.also { jobs ->
                        if (jobs.isNotEmpty()) {
                            binding?.rvDetailsTail?.visibility = View.VISIBLE
                            jobItemBtnAdapter.jobs = jobs
                        }
                    }
                    mViewModel.dataLiveData.value?.also {
                        val propId = it["propId"]?.bigDecimalToZeros()
                        when (propId) {
                            "1000" -> {
//                            revampButtonText(0, "${it["jobName"]} 装配")
                            }

                            "1010" -> {
//                            revampButtonText(0, "${it["jobName"]} 采集")
                            }

                            else -> {
                                addButton(
                                    ButtonAdapter.AdapterButtonValue(
                                        0,
                                        "${it["jobName"]} 完工汇报"
                                    )
                                ) {
                                    openProcess()
                                }
                            }
                        }
                    }
                } else {
                    addButton(ButtonAdapter.AdapterButtonValue(0, "汇报")) {
                        openProcess()
                    }
                }
            }
            dyConfigItemLiveData.observe(this@MESDetailsActivity) { config ->
                dataLiveData.value?.also {
                    ARouter.getInstance().build(MESPath.openPath(MESPath.PathTag.DYNAMIC))
                        .withSerializable("id", appId).withSerializable("scene", scene)
                        .withSerializable("code", it["code"]?.toString() ?: "")
                        .withSerializable("propId", it["propId"]?.bigDecimalToZeros() ?: "")
                        .withSerializable("title", config?.name)
                        .withSerializable("jobId", config?.id?.bigDecimalToZeros())
                        .withSerializable("taskId", it["id"]?.bigDecimalToZeros()).withSerializable(
                            "reportRuleId", config?.params?.get("reportRuleId").toString()
                        ).navigation()
                }
            }
            dataLiveData.observe(this@MESDetailsActivity) {
                dataObserve(it)
            }
        }
    }

    protected open fun dataObserve(map: Map<String, Any>) {
        buttonAdapter?.submitList(emptyList())
        mViewModel.getDynamicConfig(//
            scene, filtersReq = unionware.base.model.req.FiltersReq(
                mutableMapOf(
                    "primaryId" to (map["jobId"].bigDecimalToZeros()), "taskId" to (id as Any)
                )
            )
        )
    }


    protected open fun openProcess() {
        mViewModel.dataLiveData.value?.also {
            val propId = it["propId"]?.bigDecimalToZeros()
            processPath(propId)?.apply {
                when (this) {
                    is Class<*> -> {
                        if (openActivity(propId, this)) return@apply
                        Intent(this@MESDetailsActivity, this).apply {
                            putExtra("code", it["code"]?.toString() ?: "")
                            putExtra("taskId", it["id"]?.bigDecimalToZeros())
                            putExtra("jobId", it["jobId"]?.bigDecimalToZeros())
                            putExtra("propId", it["propId"]?.bigDecimalToZeros() ?: "")
                            putExtra("title", it["jobName"]?.toString())
                            putExtra("paramId", it["paramId"]?.toString())
                            putExtra("FSubJobs", Gson().toJson(it["FSubJobs"]))
                            putExtra("scene", scene)
                            //跳转
                            startActivity(this)
                        }
                    }

                    is String -> {
                        if (openARouter(propId, this)) return@apply
                        ARouter.getInstance().build(this).withSerializable("id", appId)
                            .withSerializable("scene", scene)
                            .withSerializable("jobId", it["jobId"]?.bigDecimalToZeros())
                            .withSerializable("title", it["jobName"]?.toString())
                            .withSerializable("propId", it["propId"]?.bigDecimalToZeros() ?: "")
                            .withSerializable("code", it["code"]?.toString() ?: "")
                            .withSerializable("taskId", it["id"]?.bigDecimalToZeros())
                            .withSerializable("billFormId", "UNW_XMES_MPS_TASK")
                            .withSerializable("billId", it["id"]?.bigDecimalToZeros())
                            .withSerializable(
                                "reportRuleId",
                                mViewModel.dyConfigLiveData.value?.params?.get("reportRuleId")
                                    .toString()
                            ).navigation()
                    }
                }
            }
        }
    }

    open fun openActivity(propId: String?, aClass: Class<*>) = false
    open fun openARouter(propId: String?, path: String) = false

    private fun processPath(propId: String?): Any? {
        return when {
            propId == "1000" -> MESPath.openPath(MESPath.PathTag.ASSEMBLE)
            propId == "1010" -> MESPath.openPath(MESPath.PathTag.COLLECT)
            mViewModel.dyConfigLiveData.value?.params?.get("isDynamicReport") == true -> {
                MESPath.openPath(MESPath.PathTag.DYNAMIC)
            }

            otherPath(propId) != null -> {
                otherPath(propId) ?: { "敬请期待".showToast() }
            }

            else -> {
                "敬请期待".showToast()
            }
        }
    }

    open fun otherPath(propId: String?): Any? = null

    override fun tailAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        return jobItemBtnAdapter.apply {
            /*it.addOnItemChildClickListener(R.id.acBtn) { adapter, _, position ->
                mViewModel.getItemDyConfig(
                    scene, filtersReq = FiltersReq(
                        mutableMapOf(
                            "primaryId" to (adapter.items[position].id as Any),
                            "taskId" to (id as Any)
                        )
                    )
                )
            }*/
            setOnJobItemClickListener {
                mViewModel.getItemDyConfig(
                    scene, filtersReq = unionware.base.model.req.FiltersReq(
                        mutableMapOf("primaryId" to (it.id), "taskId" to (id as Any))
                    )
                )
            }
        }
    }
}