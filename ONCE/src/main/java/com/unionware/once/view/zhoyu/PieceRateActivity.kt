package com.unionware.once.view.zhoyu

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.loadState.LoadState
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.unionware.once.viewmodel.zhoyu.ZhoYuViewModel
import com.unionware.once.R
import com.unionware.once.app.RouterOncePath
import com.unionware.once.databinding.ActivityPieceRateBinding
import com.unionware.virtual.view.adapter.BillListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import unionware.base.app.utils.DateFormatUtils
import unionware.base.app.view.base.BaseMvvmToolbarActivity
import unionware.base.model.bean.PropertyBean
import unionware.base.ui.datepicker.CustomDatePicker

/**
 * 中裕 计件工资查询
 * Author: sheng
 * Date:2024/12/20
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_ZHOYU_PIECE_RATE)
class PieceRateActivity : BaseMvvmToolbarActivity<ActivityPieceRateBinding, ZhoYuViewModel>() {
    override fun onBindVariableId(): MutableList<Pair<Int, Any>> = mutableListOf()
    override fun onBindLayout(): Int = R.layout.activity_piece_rate

    private val headItem = PropertyBean("time", "查询时间").also {
        it.value = DateFormatUtils.long2Str(System.currentTimeMillis(), false)
    }

    fun timeQuery(time: String? = headItem.value) {
        headItem.value = time
        binding?.apply {
            timeLayout.item = headItem
        }
        mViewModel.empPieceSearch(mutableMapOf<String?, Any?>().apply {
            time?.also {
                put("Date", it)
            }
        })
    }

    /**
     * 场景码
     */
    @JvmField
    @Autowired(name = "scene")
    var scene: String = ""

    /**
     * 标题
     */
    @JvmField
    @Autowired(name = "title")
    var title: String = ""

    private val billListAdapter = BillListAdapter()
    private var adapterHelper: QuickAdapterHelper? = null


    override fun initViewObservable() {
        mViewModel.apply {
            dataLiveData.observe(this@PieceRateActivity) {
                billListAdapter.submitList(it)
                lifecycleScope.launch {
                    delay(200)
                    adapterHelper?.trailingLoadState = LoadState.NotLoading(true)
                    binding?.smRefresh?.finishRefresh()
                }
            }
        }
    }

    override fun initView() {
        getListAdapter().apply {
            binding?.rvList?.adapter = this
            binding?.rvList?.layoutManager = LinearLayoutManager(this@PieceRateActivity)
        }
        binding?.apply {
            smRefresh.setOnRefreshListener {
                mViewModel.apply {
                    timeQuery()
                }
            }
            timeLayout.item = headItem
            timeLayout.tvDataTime.setOnClickListener {
                openTimePick()
            }
            timeLayout.ivArrows.setOnClickListener {
                openTimePick()
            }
        }
        setTitle(title)
    }

    private fun getListAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        billListAdapter.let {
            adapterHelper = QuickAdapterHelper.Builder(it)
                .setTrailingLoadStateAdapter(object : TrailingLoadStateAdapter.OnTrailingListener {
                    override fun onFailRetry() = Unit
                    override fun onLoad() {
                        //加页数
                        mViewModel.pageIndexData.value = mViewModel.pageIndexData.value?.plus(1)
                        timeQuery()
                    }
                }).build()
        }
        return adapterHelper?.adapter ?: billListAdapter
    }

    private fun openTimePick() {
        //时间选择器
        val beginTimestamp = DateFormatUtils.str2Long("1980-01-01", false)
        val endTimestamp = DateFormatUtils.str2Long("2100-01-01", false)
        val picker = CustomDatePicker(this, { timestamp: Long ->
            val time = DateFormatUtils.long2Str(timestamp, false)
            mViewModel.pageIndexData.value = 1
            timeQuery(time)
        }, beginTimestamp, endTimestamp)

        picker.setOnlyShowTime(false)
        picker.setCancelable(false)
        picker.setScrollLoop(false)
        picker.setCanShowAnim(false)
        picker.show(DateFormatUtils.str2Long(headItem.value, false))
    }

    override fun initData() {
        timeQuery()
    }
}