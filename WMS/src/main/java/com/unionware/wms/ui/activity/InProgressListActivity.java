package com.unionware.wms.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.unionware.wms.R;
import com.unionware.wms.databinding.InProgressActivtiyBinding;
import com.unionware.wms.inter.scan.InProgressContract;
import com.unionware.wms.inter.scan.InProgressPresenter;
import com.unionware.wms.model.bean.ProgressInfoBean;
import com.unionware.wms.model.req.IdReq;
import com.unionware.wms.ui.adapter.InProgressAdapter;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;
import unionware.base.model.req.FiltersReq;

@AndroidEntryPoint
public class InProgressListActivity extends BaseBindActivity<InProgressActivtiyBinding> implements InProgressContract.View, OnRefreshListener, OnLoadMoreListener,
        SwipeMenuCreator, OnItemMenuClickListener,
        OnItemClickListener, TextView.OnEditorActionListener {
    private InProgressAdapter adapter;
    private FiltersReq req;
    private String id;


    @Inject
    InProgressPresenter presenter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach(this);
    }

    @Override
    public void initView() {
        getMBind().rvList.setSwipeMenuCreator(this);
        getMBind().rvList.setOnItemMenuClickListener(this);
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(mContext));
        getMBind().rvList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL));
        getMBind().smRefresh.setOnRefreshListener(this);
        getMBind().smRefresh.setOnLoadMoreListener(this);
        getMBind().toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        getMBind().layoutSearch.etInProgressSearch.setOnEditorActionListener(this);
    }

    @Override
    public void initData() {
        adapter = new InProgressAdapter();
        id = getIntent().getStringExtra("id");
        getMBind().rvList.setAdapter(adapter);
        req = new FiltersReq(1);
        if (id != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("setId", id);
            req.setFilters(map);
        }
        presenter.getInProgressList(req);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void showInProgressList(List<ProgressInfoBean> list) {
        if (req.getPageIndex() == 1) {
            adapter.setNewInstance(list);
        } else {
            adapter.addData(list);
        }
    }

    @Override
    public void removeInProgressItem(int pos) {
        adapter.removeAt(pos);
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
        int index = req.getPageIndex();
        index++;
        req.setIndex(index);
        presenter.getInProgressList(req);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
        req.setIndex(1);
        adapter.getData().clear();
        presenter.getInProgressList(req);
    }

    @Override
    public void onItemClick(SwipeMenuBridge menuBridge, int pos) {
        new XPopup.Builder(this).asConfirm("提示", "确认删除此装箱单？", new OnConfirmListener() {
            @Override
            public void onConfirm() {
                IdReq idReq = new IdReq();
                idReq.setId(Integer.parseInt(adapter.getData().get(pos).getId()));
                presenter.deleteTempPackingList(idReq, pos);
            }
        }).show();
        menuBridge.closeMenu();

    }

    @Override
    public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext).setText("删除").setTextColor(Color.WHITE).setBackgroundColor(getResources().getColor(unionware.base.R.color.red)).setWidth(getResources().getDimensionPixelSize(unionware.base.R.dimen.dp_80)).setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        rightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
    }

    @Override
    public int onBindLayout() {
        return R.layout.in_progress_activtiy;
    }


    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        ProgressInfoBean bean = (ProgressInfoBean) adapter.getData().get(position);
        String count = bean.getCount(); // 已装件数
        String taskId = bean.getId(); // 任务id
        String capacity = bean.getCapacity(); // 箱容量
        String boxcode = null != bean.getBoxCode() ? bean.getBoxCode() : "";
        Intent intent = new Intent();
        intent.putExtra("count", count);
        intent.putExtra("taskId", taskId);
        intent.putExtra("capacity", capacity);
        intent.putExtra("boxcode", boxcode);
        setResult(1001, intent);
        finish();
    }

    @Override
    public boolean onEditorAction(TextView view, int i, KeyEvent event) {
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            String key = view.getText().toString().trim();
            doSearch(key);
            return true;
        }
        return false;
    }

    private void doSearch(String key) {
        adapter.getData().clear();
        Map<String, Object> map = req.getFilters();
        map.put("keyword", key);
        req.setIndex(1);
        req.setFilters(map);
        presenter.getInProgressList(req);
    }
}
