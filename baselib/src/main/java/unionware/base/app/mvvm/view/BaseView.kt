package com.unionware.base.lib_base.mvvm.view

/**
 * 视图层核接口
 */
interface BaseView : ILoadView, INoDataView, ITransView, INetErrView {
    fun initListener()
    fun initData()
    fun finishActivity()
}
