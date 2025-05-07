package com.unionware.base.lib_common.model.resp

import java.io.Serializable

class ListDataViewResp<T, TT> : Serializable {
    val data: T? = null
    val view: List<TT>? = null
    val pageId: String? = null
}