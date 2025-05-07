package com.unionware.wms.viewmodel

import androidx.lifecycle.LiveData
import unionware.base.app.viewmodel.SimpleBaseViewModel

abstract class CommonListViewModel : SimpleBaseViewModel() {

    var mNextPageUrl: String? = null

    fun <M> getListData(firstPage: Boolean): LiveData<List<M>> = liveDataEx {
        if (mNextPageUrl == null && !firstPage) {
            mutableListOf()
        } else {
            if (firstPage) {
                getRefreshList()
            } else {
                getLoadMoreList()
            }
        }
    }

    abstract suspend fun <M> getRefreshList(): List<M>

    abstract suspend fun <M> getLoadMoreList(): List<M>

}