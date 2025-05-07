package unionware.base.app.view.base

import android.view.ViewStub
import androidx.databinding.ViewDataBinding
import unionware.base.app.utils.ReflectUtils
import unionware.base.app.view.base.databinding.ActivityBindingHolder
import unionware.base.app.view.base.databinding.ActivityViewDataBindingHolder
import unionware.base.app.viewmodel.BaseViewModel

abstract class BaseMvvmDataBindingActivity<V : ViewDataBinding, VM : BaseViewModel> :
    BaseMvvmActivity<VM>(), ActivityBindingHolder<V> by ActivityViewDataBindingHolder() {

    override fun initContentView(mViewStubContent: ViewStub) {
        with(mViewStubContent) {
            layoutResource = onBindLayout()
            inflateBinding(viewStub = this) { binding ->
                binding.lifecycleOwner = this@BaseMvvmDataBindingActivity
                onBindVariableId().forEach { pair ->
                    binding.setVariable(pair.first, pair.second)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewModel(): Class<VM> {
        return ReflectUtils.getActualTypeArgument(1, this.javaClass) as? Class<VM>
            ?: throw IllegalArgumentException("找不到 ViewModelClass 实例，建议重写该方法")
    }

    abstract fun onBindVariableId(): MutableList<Pair<Int, Any>>
}
