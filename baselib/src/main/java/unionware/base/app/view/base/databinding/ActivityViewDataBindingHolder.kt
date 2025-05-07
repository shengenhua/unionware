package unionware.base.app.view.base.databinding

import android.view.View
import android.view.ViewStub
import androidx.activity.ComponentActivity
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import unionware.base.app.view.base.viewbinding.ObserverWrapper
import java.lang.IllegalArgumentException

class ActivityViewDataBindingHolder<T : ViewDataBinding>(@LayoutRes private val layoutRes: Int = 0) :
    ActivityBindingHolder<T> {

    private val _bindingHolder = ViewDataBindingHolder<T>()

    override fun inflateBinding(inflated: View, init: (binding: T) -> Unit) {
        DataBindingUtil.bind<T>(inflated)
            ?.also { binding ->
                _bindingHolder.bind(binding)
                init(binding)
            }
    }

    override fun inflateBinding(activity: ComponentActivity, init: (binding: T) -> Unit) {
        if (layoutRes == 0) throw IllegalArgumentException("layout file is empty!")
        DataBindingUtil.setContentView<T>(activity, layoutRes)
            .also { binding ->
                _bindingHolder.bind(binding)
                init(binding)
            }
    }

    override fun ComponentActivity.inflateBinding(
        viewStub: ViewStub?,
        onClear: ((binding: T) -> Unit)?,
        init: (binding: T) -> Unit
    ) {
        viewStub?.also {
            it.setOnInflateListener { _, inflated ->
                inflateBinding(inflated, init)
            }
            it.inflate()
        } ?: inflateBinding(this, init)
        ObserverWrapper(this) {
            clearBinding {
                onClear?.invoke(this)
            }
        }.attach()
    }

    override val binding: T? get() = _bindingHolder.binding

    override fun requireBinding(): T = _bindingHolder.requireBinding()

    override fun clearBinding(clear: T.() -> Unit) {
        _bindingHolder.clearBinding(clear)
    }
}

/**
 * Creates the [IViewBindingHolder] for [ComponentActivity]s.
 *
 * Example for use:
 * ```
 * class MyActivity : ComponentActivity(), ActivityBindingHolder<MyActivityBinding> by ActivityBinding(R.layout.my_fragment) {
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         …
 *         // replace setContentView(), and hold binding instance
 *         inflateBinding(/* option: */onClear = { it.onClear() }) { binding ->
 *             // init with binding
 *             …
 *         }
 *         …
 *     }
 *
 *     // Optional: perform clear binding
 *     private fun MyActivityBinding.onClear() {
 *         …
 *     }
 * }
 * ```
 */
@Suppress("FunctionName") // delegate ActivityBindingHolder implements
inline fun <reified T : ViewDataBinding> ActivityBinding(@LayoutRes layoutRes: Int = 0): ActivityBindingHolder<T> =
    ActivityViewDataBindingHolder(layoutRes)