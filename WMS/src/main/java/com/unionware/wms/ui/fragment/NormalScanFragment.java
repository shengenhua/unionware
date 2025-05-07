package com.unionware.wms.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.lxj.xpopup.impl.LoadingPopupView;
import com.unionware.wms.databinding.NormalScanFragmentBinding;
import com.unionware.wms.inter.wms.scan.NormalScanPresenter;
import com.unionware.wms.model.bean.NormalScanConfigBean;

import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.model.req.ViewReq;
import unionware.base.model.bean.BillBean;
import java.util.ArrayList;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * @Author : pangming
 * @Time : On 2024/7/11 15:53
 * @Description : NormalStockScanFragment
 * 废弃不使用
 */
@AndroidEntryPoint
public class NormalScanFragment extends BaseBindFragment<NormalScanFragmentBinding> {
    @Inject
    NormalScanPresenter presenter;
    private LoadingPopupView loading;
    private BillBean billBean;
    private NormalScanConfigBean normalScanConfigBean;
    private ArrayList<String> primaryIds;
    private boolean isTask = false; // 是否从任务列表点击进去的
    private String taskId;
    private String scene;
    private ViewReq req;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static NormalScanFragment newInstance(BillBean bean, String scene, NormalScanConfigBean normalScanConfigBean,ArrayList<String> primaryIds) {
        Bundle args = new Bundle();
        NormalScanFragment fragment = new NormalScanFragment();
        args.putSerializable("bean", bean);
        args.putSerializable("normalScanConfigBean", normalScanConfigBean);
        args.putStringArrayList("primaryIds", primaryIds);
        args.putString("scene", scene);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initObserve() {

    }
}
