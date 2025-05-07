package com.unionware.wms.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.lxj.xpopup.XPopup;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.unionware.wms.R;
import com.unionware.wms.databinding.NormalRecordActivityBinding;
import com.unionware.wms.inter.scan.BarcodeEditContract;
import com.unionware.wms.inter.scan.BarcodeEditPresenter;
import com.unionware.wms.inter.scan.CommonListContract;
import com.unionware.wms.inter.scan.CommonListPresenter;
import com.unionware.wms.model.event.RefreshCommonListEvent;
import com.unionware.wms.ui.adapter.BillInfoAdapter;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;
import unionware.base.model.bean.BillBean;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.ViewReq;

/**
 * @Author : pangming
 * @Time : On 2024/7/17 14:23
 * @Description : NormalRecordActivity
 */
@AndroidEntryPoint
public class NormalRecordActivity extends BaseBindActivity<NormalRecordActivityBinding> implements CommonListContract.View,
        OnRefreshListener, OnLoadMoreListener, OnItemChildClickListener, TextView.OnEditorActionListener, BarcodeEditContract.View, SwipeMenuCreator, OnItemMenuClickListener {
    private BillInfoAdapter adapter;
    private FiltersReq req;
    private ViewReq viewReq;
    private String name;
    private String scene;
    private String taskId;
    private View emptyView;

    @Inject
    CommonListPresenter presenter;//获取条码列表
    @Inject
    BarcodeEditPresenter editPresenter;//删除条码


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach(this);
        editPresenter.attach(this);
    }

    @Override
    public void initView() {
        getMBind().toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
        emptyView = getLayoutInflater().inflate(unionware.base.R.layout.view_empty, null);
        ImageView iv_empty = emptyView.findViewById(unionware.base.R.id.iv_empty_icon);
        TextView tv_tips = emptyView.findViewById(unionware.base.R.id.tv_empty_tips);
        iv_empty.setImageResource(unionware.base.R.mipmap.ic_empty_scan_record);
        tv_tips.setText("暂无扫描记录");
        adapter = new BillInfoAdapter(0, this);
        adapter.addChildClickViewIds(R.id.cd_bill_item);
        adapter.setOnItemChildClickListener(this);
        getMBind().rvList.setSwipeMenuCreator(this);
        getMBind().rvList.setOnItemMenuClickListener(this);
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(mContext));
        getMBind().rvList.setAdapter(adapter);
        getMBind().smRefresh.setOnRefreshListener(this);
        getMBind().smRefresh.setOnLoadMoreListener(this);
        getMBind().ivCommonQuery.setVisibility(View.VISIBLE);
        getMBind().etInProgressSearch.setVisibility(View.VISIBLE);
        getMBind().etInProgressSearch.setOnEditorActionListener(this);
        getMBind().tbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new XPopup.Builder(NormalRecordActivity.this).asConfirm("删除", "确认全部删除条码？", () -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Type", "All");
                    viewReq.setParams(map);
                    viewReq.setCommand("INVOKE_DELETECODE");
                    editPresenter.deleteBarcodeDetails(viewReq, -1);
                }).show();
            }
        });
    }

    @Override
    public void initData() {
        scene = getIntent().getStringExtra("scene");
        name = getIntent().getStringExtra("name");
        taskId = getIntent().getStringExtra("taskId");

        viewReq = new ViewReq(getIntent().getStringExtra("pageId"));
        req = new FiltersReq(1);
        Map<String, Object> filters = new HashMap<>();
        filters.put("taskId", taskId);
        req.setFilters(filters);
        presenter.requestList(scene, name, req);

    }

    @Override
    public boolean enableEventBus() {
        return true;
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        String keyword = textView.getText().toString().trim();
        Map<String, Object> filter = req.getFilters();
        filter.put("keyword", keyword);
        req.setIndex(1);
        presenter.requestList(scene, name, req);
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
    public void onSuccess(int pos) {
        if (pos == -1) {
            adapter.getData().clear();
            req.setIndex(1);
            presenter.requestList(scene, name, req);
        } else {
            if (adapter.getData().get(pos).getFBoxCode() != null && !adapter.getData().get(pos).getFBoxCode().isEmpty()) {
                //箱码目前删除一个明细，全部删
                adapter.getData().clear();
                req.setIndex(1);
                presenter.requestList(scene, name, req);
            } else {
                adapter.removeAt(pos);
            }
        }
        EventBus.getDefault().post(new RefreshCommonListEvent());
        ToastUtil.showToastCenter("删除成功");
    }

    @Override
    public void onUpdateSuccessEvent() {

    }

    @Override
    public void showList(List<BillBean> list) {
        if (1 == req.getPageIndex()) {
            adapter.getData().clear();
            adapter.setNewInstance(list);
        } else {
            adapter.addData(list);
        }

        getMBind().rvList.setSwipeItemMenuEnabled(adapter.getData().size() != 0);
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
        req.setIndex(1);
        Map<String, Object> filter = req.getFilters();
        if (null != filter) {
            req.getFilters().remove("keyword");
        }
        presenter.requestList(scene, name, req);

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        int index = req.getPageIndex();
        req.setIndex(index + 1);
        presenter.requestList(scene, name, req);
        refreshLayout.finishLoadMore();

    }

    @Override
    public void onItemClick(SwipeMenuBridge menuBridge, int pos) {
        menuBridge.closeMenu();
        new XPopup.Builder(NormalRecordActivity.this).asConfirm("删除", "确认删除条码？", () -> {
            Map<String, Object> map = new HashMap<>();
            map.put("RecordId", adapter.getData().get(pos).getCode());
            map.put("Type", "Record");
            viewReq.setParams(map);
            viewReq.setCommand("INVOKE_DELETECODE");
            editPresenter.deleteBarcodeDetails(viewReq, pos);
            // adapter.removeAt(pos);
        }).show();
    }

    @Override
    public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setBackgroundColor(getResources().getColor(unionware.base.R.color.red))
                .setWidth(getResources().getDimensionPixelSize(unionware.base.R.dimen.dp_80))
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        rightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
    }

    @Override
    public int onBindLayout() {
        return R.layout.normal_record_activity;
    }

}
