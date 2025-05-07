package unionware.base.app.view.base.viewbinding

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import unionware.base.app.view.base.BaseActivity

abstract class BaseBindActivity<DB : ViewDataBinding> : BaseActivity() {

    lateinit var mBind: DB
    override fun initCommonView() {
        super.initCommonView()
        mBind = DataBindingUtil.setContentView(this, onBindLayout())
    }


    override fun onDestroy() {
        super.onDestroy()
        if (::mBind.isInitialized) {
            mBind.unbind()
        }
    }
}