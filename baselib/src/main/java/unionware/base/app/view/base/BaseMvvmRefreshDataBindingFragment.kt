package unionware.base.app.view.base

import android.view.View
import androidx.databinding.ViewDataBinding
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.unionware.base.lib_base.mvvm.view.BaseRefreshView
import unionware.base.app.viewmodel.BaseRefreshViewModel

abstract class BaseMvvmRefreshDataBindingFragment<V : ViewDataBinding, VM : BaseRefreshViewModel> :
    BaseMvvmDataBindingFragment<V, VM>(), BaseRefreshView {

    protected lateinit var mRefreshLayout: SmartRefreshLayout
    protected var isRefresh = true

    override fun initCommonView(view: View) {
        super.initCommonView(view)
        initRefreshView(view)
        initBaseViewRefreshObservable()
    }

    protected abstract fun onBindRefreshLayout(): Int

    protected abstract fun enableRefresh(): Boolean

    protected abstract fun enableLoadMore(): Boolean


    /**
     * 初始化刷新组件
     */
    private fun initRefreshView(view: View) {
        // 绑定组件
        mRefreshLayout = view.findViewById(onBindRefreshLayout())
        // 是否开启刷新
        enableRefresh(enableRefresh())
        // 是否开启加载更多
        enableLoadMore(enableLoadMore())

        // 下拉刷新
        mRefreshLayout.setOnRefreshListener {
            isRefresh = true
            onRefreshEvent()
        }
        // 上拉加载
        mRefreshLayout.setOnLoadMoreListener {
            isRefresh = false
            onLoadMoreEvent()
        }
    }

    /**
     * 初始化观察者 ViewModel 层加载完数据的回调通知当前页面事件已完成
     */
    private fun initBaseViewRefreshObservable() {
        mViewModel.mUIChangeRefreshLiveData.autoRefreshLiveEvent.observe(this) {
            autoLoadData()
        }
        mViewModel.mUIChangeRefreshLiveData.stopRefreshLiveEvent.observe(this) {
            stopRefresh(it)
        }
        mViewModel.mUIChangeRefreshLiveData.stopLoadMoreLiveEvent.observe(this) {
            stopLoadMore(it)
        }
        mViewModel.mUIChangeRefreshLiveData.stopLoadMoreWithNoMoreDataEvent.observe(this) {
            stopLoadMoreWithNoMoreData()
        }
    }


    override fun enableRefresh(b: Boolean) {
        mRefreshLayout.setEnableRefresh(b)
    }

    override fun enableLoadMore(b: Boolean) {
        mRefreshLayout.setEnableLoadMore(b)
    }

    override fun enableAutoLoadMore(b: Boolean) {
        mRefreshLayout.setEnableAutoLoadMore(b)
    }

    override fun onAutoLoadEvent() {

    }

    override fun autoLoadData() {
        mRefreshLayout.autoRefresh()
    }

    override fun stopRefresh(boolean: Boolean) {
        mRefreshLayout.finishRefresh(boolean)
    }

    override fun stopLoadMore(boolean: Boolean) {
        mRefreshLayout.finishLoadMore(boolean)
    }

    override fun stopLoadMoreWithNoMoreData() {
        mRefreshLayout.finishLoadMoreWithNoMoreData()
    }

}
