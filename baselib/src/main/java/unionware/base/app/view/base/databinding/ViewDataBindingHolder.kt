package unionware.base.app.view.base.databinding

import androidx.databinding.ViewDataBinding
import unionware.base.app.view.base.viewbinding.IViewBindingHolder

internal class ViewDataBindingHolder<VB : ViewDataBinding> : IViewBindingHolder.Holder<VB>() {

    override fun clearBinding(clear: VB.() -> Unit) {
        super.clearBinding {
            clear()
            unbind()
        }
    }
}