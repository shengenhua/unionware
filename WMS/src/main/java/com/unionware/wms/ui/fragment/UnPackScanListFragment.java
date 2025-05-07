package com.unionware.wms.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.unionware.wms.R;
import com.unionware.wms.databinding.UnpackScanListFragmentBinding;
import com.unionware.wms.inter.unpack.UnPackScanListContract;
import com.unionware.wms.inter.unpack.UnPackScanListPresenter;
import com.unionware.wms.ui.adapter.UnPackDetailsRecordAdapter;
import com.unionware.wms.ui.adapter.UnPackPackRecordAdapter;
import com.unionware.wms.ui.adapter.UnPackScanRecordAdapter;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.room.table.UnPackScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/6/1 13:44
 * @Description : UpPackScanListFragment
 */
@AndroidEntryPoint
public class UnPackScanListFragment extends BaseBindFragment<UnpackScanListFragmentBinding> implements UnPackScanListContract.View, View.OnClickListener, SwipeMenuCreator, OnItemMenuClickListener {
    @Inject
    UnPackScanListPresenter presenter;

    UnPackPackRecordAdapter unPackPackRecordAdapter;
    UnPackDetailsRecordAdapter unPackDetailsRecordAdapter;
    public static UnPackScanListFragment newInstance(String id) {
        UnPackScanListFragment fragment = new UnPackScanListFragment();
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
        Objects.requireNonNull(getMBind()).tvRecordPackingDel.setOnClickListener(this);
        Objects.requireNonNull(getMBind()).tvRecordDetalisDel.setOnClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            presenter.getPackScanListByInternalCodeId(mContext,getArguments().getString("id"));
            presenter.getDetailsScanListByInternalCodeId(mContext,getArguments().getString("id"));
        }
    }

    @Override
    public void showDetailsScanList(List<UnPackScanInfo> scanInfoList) {
        if(unPackDetailsRecordAdapter == null){
            unPackDetailsRecordAdapter = new UnPackDetailsRecordAdapter();
            Objects.requireNonNull(getMBind()).rvDetalisList.setLayoutManager(new LinearLayoutManager(getActivity()));
            getMBind().rvDetalisList.setSwipeMenuCreator(this);
            getMBind().rvDetalisList.setOnItemMenuClickListener(this);
            getMBind().rvDetalisList.setAdapter(unPackDetailsRecordAdapter);
        }
        unPackDetailsRecordAdapter.setList(scanInfoList);
    }


    @Override
    public void showPackScanList(List<UnPackScanInfo> scanInfoList) {
        unPackPackRecordAdapter = new UnPackPackRecordAdapter();
        Objects.requireNonNull(getMBind()).rvPackingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        getMBind().rvPackingList.setAdapter(unPackPackRecordAdapter);
        unPackPackRecordAdapter.setList(scanInfoList);
        if(scanInfoList.size()==0){
            getMBind().tvRecordPackingDel.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void upDataByDeleteDetail(UnPackScanInfo unPackScanInfo) {
        unPackDetailsRecordAdapter.remove(unPackScanInfo);
    }

    @Override
    public void upDataByDeleteAll() {
        unPackPackRecordAdapter.getData().clear();
        unPackPackRecordAdapter.notifyDataSetChanged();
        unPackDetailsRecordAdapter.getData().clear();
        unPackDetailsRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void upDataByDeleteDetailsAll() {
        unPackDetailsRecordAdapter.getData().clear();
        unPackDetailsRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.tv_record_detalis_del){
            if(unPackDetailsRecordAdapter.getData().size()>0){
                new XPopup.Builder(getContext()).asConfirm("标题", "确认删除本次扫描的子条码信息数据？",
                            new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    presenter.deleteAllDetailByInternalCodeId(mContext,getArguments().getString("id"),unPackDetailsRecordAdapter.getData().get(0).getPackCode());
                                }
                            })
                    .show();
            }else {
                return;
            }

        }else if(view.getId() == R.id.tv_record_packing_del){
            if(unPackPackRecordAdapter.getData().size()>0){
                new XPopup.Builder(getContext()).asConfirm("标题", "确认删除本次扫描的信息数据？",
                                new OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {
                                        presenter.deleteByInternalCodeId(mContext,getArguments().getString("id"));
                                    }
                                })
                        .show();
            }else {
                return;
            }

        }
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
        presenter.deleteByEntity(mContext,adapterPosition,unPackDetailsRecordAdapter.getItem(adapterPosition));
    }

    @Override
    public void initObserve() {

    }
}
