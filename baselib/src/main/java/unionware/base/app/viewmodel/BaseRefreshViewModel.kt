package unionware.base.app.viewmodel

import unionware.base.app.event.SingleLiveEvent

abstract class BaseRefreshViewModel : BaseViewModel() {

    var mUIChangeRefreshLiveData = UIChangeRefreshLiveData()

    inner class UIChangeRefreshLiveData : unionware.base.app.event.SingleLiveEvent<Any>() {
        private var mAutoRefreshLiveEvent: unionware.base.app.event.SingleLiveEvent<Void>? = null
        private var mStopRefreshLiveEvent: unionware.base.app.event.SingleLiveEvent<Boolean>? = null
        private var mStopLoadMoreLiveEvent: unionware.base.app.event.SingleLiveEvent<Boolean>? = null
        private var mStopLoadMoreWithNoMoreDataLiveEvent: unionware.base.app.event.SingleLiveEvent<Void>? = null

        val autoRefreshLiveEvent: unionware.base.app.event.SingleLiveEvent<Void> =
            createLiveData(mAutoRefreshLiveEvent).also { mAutoRefreshLiveEvent = it }
        val stopRefreshLiveEvent: unionware.base.app.event.SingleLiveEvent<Boolean> =
            createLiveData(mStopRefreshLiveEvent).also { mStopRefreshLiveEvent = it }
        val stopLoadMoreLiveEvent: unionware.base.app.event.SingleLiveEvent<Boolean> =
            createLiveData(mStopLoadMoreLiveEvent).also { mStopLoadMoreLiveEvent = it }
        val stopLoadMoreWithNoMoreDataEvent =
            createLiveData(mStopLoadMoreWithNoMoreDataLiveEvent).also {
                mStopLoadMoreWithNoMoreDataLiveEvent = it
            }
    }


    /**
     * ViewModel 层发布自动刷新事件
     */
    open fun postAutoRefreshEvent() {
        mUIChangeRefreshLiveData.autoRefreshLiveEvent.call()
    }

    /**
     * ViewModel 层发布停止刷新事件
     * @param boolean false 刷新失败
     */
    open fun postStopRefreshEvent(boolean: Boolean = true) {
        mUIChangeRefreshLiveData.stopRefreshLiveEvent.value = boolean
    }

    /**
     * ViewModel 层发布停止加载更多
     * @param boolean false 加载失败
     */
    open fun postStopLoadMoreEvent(boolean: Boolean = true) {
        mUIChangeRefreshLiveData.stopLoadMoreLiveEvent.value = boolean
    }

    /**
     * ViewModel 层发布完成加载并标记没有更多数据
     */
    open fun postStopLoadMoreWithNoMoreDataEvent() {
        mUIChangeRefreshLiveData.stopLoadMoreWithNoMoreDataEvent.call()
    }

    abstract fun refreshData()

    abstract fun loadMore()
}
