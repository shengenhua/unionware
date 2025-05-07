package unionware.base.app.view.base.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import unionware.base.app.view.base.viewbinding.IViewBindingHolder

interface FragmentBindingHolder<T : ViewDataBinding> : IViewBindingHolder<T> {

    fun inflateBinding(
        inflater: LayoutInflater,
        root: ViewGroup?,
        attachToRoot: Boolean,
        block: (binding: T) -> Unit
    ): View

    fun Fragment.inflateBinding(
        inflater: LayoutInflater,
        root: ViewGroup?,
        attachToRoot: Boolean = false,
        onClear: ((binding: T) -> Unit)? = null,
        init: (binding: T) -> Unit
    ): View

    fun inflateBinding(inflated: View, init: (binding: T) -> Unit)

    fun Fragment.inflateBinding(
        viewStub: ViewStub? = null,
        onClear: ((binding: T) -> Unit)? = null,
        init: (binding: T) -> Unit
    )
}