package com.unionware.wms.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.unionware.wms.R;
import com.unionware.wms.databinding.TransScanListFragmentBinding;
import com.unionware.wms.inter.trans.TransScanListContract;
import com.unionware.wms.inter.trans.TransScanListPresenter;
import com.unionware.wms.model.event.EmptyEvent;
import com.unionware.wms.ui.adapter.TransDetailsRecordAdapter;
import com.unionware.wms.ui.adapter.TransPackRecordAdapter;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.room.table.TransScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/6/6 16:22
 * @Description : TransScanListFragment
 */
@AndroidEntryPoint
public class TransScanListFragment extends BaseBindFragment<TransScanListFragmentBinding> implements TransScanListContract.View, SwipeMenuCreator, OnItemMenuClickListener, View.OnClickListener {
    @Inject
    TransScanListPresenter presenter;
    TransDetailsRecordAdapter transDetailsRecordAdapter;
    TransPackRecordAdapter transPackRecordAdapter;

    public static TransScanListFragment newInstance(String id) {
        TransScanListFragment fragment = new TransScanListFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
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
        Objects.requireNonNull(getMBind()).tvRecordPackingDel.setOnClickListener((View.OnClickListener) this);
        Objects.requireNonNull(getMBind()).tvRecordDetalisDel.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            presenter.getInBarcodeScanListByInternalCodeId(mContext, getArguments().getString("id"));
            presenter.getDetailsScanListByInternalCodeId(mContext, getArguments().getString("id"));
        }
    }

    @Override
    public void showScanList(List<TransScanInfo> scanInfoList) {
//        adapter = new TransScanRecordAdapter();
//        Objects.requireNonNull(getMBind()).rcvScan.setLayoutManager(new LinearLayoutManager(getActivity()));
//        getMBind().rcvScan.setSwipeMenuCreator(this);
//        getMBind().rcvScan.setAdapter(adapter);
//        adapter.setList(scanInfoList);
//        adapter.setClickListener(this);
    }

    @Override
    public void showDetailsScanList(List<TransScanInfo> scanInfoList) {
        if (transDetailsRecordAdapter == null) {
            transDetailsRecordAdapter = new TransDetailsRecordAdapter();
            Objects.requireNonNull(getMBind()).rvDetalisList.setLayoutManager(new LinearLayoutManager(getActivity()));
            getMBind().rvDetalisList.setSwipeMenuCreator(this);
            getMBind().rvDetalisList.setOnItemMenuClickListener(this);
            getMBind().rvDetalisList.setAdapter(transDetailsRecordAdapter);
        }
        transDetailsRecordAdapter.setList(scanInfoList);
    }

    @Override
    public void showPackScanList(List<TransScanInfo> scanInfoList) {
        transPackRecordAdapter = new TransPackRecordAdapter();
        Objects.requireNonNull(getMBind()).rvPackingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        getMBind().rvPackingList.setAdapter(transPackRecordAdapter);
        transPackRecordAdapter.setList(scanInfoList);
        if (scanInfoList.size() == 0) {
            getMBind().tvRecordPackingDel.setVisibility(View.INVISIBLE);
        } else {
            getMBind().tvRecordPackingDel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void upDataByDeleteDetail(TransScanInfo transScanInfo) {
        transDetailsRecordAdapter.remove(transScanInfo);
    }

    @Override
    public void upDataByDeleteAll() {
        transPackRecordAdapter.getData().clear();
        transPackRecordAdapter.notifyDataSetChanged();
        transDetailsRecordAdapter.getData().clear();
        transDetailsRecordAdapter.notifyDataSetChanged();
        EventBus.getDefault().post(new EmptyEvent());
    }

    @Override
    public void upDataByDeleteDetailsAll() {
        transDetailsRecordAdapter.getData().clear();
        transDetailsRecordAdapter.notifyDataSetChanged();
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
    public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
        menuBridge.closeMenu();
        presenter.deleteByEntity(mContext, adapterPosition, transDetailsRecordAdapter.getItem(adapterPosition));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_record_packing_del) {
            if (transPackRecordAdapter.getData().size() > 0) {
                new XPopup.Builder(getContext()).asConfirm("标题", "确认删除本次扫描的信息数据？",
                                new OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {
                                        presenter.deleteByInternalCodeId(mContext, getArguments().getString("id"));
                                    }
                                })
                        .show();
            }

        } else if (view.getId() == R.id.tv_record_detalis_del) {
            if (transDetailsRecordAdapter.getData().size() > 0) {
                new XPopup.Builder(getContext()).asConfirm("标题", "确认删除本次扫描的子条码信息数据？",
                                () -> presenter.deleteAllDetailByInternalCodeId(mContext, getArguments().getString("id")))
                        .show();
            }

        }
    }

    @Override
    public void initObserve() {

    }
}
