package unionware.base.app.view.base.dialog

import android.content.Context
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.impl.LoadingPopupView
import unionware.base.R
import unionware.base.app.ui.LoadingMsgView

class LoadingPop(
    context: Context
) : LoadingPopupView(context, R.layout.pop_loading) {
    private var mLoadingView: LoadingMsgView? = null
    override fun onCreate() {
        super.onCreate()
        initView()
    }

    private fun initView() {
        mLoadingView = findViewById(R.id.view_msg_loading)
        mLoadingView?.loading(true)
    }


    fun show(msg: String): BasePopupView {
        mLoadingView?.loading(true, msg)
        return super.show()
    }

    override fun show(): BasePopupView {
        mLoadingView?.loading(true)
        return super.show()
    }

    override fun dismiss() {
        mLoadingView?.loading(false)
        super.dismiss()
    }
}