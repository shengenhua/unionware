package com.unionware.wms.ui.fragment;

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
import com.unionware.wms.databinding.QueryItemListFragmentBinding;
import com.unionware.wms.inter.scan.BarcodeEditContract;
import com.unionware.wms.inter.scan.BarcodeEditPresenter;
import com.unionware.wms.inter.scan.CommonListContract;
import com.unionware.wms.inter.scan.CommonListPresenter;
import com.unionware.wms.model.event.TaskIdEvent;
import com.unionware.wms.ui.adapter.BillInfoAdapter;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.model.bean.BillBean;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.ViewReq;

@AndroidEntryPoint
public class QueryInfoListFragment extends BaseBindFragment<QueryItemListFragmentBinding> implements CommonListContract.View,
        OnRefreshListener, OnLoadMoreListener, OnItemChildClickListener, TextView.OnEditorActionListener, BarcodeEditContract.View, SwipeMenuCreator, OnItemMenuClickListener {

    private BillInfoAdapter adapter;
    private FiltersReq req;
    private ViewReq viewReq;
    private String name;
    private String scene;
    private View emptyView;


    @Inject
    CommonListPresenter presenter;
    @Inject
    BarcodeEditPresenter editPresenter;


    public static QueryInfoListFragment newInstance(String name, String scene) {
        Bundle args = new Bundle();
        QueryInfoListFragment fragment = new QueryInfoListFragment();
        args.putString("name", name);
        args.putString("scene", scene);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach(this);
        editPresenter.attach(this);
        req = new FiltersReq(1);
    }

    @Override
    public void initData() {
        if (getArguments() != null) {
            name = getArguments().getString("name");
            scene = getArguments().getString("scene");
        }
        emptyView = getLayoutInflater().inflate(unionware.base.R.layout.view_empty, null);
        ImageView iv_empty = emptyView.findViewById(unionware.base.R.id.iv_empty_icon);
        TextView tv_tips = emptyView.findViewById(unionware.base.R.id.tv_empty_tips);
        iv_empty.setImageResource(unionware.base.R.mipmap.ic_empty_scan_record);
        tv_tips.setText("暂无扫描记录");
        adapter = new BillInfoAdapter(0, getActivity());
        adapter.addChildClickViewIds(R.id.cd_bill_item);
        adapter.setOnItemChildClickListener(this);
        getMBind().rvList.setSwipeMenuCreator(this);
        getMBind().rvList.setOnItemMenuClickListener(this);
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(mContext));
        getMBind().rvList.setAdapter(adapter);
        getMBind().smRefresh.setOnRefreshListener(this);
        getMBind().smRefresh.setOnLoadMoreListener(this);
        getMBind().layoutSearch.ivCommonQuery.setVisibility(View.VISIBLE);
        getMBind().layoutSearch.etInProgressSearch.setVisibility(View.VISIBLE);
        getMBind().layoutSearch.etInProgressSearch.setOnEditorActionListener(this);

    }

    @Override
    public boolean enableEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getTaskInfoList(TaskIdEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put("taskId", event.getTaskId());
        req.setFilters(map);
        presenter.requestList(scene, name, req);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPageId(ViewReq event) {
        viewReq = new ViewReq(event.getPageId());
        viewReq.setCommand("INVOKE_DELETECODE");
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (null != req && isVisibleToUser) {
            presenter.requestList(scene, name, req);
        }

    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void onSuccess(int pos) {
        adapter.getData().clear();
        req.setIndex(1);
        presenter.requestList(scene, name, req);
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
        new XPopup.Builder(mContext).asConfirm("删除", "确认删除条码？", () -> {
            Map<String, Object> map = new HashMap<>();
            map.put("RecordId", adapter.getData().get(pos).getCode());
            viewReq.setParams(map);
            editPresenter.deleteBarcodeDetails(viewReq, pos);
            adapter.removeAt(pos);
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
    public void initObserve() {

    }
}
