package unionware.base.app.ui

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import unionware.base.R

class LoadingMsgView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {
    private val mAnimationDrawable: AnimationDrawable
    private val showMsgView: TextView

    init {
        View.inflate(context, R.layout.view_msg_loading, this)
        val imgLoading = findViewById<ImageView>(R.id.img_trans_loading)
        showMsgView = findViewById(R.id.showMsg)
        mAnimationDrawable = imgLoading.drawable as AnimationDrawable
    }

    fun startLoading() {
        mAnimationDrawable.start()
    }

    fun stopLoading() {
        mAnimationDrawable.stop()
    }

    fun loading(b: Boolean, msg: String = "加载中...") {
        if (b) {
            startLoading()
        } else {
            stopLoading()
        }
        showMsgView.text = msg
    }

}
