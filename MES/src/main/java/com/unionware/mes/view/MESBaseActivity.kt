package com.unionware.mes.view

import android.content.res.Configuration
import androidx.activity.OnBackPressedCallback
import androidx.databinding.ViewDataBinding
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.lxj.xpopup.XPopup
import unionware.base.app.utils.sound.SoundUtils
import unionware.base.app.view.base.BaseMvvmToolbarActivity
import unionware.base.app.viewmodel.BaseViewModel


abstract class MESBaseActivity<V : ViewDataBinding, VM : BaseViewModel> :
    BaseMvvmToolbarActivity<V, VM>() {
    /**
     * 场景码
     */
    @JvmField
    @Autowired(name = "scene")
    var scene: String = ""

    /**
     * 标题
     */
    @JvmField
    @Autowired(name = "title")
    var title: String = ""
    override fun initBaseViewObservable() {
        super.initBaseViewObservable()
        mViewModel.mUIChangeLiveData.getTTSEvent().observe(this) { SoundUtils.playVoice(this, it) }
        mViewModel.mUIChangeLiveData.getTTSSucOrFailEvent()
            .observe(this) { SoundUtils.playVoice(this, it) }
    }

    override fun initViewObservable() = Unit
    override fun onBindVariableId(): MutableList<Pair<Int, Any>> = mutableListOf()

    override fun initCommonView() {
        super.initCommonView()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                interceptBackPressed()
            }
        })
    }

    protected fun isLandscape() =
        resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    protected open fun backShowDialog() = false

    protected fun interceptBackPressed() {
        if (backShowDialog()) {
            XPopup.Builder(this@MESBaseActivity).asConfirm("提示", "存在已扫码的数据是否退出？") {
                mViewModel.postFinishActivityEvent()
            }.show()
        } else {
            mViewModel.postFinishActivityEvent()
        }
    }

    override fun itemSelectedHome() {
        interceptBackPressed()
    }
}