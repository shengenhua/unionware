package com.unionware.wms.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.ConfirmPopupView;
import com.unionware.wms.R;
import com.unionware.wms.URLPath;
import com.unionware.wms.databinding.PackingScanRecordFragmentBinding;
import com.unionware.wms.inter.scan.RecordContract;
import com.unionware.wms.inter.scan.RecordPresenter;
import com.unionware.wms.model.bean.BarcodeDetailsBean;
import com.unionware.wms.model.event.RefreshTaskIdEvent;
import com.unionware.wms.model.req.DeleteReq;

import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.model.req.FiltersReq;
import com.unionware.wms.ui.adapter.PackingDetalisRecordAdapter;
import com.unionware.wms.ui.adapter.PackingRecordAdapter;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * 装箱扫描记录
 */
@AndroidEntryPoint
public class PackingScanRecordFragment extends BaseBindFragment<PackingScanRecordFragmentBinding> implements
        RecordContract.View, TextView.OnEditorActionListener, OnItemChildClickListener, SwipeMenuCreator, OnItemMenuClickListener {

    @Inject
    RecordPresenter presenter;

    private PackingDetalisRecordAdapter detalisAdapter; // 详情适配
    private PackingRecordAdapter packingAdapter;
    private ConfirmPopupView popupView; // 删除弹框（搜索）
    private DeleteReq req; // 删除条码参数
    private String taskid = "";

    private static final int TYPE_DELETE_NONE = -1; //删除策略 -- 什么都不做（仅提示）
    private static final int TYPE_DELETE_ALL = -2; //删除策略 -- 清除全部


    public static PackingScanRecordFragment newInstance(String id) {
        Bundle args = new Bundle();
        PackingScanRecordFragment fragment = new PackingScanRecordFragment();
        args.putString("taskId", id);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach(this);
        taskid = getArguments().getString("taskId");
    }


    private void getRecordDetailInfo(String name) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("primaryId", taskid);
        presenter.getRecordDetailInfo(name, new FiltersReq(map));
    }

    @Override
    public void initData() {
        req = new DeleteReq(Integer.parseInt(getArguments().getString("taskId")));
        packingAdapter = new PackingRecordAdapter();
        popupView = new XPopup.Builder(getContext()).asConfirm("删除", "确认删除条码？", () -> {
            presenter.deletaBarcodeInfo(req, TYPE_DELETE_NONE);
        });

        getMBind().etRecordSearch.setOnEditorActionListener(this);

        detalisAdapter = new PackingDetalisRecordAdapter();

        // 包装条码
        getMBind().rvPackingList.setLayoutManager(new LinearLayoutManager(mContext));
        getMBind().rvPackingList.setAdapter(packingAdapter);
        getMBind().rvDetalisList.setSwipeMenuCreator(this);
        getMBind().rvDetalisList.setOnItemMenuClickListener(this);
        getMBind().rvDetalisList.setLayoutManager(new LinearLayoutManager(mContext));
        getMBind().rvDetalisList.setAdapter(detalisAdapter);


        getMBind().tvRecordPackingDel.setOnClickListener(view -> {
            Map<String, Object> map = new HashMap<>();
            new XPopup.Builder(getContext()).asConfirm("删除", "确认删除条码？", () -> {
                BarcodeDetailsBean bean = packingAdapter.getData().get(0);
                List list = new ArrayList();
                list.add(bean.getBoxCode());
                DeleteReq i = new DeleteReq(bean.getId());
                i.setCodes(list);
                presenter.deletaBarcodeInfo(i, TYPE_DELETE_NONE);
            }).show();
        });


        getMBind().tvRecordDetalisDel.setOnClickListener(view -> {
            List<String> barcodeList = detalisAdapter.getData().stream().map(BarcodeDetailsBean::getBarCode).collect(Collectors.toList());
            new XPopup.Builder(getContext()).asConfirm("删除", "确认删除条码？", () -> {
                BarcodeDetailsBean bean = packingAdapter.getData().get(0);
                DeleteReq i = new DeleteReq(bean.getId());
                i.setCodes(barcodeList);
                presenter.deletaBarcodeInfo(i, TYPE_DELETE_ALL);
            }).show();
        });

    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void removeDetalsBarcodeInfo(int pos) {
        ToastUtil.showToastCenter( "删除成功");
        if (TYPE_DELETE_ALL == pos) {
            detalisAdapter.getData().clear();
        } else if (TYPE_DELETE_NONE != pos) {
            detalisAdapter.removeAt(pos);
        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getRecordDetailInfo(URLPath.Pack.PATH_PACK_SCAN_PACKING_CODE); // 获取明细条码
            getRecordDetailInfo(URLPath.Pack.PATH_PACK_SCAN_DETAILS_CODE); // 获取明细条码
        }
    }

    @Override
    public void showPackingDetalisInfo(List<BarcodeDetailsBean> list) {
        if (list == null || list.size() == 0) {
            packingAdapter.setEmptyView(unionware.base.R.layout.view_no_data);
        } else {
            packingAdapter.getData().clear();
            packingAdapter.setNewInstance(list);
        }

    }

    @Override
    public void showDetalsBarcodeInfo(List<BarcodeDetailsBean> list) {
        if (list == null || list.size() == 0) {
            detalisAdapter.setEmptyView(unionware.base.R.layout.view_no_data);
        } else {
            detalisAdapter.getData().clear();
            detalisAdapter.setNewInstance(list);
        }

    }

    @Override
    public boolean onEditorAction(TextView view, int i, KeyEvent keyEvent) {
        showDeleteBarcodeDialog(view.getText().toString());
        return false;
    }

    private void showDeleteBarcodeDialog(String barcode) {
        List<String> list = new ArrayList();
        list.add(barcode);
        req.setCodes(list);
        popupView.show();
    }

    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {


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
    public void onItemClick(SwipeMenuBridge menuBridge, int pos) {
        menuBridge.closeMenu();
        new XPopup.Builder(getContext()).asConfirm("删除", "确认删除条码？", () -> {
            BarcodeDetailsBean bean = detalisAdapter.getData().get(pos);
            presenter.deletaBarcodeInfo(new DeleteReq(bean.getId(), Integer.parseInt(bean.getEntryId())), pos);
        }).show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(RefreshTaskIdEvent event) {
        taskid = event.getId();
    }

    @Override
    public boolean enableEventBus() {
        return true;
    }

    @Override
    public void initObserve() {

    }
}
