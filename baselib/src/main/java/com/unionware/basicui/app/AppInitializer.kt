package com.unionware.basicui.app

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {
//        UnionwareApp.init(context as Application)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}