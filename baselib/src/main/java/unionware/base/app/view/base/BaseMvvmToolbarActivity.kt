package unionware.base.app.view.base

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.databinding.ViewDataBinding
import com.lxj.xpopup.XPopup
import unionware.base.R
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.ext.getColorPrimary
import unionware.base.ext.showToast

abstract class BaseMvvmToolbarActivity<V : ViewDataBinding, VM : BaseViewModel> :
    BaseMvvmDataBindingActivity<V, VM>() {
    override fun onBindToolbarLayout(): Int {
        return R.layout.base_toolbar
    }

    override fun initCommonView() {
        super.initCommonView()
        //防止在initView 里面调用接口时候 lifecycle 为null
        mViewModel.lifecycle = this
    }

    override fun initToolbar(view: View) {
        mToolbar = view.findViewById<Toolbar>(R.id.toolbar).apply {
            setSupportActionBar(this)
            //是否显示标题
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            setNavigationOnClickListener {
                itemSelectedHome()
//                this@BaseMvvmToolbarActivity.onBackPressedDispatcher.onBackPressed()
            }
//            view.findViewById<TextView>(com.unionware.base.ftmes.R.id.title_bar).text = "标题"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = theme.getColorPrimary()
//            resources.getColor(androidx.transition.R.attr.colorPrimary, theme)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (enableToolBarMenu()) {
            menuInflater.inflate(R.menu.mes_menu, menu)
            menu?.forEach {
                showItemMenu(it)
                val spannableString = SpannableString(it.title)
                spannableString.setSpan(
                    ForegroundColorSpan(Color.WHITE),
                    0,
                    spannableString.length,
                    0
                )
                it.setTitle(spannableString)
            }
        }
        return true
    }


    /**
     * 控制某个 menu 的显示 或者 文字修改
     */
    open fun showItemMenu(menuItem: MenuItem) = Unit

    open fun enableToolBarMenu(): Boolean = false

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                itemSelectedHome()
                true
            }

            R.id.action_submit -> {
                if (mViewModel.mUIChangeLiveData.getShowTransLoadingViewEvent().value == true) {
                    "操作中，请稍等".showToast()
                    return true
                }
                onActionSubmit()
                true
            }

            R.id.action_other -> {
                if (mViewModel.mUIChangeLiveData.getShowTransLoadingViewEvent().value == true) {
                    "操作中，请稍等".showToast()
                    return true
                }
                onActionOther()
                true
            }

            R.id.action_other_2 -> {
                if (mViewModel.mUIChangeLiveData.getShowTransLoadingViewEvent().value == true) {
                    "操作中，请稍等".showToast()
                    return true
                }
                onActionOther2()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    protected open fun itemSelectedHome() {
        finish()
    }

    /**
     * 提交按钮 显示的对话框
     */
    protected open fun onActionSubmit() {
        XPopup.Builder(this).asConfirm("提示", "是否提交？") {
            onActionSubmitConfirm()
        }.show()
    }

    /**
     * 其他按钮
     */
    protected open fun onActionOther2() = Unit

    /**
     * 其他按钮
     */
    protected open fun onActionOther() = Unit

    /**
     * 对话框点击确认
     */
    protected open fun onActionSubmitConfirm() = Unit
}