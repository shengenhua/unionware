package unionware.base.app.view.base

import android.view.ViewStub
import androidx.viewbinding.ViewBinding
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.app.utils.ReflectUtils
import unionware.base.app.view.base.viewbinding.ActivityViewBinding
import unionware.base.app.view.base.viewbinding.ActivityViewBindingHolder

@Suppress("UNCHECKED_CAST")
abstract class BaseMvvmViewBindingActivity<V : ViewBinding, VM : BaseViewModel> :
    BaseMvvmActivity<VM>(), ActivityViewBinding<V> by ActivityViewBindingHolder() {

    override fun initContentView(mViewStubContent: ViewStub) {
        with(mViewStubContent) {
            layoutResource = onBindLayout()
            inflateBinding(this, onBindingClass())
        }
    }

    override fun onBindViewModel(): Class<VM> {
        return ReflectUtils.getActualTypeArgument(1, this.javaClass) as? Class<VM>
            ?: throw IllegalArgumentException("找不到 ViewModelClass 实例，建议重写该方法")
    }

    open fun onBindingClass(): Class<V> {
        return ReflectUtils.getActualTypeArgument(0, this.javaClass) as? Class<V>
            ?: throw IllegalArgumentException("找不到 BindingClass 实例，建议重写该方法")
    }
}
