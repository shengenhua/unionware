package com.unionware.basicui.setting.apptheme

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tencent.mmkv.MMKV
import com.unionware.basicui.setting.apptheme.TextStyleAdapter.TextStyle
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
//            recreate()
            textStyleAdapter.notifyItemChanged(0, textStyleAdapter.itemCount)
        }
    }
    private val textStyleAdapter = TextStyleAdapter().apply {
        this.submitList(
            arrayListOf(
                TextStyle(R.attr.font10, "文本 10"),
                TextStyle(R.attr.font12, "文本 12"),
                TextStyle(R.attr.font14, "文本 14"),
                TextStyle(R.attr.font16, "文本 16"),
                TextStyle(R.attr.font18, "文本 18"),
                TextStyle(R.attr.font20, "文本 20"),
//            TextStyle(R.attr.font22, "文本 22"),
                TextStyle(R.attr.font24, "文本 24"),
                TextStyle(R.attr.font26, "文本 26"),
                TextStyle(R.attr.font28, "文本 28"),
                TextStyle(R.attr.font30, "文本 30"),
                TextStyle(R.attr.font32, "文本 32"),
                TextStyle(R.attr.font34, "文本 34"),
//            TextStyle(R.attr.font36, "文本 36"),
                TextStyle(R.attr.font38, "文本 38"),
                TextStyle(R.attr.font40, "文本 40"),
            )
        )
    }

    override fun initView() {
        mmkv.decodeInt("unionwareTextSize", R.style.Default_TextSize_Medium).also {
            mmkv.encode("themeText", it)
        }
        mBind.apply {
            layoutToolbar.tbTitle.text = "字体设置"
            layoutToolbar.toolbar.setNavigationOnClickListener { finish() }
            themeRecyclerView.apply {
                layoutManager = GridLayoutManager(this@ThemeTextActivity, 4)
                adapter = unionwareThemeAdapter
            }
            textShowRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@ThemeTextActivity)
                adapter = textStyleAdapter
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

    override fun initData() {
    }
}