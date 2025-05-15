package com.unionware.basicui.setting.apptheme

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.tencent.mmkv.MMKV
import unionware.base.R
import unionware.base.app.view.base.viewbinding.BaseBindActivity
import unionware.base.databinding.ActivityThemeTextBinding

class ThemeTextActivity : BaseBindActivity<ActivityThemeTextBinding>() {
    override fun onBindLayout(): Int = R.layout.activity_theme_text
    private var mmkv = MMKV.mmkvWithID("app")

    private val unionwareThemeAdapter = UnionwareTextThemeAdapter().apply {
        this.submitList(UnionwareThemeConfig.getTextThemeList())
        setOnItemClickListener { adapter, view, position ->
            val item = getItem(position)
            mmkv.encode("themeText", item?.themeStyle ?: R.style.Default_TextSize_Medium)
            recreate()
        }
    }

    override fun getInitTheme(): Int? {
        return super.getInitTheme()
    }

    override fun initThemeTextSize() {
        mmkv.decodeInt("unionwareTextSize", R.style.Default_TextSize_Medium)
            .apply {
                if (mmkv.decodeInt("themeText", -1) == -1) {
                    mmkv.encode("themeText", this)
                }
            }
        mmkv.decodeInt("themeText", R.style.Default_TextSize_Medium)
            .apply {
                theme.applyStyle(this, true)
            }
    }

    override fun initView() {
        mBind.apply {
            layoutToolbar.tbTitle.text = "字体设置"
            layoutToolbar.toolbar.setNavigationOnClickListener { finish() }
            themeRecyclerView.apply {
                layoutManager = GridLayoutManager(this@ThemeTextActivity, 4)
                adapter = unionwareThemeAdapter
            }
            btSave.setOnClickListener {
                mmkv.encode(
                    "unionwareTextSize",
                    mmkv.decodeInt("themeText", R.style.Default_TextSize_Medium)
                )
                baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
                    ?.apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(this)
                    }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mmkv.encode("themeText", -1)
    }

    override fun initData() {
    }
}