package com.unionware.wms.ui.activity

import androidx.databinding.ViewDataBinding
import unionware.base.app.view.base.BaseMvvmDataBindingActivity
import unionware.base.app.viewmodel.BaseViewModel


abstract class BaseViewDataActivity<V : ViewDataBinding, VM : BaseViewModel> : BaseMvvmDataBindingActivity<V, VM>() {


}