package com.unionware.emes.view.process.base

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.lxj.xpopup.XPopup
import com.unionware.emes.EMesInitializer
import com.unionware.emes.view.dialog.ProcessProgPop
import com.unionware.emes.view.process.AgingShelfProActivity
import com.unionware.emes.view.process.BadnessProActivity
import com.unionware.emes.view.process.CalibrateProActivity
import com.unionware.emes.view.process.CalibrationProActivity
import com.unionware.emes.view.process.CastingCuringProActivity
import com.unionware.emes.view.process.CollectProcessActivity
import com.unionware.emes.view.process.FinalInspectProActivity
import com.unionware.emes.view.process.FirstInspectProActivity
import com.unionware.emes.view.process.LabourWeldProActivity
import com.unionware.emes.view.process.PackProcessActivity
import com.unionware.emes.view.process.ReadyProcessActivity
import com.unionware.emes.view.process.StandardProActivity
import com.unionware.emes.view.process.WeldProActivity
import com.unionware.emes.viewmodel.process.ProcessDetailsVM
import com.unionware.mes.MESPath
import com.unionware.mes.view.basics.MESDetailsActivity
import com.unionware.virtual.view.adapter.ButtonAdapter
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.api.util.ConvertUtils
import unionware.base.ext.bigDecimalToZeros
import unionware.base.ext.showToast
import unionware.base.model.bean.CommonListBean

/**
 * Author: sheng
 * Date:2024/12/3
 */

@AndroidEntryPoint
@Route(path = EMesInitializer.PATH_MES_CORPUSCLE_DETAILS)
class ProcessDetailsActivity : MESDetailsActivity<ProcessDetailsVM>() {
    private var processProgPop: ProcessProgPop? = null

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.updateJobScheduleLive.observe(this) {
            processProgPop?.dismiss()
            "进度提交成功".showToast()
        }
        mViewModel.dataLiveData.observe(this) {
            val propId = it["propId"]?.bigDecimalToZeros()

            otherPath(propId)?.apply {//判断是否是 emes 的功能
                addButton(ButtonAdapter.AdapterButtonValue(1, "进度确认")) {
                    processProgPop?.dismiss()
                    processProgPop = ProcessProgPop(
                        this@ProcessDetailsActivity, mutableListOf<CommonListBean>().apply {
                            mViewModel.dataLiveData.value?.filter { map ->
                                map.key == "code" || map.key == "jobName" || map.key == "taskNo"
                            }?.also { data ->
                                this.addAll(
                                    ConvertUtils.convertMapToList(
                                        mViewModel.viewBeanLive.value, data
                                    )
                                )
                            }
                        }).also { pop ->
                        pop.confirmListener = { progress ->
                            mViewModel.reportProgress(
                                mutableMapOf(
                                    "progress" to progress, Pair(
                                        "taskId",
                                        mViewModel.dataLiveData.value?.get("id")
                                            ?.bigDecimalToZeros()?.toInt() ?: -1
                                    )
                                )
                            )
                        }
                        XPopup.Builder(mContext).maxWidth(2000).dismissOnBackPressed(false)
                            .dismissOnTouchOutside(false).asCustom(pop).show()
                    }
                }
                when (propId) {
                    Process.FIRST_INSPECT -> Unit
                    Process.FINAL_INSPECT -> {
                        revampButtonText(0, "扫码送检")
                    }

                    else -> {
                        openBadness()
                    }
                }
            }
        }
    }

    override fun dataObserve(map: Map<String, Any>) {
        val propId = map["propId"]?.bigDecimalToZeros()
        if (otherPath(propId) != null) {
            buttonAdapter?.submitList(emptyList())
            addButton(ButtonAdapter.AdapterButtonValue(0, "汇报")) {
                openProcess()
            }
        } else {
            super.dataObserve(map)
        }
    }

    override fun openARouter(propId: String?, path: String): Boolean {
        return super.openARouter(propId, path)
    }

    private fun openBadness() {
        addButton(ButtonAdapter.AdapterButtonValue(10, "不良返修")) {//Badness
            mViewModel.dataLiveData.value?.also {
                val intent = Intent(this, BadnessProActivity::class.java).apply {
                    putExtra("code", it["code"].toString())
                    putExtra("taskId", it["id"]?.bigDecimalToZeros())
                    putExtra("jobId", it["jobId"]?.bigDecimalToZeros())
                    putExtra("title", "不良返修")
                    putExtra("scene", scene)
                }
                startActivity(intent)
            }
        }
    }

    override fun otherPath(propId: String?): Any? {
        return when (propId) {
            Process.STANDARD -> StandardProActivity::class.java
            Process.CASTING_CURING -> CastingCuringProActivity::class.java
            Process.AGING_SHELF -> AgingShelfProActivity::class.java
            Process.COLLECT -> CollectProcessActivity::class.java
            Process.WELD -> WeldProActivity::class.java
            Process.LABOUR_WELD -> LabourWeldProActivity::class.java
            Process.READY -> ReadyProcessActivity::class.java
            Process.CALIBRATION -> CalibrationProActivity::class.java
            Process.FIRST_INSPECT -> FirstInspectProActivity::class.java
            Process.FINAL_INSPECT -> FinalInspectProActivity::class.java
            Process.CALIBRATE -> CalibrateProActivity::class.java
            Process.PACK_JOB -> PackProcessActivity::class.java
            "1000" -> MESPath.openPath(MESPath.PathTag.ASSEMBLE)
            else -> null
        }
    }


    class Process {
        companion object {

            /**
             * 标准
             */
            const val STANDARD = "3000"

            /**
             * 浇封固化
             */
            const val CASTING_CURING = "3001"

            /**
             * 老化上架
             */
            const val AGING_SHELF = "3002"

            /**
             * 标准
             */
            const val COLLECT = "3003"

            /**
             * 焊接
             */
            const val WELD = "3004"

            /**
             * 检查准备
             */
            const val READY = "3005"

            /**
             * 标定
             */
            const val CALIBRATION = "3006"

            /**
             * 首检
             */
            const val FIRST_INSPECT = "3007"

            /**
             * 终检
             */
            const val FINAL_INSPECT = "3008"

            /**
             * 校准
             */
            const val CALIBRATE = "3009"

            /**
             * 人工焊
             */
            const val LABOUR_WELD = "3015"

            /**
             * 包装
             */
            const val PACK_JOB = "3016"
        }
    }
}