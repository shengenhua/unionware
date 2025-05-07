package unionware.base.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoadingUtil {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var loading: LoadingPopupView? = null

        @JvmStatic
        fun dismiss() {
            if (loading?.isDismiss == false) {
                loading?.dismiss()
            }
        }

        @JvmStatic
        fun show(title: String? = "加载中...") {
            MainScope().launch {
                if (loading?.isShow == false) {
                    loading?.setTitle(title)?.show()
                }
            }
        }

        @JvmStatic
        fun init(activity: Activity) {
            loading =
                XPopup.Builder(activity)
                    .dismissOnBackPressed(false)
                    .dismissOnTouchOutside(false)
                    .asLoading()
        }

        @JvmStatic
        fun unInit() {
            loading = null
        }
    }

}