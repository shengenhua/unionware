package com.unionware.once.view.dynamic

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.once.app.RouterOncePath
import com.unionware.once.viewmodel.dynamic.LacalDynamicViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 动态装配
 * Author: sheng
 * Date:2024/11/19
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_MES_LACAL_DYNAMIC)
open class LacalViewDynamicActivity : LacalDynamicActivity<LacalDynamicViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        super.initView()
        /*binding?.apply {
            val viewTreeObserver = root.viewTreeObserver
            viewTreeObserver.addOnGlobalLayoutListener {
                val rect = Rect()
                root.getWindowVisibleDisplayFrame(rect)
                val screenHeight = root.rootView.height
                val keyboardHeight = screenHeight - rect.bottom
                if (keyboardHeight > screenHeight * 0.15) {
                    // 软键盘显示，调整布局
                    // 例如，将布局向上推移 keyboardHeight 像素
                    root.translationY = -keyboardHeight.toFloat()
                } else {
                    // 软键盘隐藏，恢复布局
                    root.translationY = 0f
                }
            }
        }*/
    }
}