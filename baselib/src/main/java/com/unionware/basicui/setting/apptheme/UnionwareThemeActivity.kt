package com.unionware.basicui.setting.apptheme

import android.content.Intent
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.GridLayoutManager
import com.tencent.mmkv.MMKV
import unionware.base.R
import unionware.base.databinding.ActivityAppThemeBinding
import unionware.base.app.view.base.viewbinding.BaseBindActivity
import unionware.base.model.local.UnionwareTheme

/**
 * Author: sheng
 * Date:2025/4/22
 */
class UnionwareThemeActivity : BaseBindActivity<ActivityAppThemeBinding>() {
    override fun onBindLayout(): Int = R.layout.activity_app_theme
    private val unionwareThemeAdapter = UnionwareThemeAdapter().apply {
        setOnItemClickListener { adapter, view, position ->
            val item = getItem(position)
            item?.apply {
                val kv = MMKV.mmkvWithID("app")
                if (themeStyle == -1) {
                    kv.remove("unionwareTheme")
                } else {
                    kv.encode("unionwareTheme", themeStyle)
                }
                baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
                    ?.apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(this)
                    }
            }
        }
    }

    override fun initView() {
        mBind.apply {
            layoutToolbar.tbTitle.text = "主题"
            layoutToolbar.toolbar.setNavigationOnClickListener { finish() }
            themeRecyclerView.apply {
                layoutManager = GridLayoutManager(this@UnionwareThemeActivity, 3)
                adapter = unionwareThemeAdapter
            }
        }


        // 在某个Activity中
        /*Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }

    override fun initData() {
        unionwareThemeAdapter.submitList(UnionwareThemeConfig.getThemeList())
    }
}