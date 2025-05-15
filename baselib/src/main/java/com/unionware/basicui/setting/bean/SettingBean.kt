package com.unionware.basicui.setting.bean

import android.app.Activity
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

/**
 * Author: sheng
 * Date:2025/1/3
 */
class SettingBean {
    constructor(name: String?, key: String?, switch: Boolean) {
        this.key = key
        this.switch = switch
        this.name = name
    }

    constructor(name: String?, cls: Class<*>?) {
        this.name = name
        this.cls = cls
    }

    constructor(name: String?, path: String?) {
        this.name = name
        this.path = path
    }

    constructor(name: String?, method: SettingRunnable?) {
        this.name = name
        this.method = method
    }

    constructor(name: String?, isUnionware: Boolean, path: String?) {
        this.name = name
        if (isUnionware) {
            this.uPath = path
        } else {
            this.path = path
        }
    }


    /**
     * 名称
     */
    var name: String? = null

    /**
     * 键名
     */
    var key: String? = null

    /**
     * 类型
     */
    var type: Int = 0

    /**
     * 类型
     */
    @ColorInt
    var textColor: Int = -1

    /**
     * 类型
     * 跳转 = 1
     * 地址 = 2
     * 开关 = 3
     */
    @DrawableRes
    var drawable: Int = -1

    /**
     * 类
     */
    var cls: Class<*>? = null
        set(value) {
            type = 1
            field = value
        }

    /**
     * 地址
     */
    var path: String? = null
        set(value) {
            type = 2
            field = value
        }

    /**
     * 地址
     */
    var uPath: String? = null
        set(value) {
            type = 3
            field = value
        }

    /**
     *  开关
     */
    var switch: Boolean = false
        set(value) {
            type = 4
            field = value
        }

    /**
     * 方法
     */
    var method: SettingRunnable? = null
        set(value) {
            type = 5
            field = value
        }

    fun interface SettingRunnable {
        fun run(activity: Activity)
    }
}