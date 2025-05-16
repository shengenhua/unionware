package com.unionware.basicui.setting.apptheme

import androidx.core.graphics.toColorInt
import unionware.base.R
import unionware.base.model.local.UnionwareTextTheme
import unionware.base.model.local.UnionwareTheme

/**
 * Author: sheng
 * Date:2025/5/15
 */
class UnionwareThemeConfig {
    companion object {

        private val themeList = mutableListOf<UnionwareTheme>()
        private val textThemeList = mutableListOf<UnionwareTextTheme>()

        init {
            themeList.addAll(
                arrayListOf(
                    UnionwareTheme(-1, "默认主题", "#3370FF".toColorInt()),
                    UnionwareTheme(R.style.UnionwareRed, "红色主题", "#E53935".toColorInt()),
                    UnionwareTheme(R.style.UnionwareAzure, "天青主题", "#00BBFF".toColorInt()),
                    UnionwareTheme(R.style.UnionwareGreen, "绿色主题", "#308033".toColorInt()),
            ))
            textThemeList.addAll(
                arrayListOf(
                    UnionwareTextTheme(R.style.Default_TextSize_Small, "小"),
                    UnionwareTextTheme(R.style.Default_TextSize_Medium, "标准"),
                    UnionwareTextTheme(R.style.Default_TextSize_Big, "大"),
                    UnionwareTextTheme(R.style.Default_TextSize_Huge, "极大"),
                    UnionwareTextTheme(R.style.Default_TextSize_Gigantic, "巨大"),
                )
            )
        }

        @JvmStatic
        fun getThemeList(): List<UnionwareTheme> {
            return themeList
        }

        @JvmStatic
        fun addUnionwareTheme(vararg beans: UnionwareTheme) {
            beans.forEach {
                val oldBean = themeList.firstOrNull { bean -> bean.themeName == it.themeName }
                if (oldBean != null) {
                    oldBean.apply {
                        themeStyle = it.themeStyle
                        themeName = it.themeName
                        themeColor = it.themeColor
                    }
                } else {
                    themeList.add(it)
                }
            }
        }

        @JvmStatic
        fun getTextThemeList(): List<UnionwareTextTheme> {
            return textThemeList
        }

        @JvmStatic
        fun addUnionwareTextTheme(vararg beans: UnionwareTextTheme) {
            beans.forEach {
                val oldBean = textThemeList.firstOrNull { bean -> bean.themeName == it.themeName }
                if (oldBean != null) {
                    oldBean.apply {
                        themeStyle = it.themeStyle
                        themeName = it.themeName
                    }
                } else {
                    textThemeList.add(it)
                }
            }
        }

    }
}