package unionware.base.app.view.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import unionware.base.app.viewmodel.ErrorState
import unionware.base.app.viewmodel.LoadState
import unionware.base.app.viewmodel.SimpleBaseViewModel
import unionware.base.app.viewmodel.SuccessState
import unionware.base.app.utils.ToastUtil.showToast
import java.lang.reflect.ParameterizedType

abstract class BaseMVActivity<VM : SimpleBaseViewModel> : AppCompatActivity() {

    abstract val getLayoutRes: Int
    lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        initWindow()
        super.onCreate(savedInstanceState)
        setLayout()
        initView()
        initViewModel()
        initData()
        initEvent()
    }

    open fun setLayout() {
        setContentView(getLayoutRes)
    }

    @Suppress("UNCHECKED_CAST")
    private fun initViewModel() {
        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
        mViewModel = ViewModelProvider(this)[parameterizedType.actualTypeArguments[0] as Class<VM>]
        mViewModel.mStateLiveData.observe(this) { state ->
            when (state) {
                LoadState -> {
                    showLoading()
                }
                SuccessState -> {
                    hideLoading()
                }
                is ErrorState -> {
                    hideLoading()
                    state.errorMsg?.let { showToast(it) }
                    handlerError()
                }
                else -> {}
            }
        }
    }

    //扩展liveData的observe函数
    protected fun <T : Any> LiveData<T>.observerKt(block: (T) -> Unit) {
        this.observe(this@BaseMVActivity) {
            block(it)
        }
    }

    open fun showLoading() {

    }

    open fun hideLoading() {

    }

    open fun handlerError() {

    }

    open fun initWindow() {

    }

    abstract fun initView()

    abstract fun initData()

    abstract fun initEvent()

}