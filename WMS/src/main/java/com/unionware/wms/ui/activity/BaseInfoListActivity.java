package com.unionware.wms.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.unionware.wms.R;
import com.unionware.wms.databinding.BaseInfoActivityBinding;
import com.unionware.wms.inter.scan.BaseInfoContract;
import com.unionware.wms.inter.scan.BaseInfoPresenter;

import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;
import unionware.base.model.bean.BaseInfoBean;
import unionware.base.model.req.FiltersReq;
import com.unionware.wms.ui.adapter.BaseInfoAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BaseInfoListActivity extends BaseBindActivity<BaseInfoActivityBinding> implements BaseInfoContract.View, OnRefreshListener, OnLoadMoreListener, OnItemClickListener {
    private BaseInfoAdapter adapter;
    private int index = 1;
    private FiltersReq req;
    private String key;
    private String parentId = "";

    @Inject
    BaseInfoPresenter presenter;

    @Override
    public int onBindLayout() {
        return R.layout.base_info_activity;
    }

    @Override
    public void initView() {
        presenter.attach(this);
        req = new FiltersReq(index);
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseInfoAdapter();
        adapter.setOnItemClickListener(this);
        getMBind().rvList.setAdapter(adapter);
        getMBind().tvScanInProgress.setVisibility(View.GONE);
        getMBind().smRefresh.setOnRefreshListener(this);
        getMBind().smRefresh.setOnLoadMoreListener(this);
        getMBind().smRefresh.setRefreshFooter(new ClassicsFooter(this));
        setSupportActionBar(mBind.toolbar);
        mBind.tvTabTitle.setText("基础资料");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBind.toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

    @Override
    public void initData() {
        key = getIntent().getStringExtra("key"); // 从扫描配置的key来
        parentId = getIntent().getStringExtra("parentId"); // 辅助资料父id
        if (!"".equals(parentId)) {
            Map<String, Object> filters = new HashMap<>();
            filters.put("parentId", parentId);
            req.setFilters(filters);
        }
        presenter.getBaseInfoList(key, req);
    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void showBaseInfoList(List<BaseInfoBean> list) {
        if (index == 1) {
            adapter.setList(list);
        } else {
            adapter.addData(list);
        }

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        adapter.getData().clear();
        index = 1;
        req.setIndex(index);
        presenter.getBaseInfoList(key, req);
        refreshLayout.finishRefresh();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        index++;
        req.setIndex(index);
        presenter.getBaseInfoList(key, req);
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        boolean isEdit = getIntent().getBooleanExtra("isEdit", false);
        BaseInfoBean bean = (BaseInfoBean) adapter.getData().get(position);
        if (isEdit) {
            bean.setPos(getIntent().getIntExtra("position", 0));
            bean.setIndex(getIntent().getIntExtra("index", 0));
            EventBus.getDefault().post(bean);
        } else {
            Intent intent = getIntent().putExtra("baseInfo", bean);
            setResult(getIntent().getIntExtra("position", 0), intent);
        }
        finish();
    }
}
