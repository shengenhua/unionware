package unionware.base.app.utils

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import kotlinx.coroutines.launch

class XPopupLoadingUtil {

    private var loading: LoadingPopupView? = null
    private var context: ComponentActivity? = null

    fun dismiss() {
        if (loading?.isDismiss == false) {
            loading?.dismiss()
        }
    }

    fun show(title: String? = "加载中...") {
        if (loading == null) {
            loading = XPopup.Builder(context).dismissOnTouchOutside(false).asLoading()
        }
        context?.lifecycleScope?.launch {
            if (loading?.isShow == false) {
                loading?.setTitle(title)?.show()
            }
        }
    }

    fun init(activity: ComponentActivity) {
        activity.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_DESTROY -> {
                        dismiss()
                        loading = null
                        activity.lifecycle.removeObserver(this)
                    }

                    else -> {}
                }
            }
        })
    }
}