package com.unionware.mes.adapter

import androidx.databinding.ViewDataBinding
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder

abstract class BaseQueryAdapter<T : Any, DB : ViewDataBinding> :
    BaseQuickAdapter<T, DataBindingHolder<DB>>() {

}