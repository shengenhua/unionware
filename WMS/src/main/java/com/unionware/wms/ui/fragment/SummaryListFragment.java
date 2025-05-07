package com.unionware.wms.ui.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.lxj.xpopup.XPopup;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.unionware.wms.R;
import com.unionware.wms.databinding.QueryItemListFragmentBinding;
import com.unionware.wms.inter.scan.CommonListContract;
import com.unionware.wms.inter.scan.CommonListPresenter;

import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.model.bean.BillBean;
import unionware.base.model.req.ViewReq;
import com.unionware.wms.ui.adapter.BillInfoAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SummaryListFragment extends BaseBindFragment<QueryItemListFragmentBinding> implements CommonListContract.View,
        OnItemChildClickListener, OnRefreshListener, TextView.OnEditorActionListener {
    private BillInfoAdapter adapter;
    private ViewReq req;
    private View emptyView;


    @Inject
    CommonListPresenter presenter;

    public static SummaryListFragment newInstance() {
        Bundle args = new Bundle();
        SummaryListFragment fragment = new SummaryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach(this);
    }

    @Override
    public void initData() {
        emptyView = getLayoutInflater().inflate(unionware.base.R.layout.view_empty, null);
        ImageView iv_empty = emptyView.findViewById(unionware.base.R.id.iv_empty_icon);
        TextView tv_tips = emptyView.findViewById(unionware.base.R.id.tv_empty_tips);
        iv_empty.setImageResource(unionware.base.R.mipmap.ic_empty_scan_record);
        tv_tips.setText("暂无任务明细");
        getMBind().tvQueryFilter.setVisibility(View.VISIBLE);
        getMBind().layoutSearch.etInProgressSearch.setVisibility(View.GONE);
        adapter = new BillInfoAdapter(0, getActivity());
        adapter.addChildClickViewIds(R.id.cd_bill_item);
        adapter.setOnItemChildClickListener(this);
        getMBind().smRefresh.setOnRefreshListener(this);
        getMBind().smRefresh.setEnableLoadMore(false);
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(mContext));
        getMBind().rvList.setAdapter(adapter);
        getMBind().tvQueryFilter.setOnClickListener(view -> {
            String[] strings = new String[]{"全部", "已完成", "未完成"};
            new XPopup.Builder(getActivity()).maxHeight((int) getActivity().getResources().getDimension(unionware.base.R.dimen.dp_400))
                    .asBottomList("", strings, (position, text) -> {
                        getMBind().tvQueryFilter.setText(strings[position]);
                        adapter.getData().clear();
                        Map<String, Object> map = req.getParams();
                        map.put("type", String.valueOf(position));
                        req.setParams(map);
                        presenter.getSummaryList(req);
                    }).show();
        });

    }

    @Override
    public boolean enableEventBus() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getSummaryList(ViewReq event) {
        req = new ViewReq(event.getPageId());
        req.setCommand("INVOKE_SUMMARY");
        Map<String, Object> map = new HashMap<>();
        map.put("type", "0");
        req.setParams(map);
        presenter.getSummaryList(req);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (null != req && isVisibleToUser) {
            req.setCommand("INVOKE_SUMMARY");
            presenter.getSummaryList(req);
        }

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {

    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void showList(List<BillBean> list) {
        getMBind().rvList.setSwipeItemMenuEnabled(true);
        adapter.setNewInstance(list);
    }

    @Override
    public void showEmptyView() {
        getMBind().rvList.setSwipeItemMenuEnabled(false);
        adapter.getData().clear();
        adapter.notifyDataSetChanged();
        adapter.setEmptyView(emptyView);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
        if (adapter != null) {
            adapter.getData().clear();
        }
        if (req != null) {
            presenter.getSummaryList(req);
        }

    }

    @Override
    public void initObserve() {

    }
}
