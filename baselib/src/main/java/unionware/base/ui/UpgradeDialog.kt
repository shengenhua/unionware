package unionware.base.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.lxj.xpopup.core.CenterPopupView
import com.tencent.mmkv.MMKV
import com.unionware.lib_base.utils.ext.formatterYMD
import unionware.base.R
import unionware.base.app.model.AppLastest
import unionware.base.databinding.DialogUpgradeBinding

/**
 * Author: sheng
 * Date:2025/3/21
 */
@SuppressLint("ViewConstructor")
class UpgradeDialog(
    context: Context,
    private val appLastest: AppLastest,
    private val confirm: OnBtnListener,
) : CenterPopupView(context) {
    lateinit var binding: DialogUpgradeBinding

    /*override fun getInnerLayoutId(): Int {
        return R.layout.dialog_upgrade
    }*/
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_upgrade
    }

    fun interface OnBtnListener {
        fun onConfirm()
    }

    override fun onCreate() {
        super.onCreate()
        binding = DialogUpgradeBinding.bind(findViewById(R.id.clRoot))
        binding.tvTitle.text = "新版本:${appLastest.name}"
        binding.tvVer.text = appLastest.version
        appLastest.remark?.apply {
            binding.tvPrompt.text = this
        }
        if (appLastest.remark?.trim().isNullOrEmpty()) {
            binding.tvVerLine.visibility = View.GONE
        } else {
            binding.tvVerLine.visibility = View.VISIBLE
        }

        if (appLastest.mode == "A") {
            binding.btnCancel.visibility = View.GONE
            binding.btnLine.visibility = View.GONE
        }
        binding.btnCancel.setOnClickListener {
            MMKV.mmkvWithID("app").apply {
                this.encode("CheckUpdateAppTime", System.currentTimeMillis().formatterYMD())
            }
            dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            confirm.onConfirm()
        }
    }
}