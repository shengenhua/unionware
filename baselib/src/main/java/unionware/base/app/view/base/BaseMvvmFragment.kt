package unionware.base.app.view.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import unionware.base.app.utils.ReflectUtils
import unionware.base.ext.showToast
import unionware.base.app.viewmodel.BaseViewModel

abstract class BaseMvvmFragment<VM : BaseViewModel> : BaseFragment() {
    protected val mViewModel: VM by lazy { ViewModelProvider(this)[onBindViewModel()] }

    override fun initCommonView(view: View) {
        super.initCommonView(view)
        initBaseViewObservable()
        initViewObservable()
    }

    /**
     * 绑定 ViewModel
     */
    @Suppress("UNCHECKED_CAST")
    open fun onBindViewModel(): Class<VM> {
        return ReflectUtils.getActualTypeArgument(0, this.javaClass) as? Class<VM>
            ?: throw IllegalArgumentException("找不到 ViewModelClass 实例，建议重写该方法")
    }

    /**
     * 放置 观察者对象
     */
    abstract fun initViewObservable()


    /**
     * 初始化页面观察 变更相应的展示
     */
    protected open fun initBaseViewObservable() {
        // 将 Frament 的生命周期同步到 ViewModel 中
        lifecycle.addObserver(mViewModel)

        mViewModel.mUIChangeLiveData.getShowToastViewEvent()
            .observe(this) { it.showToast() }
        mViewModel.mUIChangeLiveData.getShowInitLoadViewEvent()
            .observe(this) {
                showInitLoadView(it)
            }
        mViewModel.mUIChangeLiveData.getShowTransLoadingViewEvent()
            .observe(this) {
                showTransLoadingView(it)
            }
        mViewModel.mUIChangeLiveData.getShowNoDataViewEvent()
            .observe(this) {
                showNoDataView(it)
            }
        mViewModel.mUIChangeLiveData.getShowNetWorkErrViewEvent()
            .observe(this) {
                showNetWorkErrView(it)
            }
        mViewModel.mUIChangeLiveData.getStartActivityEvent()
            .observe(this) {
                val clz =
                    it[BaseViewModel.ParameterField.CLASS] as Class<*>?
                val bundle = it[BaseViewModel.ParameterField.BUNDLE] as Bundle?
                startActivity(clz, bundle)
            }
        mViewModel.mUIChangeLiveData.getFinishActivityEvent()
            .observe(this) { mActivity?.finish() }
        mViewModel.mUIChangeLiveData.getOnBackPressedEvent()
            .observe(this) {
                @Suppress("DEPRECATION")
                mActivity?.onBackPressed()
            }
    }

}
