package com.unionware.wms.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.unionware.wms.R;
import com.unionware.wms.URLPath;
import com.unionware.wms.databinding.InProgressActivtiyBinding;
import com.unionware.wms.inter.scan.CommonListContract;
import com.unionware.wms.inter.scan.CommonListPresenter;
import com.unionware.wms.ui.activity.StockScanActivity;
import com.unionware.wms.ui.adapter.BillInfoAdapter;

import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.model.bean.BillBean;
import unionware.base.model.bean.MenuBean;
import unionware.base.model.req.FiltersReq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CommonListFragment extends BaseBindFragment<InProgressActivtiyBinding> implements CommonListContract.View,
        OnRefreshListener, OnLoadMoreListener, OnItemClickListener, TextView.OnEditorActionListener {
    private BillInfoAdapter adapter;
    private FiltersReq req;
    private MenuBean bean;
    private String name;
    private ActivityResultLauncher<Intent> launcher;
    private View emptyView;
    @Inject
    CommonListPresenter presenter;


    public static CommonListFragment newInstance(MenuBean bean, String name) {
        Bundle args = new Bundle();
        CommonListFragment fragment = new CommonListFragment();
        args.putSerializable("bean", bean);
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach(this);
        registerActivityResult();
    }


    @Override
    public void initData() {
        emptyView = getLayoutInflater().inflate(unionware.base.R.layout.view_empty, null);
        ImageView iv_empty = emptyView.findViewById(unionware.base.R.id.iv_empty_icon);
        TextView tv_tips = emptyView.findViewById(unionware.base.R.id.tv_empty_tips);
        iv_empty.setImageResource(unionware.base.R.mipmap.ic_empty_bill);
        name = getArguments().getString("name");
        bean = (MenuBean) getArguments().getSerializable("bean");
//        getMBind().layoutSearch.ivCommonQuery.setVisibility(View.VISIBLE);
//        getMBind().layoutSearch.etInProgressSearch.setVisibility(View.VISIBLE);
        tv_tips.setText(URLPath.Stock.PATH_STOCK_IN_PROGRESS_TASK_CODE.equals(name) ? "还没有任务哦～" : "还没有单据哦～");
        Map<String, Object> map = new HashMap<>();
        map.put("setId", bean.getId());
        String ruleId = null == bean.getConvertId() || "".equals(bean.getConvertId()) ? name : bean.getConvertId();
        if (URLPath.Stock.PATH_STOCK_IN_PROGRESS_TASK_CODE.equals(name)) { // 任务列表
            req = new FiltersReq(1, map);
        } else {
            req = new FiltersReq(1, true, ruleId, map);
            Map<String, Object> params = new HashMap<>();
            params.put("jobFlowId", bean.getFlowId());
            req.setParams(params);
        }
        adapter = new BillInfoAdapter(1, getActivity());
        adapter.setOnItemClickListener(this);
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(mContext));
        getMBind().rvList.setAdapter(adapter);
        getMBind().smRefresh.setOnRefreshListener(this);
        getMBind().smRefresh.setOnLoadMoreListener(this);
        presenter.requestList(bean.getScene(), name, req);
//        getMBind().layoutSearch.etInProgressSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
//            if (i == EditorInfo.IME_ACTION_SEARCH) {
//                String keyword = textView.getText().toString().trim();
//                Map<String, String> filter = req.getFilters();
//                filter.put("keyword", keyword);
//                req.setIndex(1);
//                presenter.requestList(bean.getScene(), name, req);
//            }
//            return false;
//        });
    }


    private void registerActivityResult() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == URLPath.Stock.PATH_SUBMIT_FINISH) {
                req.setIndex(1);
                presenter.requestList(bean.getScene(), name, req);
            }

        });
    }

    @Override
    public void showList(List<BillBean> list) {
        if (1 == req.getPageIndex()) {
            adapter.getData().clear();
            adapter.setNewInstance(list);
        } else {
            adapter.addData(list);
        }

    }

    @Override
    public void showEmptyView() {
        adapter.setEmptyView(emptyView);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (null != req && isVisibleToUser) {
            req.setIndex(1);
            presenter.requestList(bean.getScene(), name, req);
        }

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        int index = req.getPageIndex();
        req.setIndex(index + 1);
        presenter.requestList(bean.getScene(), name, req);
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        adapter.getData().clear();
        req.setIndex(1);
        req.getFilters().remove("keyword");
        presenter.requestList(bean.getScene(), name, req);
        refreshLayout.finishRefresh();
    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }


    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
        jumpToScanActivity(adapter, position);
    }

    private void jumpToScanActivity(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, int position) {
        BillBean billBean = (BillBean) adapter.getData().get(position);
        billBean.setPrimaryId(bean.getId());
        billBean.setFlowId(bean.getFlowId());
        billBean.setFormId(name);
        Intent intent = new Intent(getActivity(), StockScanActivity.class);
        intent.putExtra("bean", billBean);
        intent.putExtra("scene", bean.getScene());
        launcher.launch(intent);
    }


    @Override
    public void initObserve() {

    }
}
