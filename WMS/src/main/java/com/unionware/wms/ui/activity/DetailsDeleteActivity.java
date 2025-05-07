package com.unionware.wms.ui.activity;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.lxj.xpopup.XPopup;
import com.unionware.wms.R;
import com.unionware.wms.URLPath;
import com.unionware.wms.databinding.CommonSwipeListBinding;
import com.unionware.wms.inter.scan.BarcodeEditContract;
import com.unionware.wms.inter.scan.BarcodeEditPresenter;
import com.unionware.wms.inter.scan.CommonListContract;
import com.unionware.wms.inter.scan.CommonListPresenter;

import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;
import unionware.base.model.bean.BillBean;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.ViewReq;
import com.unionware.wms.ui.adapter.BillInfoAdapter;
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

@AndroidEntryPoint
public class DetailsDeleteActivity extends BaseBindActivity<CommonSwipeListBinding> implements CommonListContract.View, BarcodeEditContract.View, SwipeMenuCreator, OnItemMenuClickListener {
    private BillInfoAdapter adapter;
    private FiltersReq req;

    @Inject
    BarcodeEditPresenter presenter;

    @Inject
    CommonListPresenter listPresenter;


    @Override
    public int onBindLayout() {
        return R.layout.common_swipe_list;
    }

    @Override
    public void initView() {
        listPresenter.attach(this);
        presenter.attach(this);
    }


    @Override
    public void initData() {
        String container = getIntent().getStringExtra("container");

        getMBind().toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
        Map<String, Object> map = new HashMap<>();
        map.put("container", container);
        map.put("TaskId", getIntent().getStringExtra("TaskId"));
        req.setFilters(map);
        getMBind().tvTabTitle.setText("删除明细");
        adapter = new BillInfoAdapter(0, this);
        getMBind().rvList.setSwipeMenuCreator(this);
        getMBind().rvList.setOnItemMenuClickListener(this);
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(this));
        getMBind().rvList.setAdapter(adapter);
        listPresenter.requestList(URLPath.Stock.PATH_STOCK_SCENE, URLPath.Stock.PATH_STOCK_SCAN_TASK_RECORD_CODE, req);

    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void onSuccess(int pos) {
        adapter.removeAt(pos);
    }

    @Override
    public void onUpdateSuccessEvent() {

    }

    @Override
    public void showList(List<BillBean> list) {
        adapter.setNewInstance(list);
    }

    @Override
    public void showEmptyView() {

    }

    @Override
    public void onItemClick(SwipeMenuBridge menuBridge, int pos) {
        menuBridge.closeMenu();
        ViewReq req = new ViewReq("INVOKE_DELETECODE", getIntent().getStringExtra("pageId"));
        new XPopup.Builder(this).asConfirm("删除", "确认删除条码？", () -> {
            Map<String, Object> map = new HashMap<>();
            map.put("FLPN", adapter.getData().get(pos).getBarcode());
            req.setParams(map);
            presenter.deleteBarcodeDetails(req, pos);
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
}
