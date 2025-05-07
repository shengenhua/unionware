package com.unionware.wms.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.unionware.wms.R;
import com.unionware.wms.URLPath;
import com.unionware.wms.databinding.RepackingFragmentBinding;
import com.unionware.wms.inter.trans.TransContract;
import com.unionware.wms.inter.trans.TransPresenter;
import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;

import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.model.bean.EntityBean;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.AnalysisReq;
import com.unionware.wms.model.event.EmptyEvent;
import com.unionware.wms.strategy.PackConfig;
import com.unionware.wms.ui.activity.PrintTemplateListActivity;
import com.unionware.wms.ui.adapter.ScanAdapter;
import com.unionware.wms.ui.adapter.TransAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.model.bean.PrintTemplateBean;

/**
 * @Author : pangming
 * @Time : On 2023/6/5 14:57
 * @Description : 整箱转箱 RePackingFragment
 */
@AndroidEntryPoint
public class BTransFragment extends BaseBindFragment<RepackingFragmentBinding> implements TransContract.View, ScanAdapter.OnEditorActionChangeListener, OnItemChildClickListener, View.OnClickListener {
    @Inject
    TransPresenter presenter;
    private TransAdapter adapter;
    private ScanConfigBean bean;

    private ActivityResultLauncher<Intent> launcher;

    private BarcodeDetailsInfoBean inBarcode = null;

    private LoadingPopupView loading;
    private PrintTemplateBean printTemplateBean;

    public static BTransFragment newInstance(String id, String taskId) {
        Bundle args = new Bundle();
        BTransFragment fragment = new BTransFragment();
        args.putString("id", id);
        args.putString("taskId", taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach(this);
        loading = new XPopup.Builder(mContext).dismissOnTouchOutside(false).dismissOnBackPressed(false).asLoading();
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            //int code = result.getResultCode();
            if (data != null) {
                printTemplateBean = new PrintTemplateBean();
                printTemplateBean.setTempId(data.getStringExtra("tempId"));
                printTemplateBean.setTempName(data.getStringExtra("tempName"));
                adapter.getEditTextSetValue("label", data.getStringExtra("tempName"));
                adapter.setFocusable(1);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (adapter != null) {
                adapter.setFocusable(adapter.getMarkPosition());
            }
        } else {
            if (adapter != null) {
                adapter.markFocus();
            }
        }
    }

    @Override
    public boolean enableEventBus() {
        return true;
    }

    @Override
    public void initData() {
        presenter.getScanConfigDetalisInfo(getArguments().getString("id"));
        getMBind().btnRepacking.setOnClickListener(this);
    }

    @Override
    public void initScanConfigItem(ScanConfigBean data) {
        bean = data;
        List<EntityBean> list = new ArrayList<>();
        if ("".equals(data.getTransferPackType()) || "1".equals(data.getTransferPackType())) {
            Objects.requireNonNull(getMBind()).btnScanConfirm.setVisibility(View.GONE);
        }
        list = PackConfig.getInstanceByTrans(data.getTransferPackType(), data.getTransferInPackCode()).getLocalScanConfigList();
        adapter = new TransAdapter();
        getMBind().rvScanList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.addChildClickViewIds(R.id.iv_base_info_query);
        getMBind().rvScanList.setAdapter(adapter);
        adapter.setList(list);
        adapter.setOnEditorActionChangeListener(this);
        adapter.setOnItemChildClickListener(this);
        getMBind().rvScanList.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ("in".equals(adapter.getKey(0))) {
                    presenter.setInBarcode(mContext, getArguments().getString("id"));
                } else {
                    adapter.setFocusable(0);
                }
            }
        }, 50);
    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void showSuccessAnalysisEvent(BarcodeDetailsInfoBean data, int pos) {
        if ("2".equals(bean.getTransferInPackCode())) {
            presenter.confirmEntry(mContext, "", adapter.getEditTextString(pos), getArguments().getString("id"));
        } else {
            if ("in".equals(adapter.getKey(pos))) {
                inBarcode = data;
            }
            if ("out".equals(adapter.getKey(pos))) {
                if (inBarcode == null) {
                    ToastUtil.showToastCenter("请选扫描转入箱码");
                    return;
                }
                //控制转出箱码不允许与转入箱码相同
                if (adapter.getEditTextGetValue("in").equals(data.getBarCode())) {
                    ToastUtil.showToastCenter("转出箱码不允许与转入箱码相同");
                    return;
                }
                if (!inBarcode.getBarCode().equals(adapter.getEditTextGetValue("in"))) {
                    ToastUtil.showToastCenter( "转入条码不正确，请重新扫描");
                    return;
                }
                //判断是否有转入箱码
                String inBarcode = null;
                if ("in".equals(adapter.getKey(pos - 1))) {
                    inBarcode = adapter.getEditTextString(pos - 1);
                }
                //控制转箱箱码不允许重复扫描
                //确认扫入
                presenter.confirmEntry(mContext, inBarcode, adapter.getEditTextString(pos), getArguments().getString("id"));

            }
        }

        adapter.setFocusable(pos + 1);
    }

    @Override
    public void showFailAnalysisEvent(int pos, String msg) {
        ToastUtil.showToastCenter( msg);
        if ("in".equals(adapter.getKey(pos))) {
            adapter.setSelection(pos);
            inBarcode = null;
        }
        if ("out".equals(adapter.getKey(pos))) {
            adapter.setSelection(pos);
        }
    }

    @Override
    public void showSuccessConfirmEntry() {
        //清空转出箱码
        adapter.getEditTextSetValue("out", "");
        adapter.setFocusable("out");
    }

    @Override
    public void showFailConfirmEntry(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void setInBarcode(String inBarcode) {
        adapter.getEditTextSetValue("in", inBarcode);
        this.inBarcode = new BarcodeDetailsInfoBean();
        this.inBarcode.setBarCode(inBarcode);
        adapter.setFocusable(1);
    }

    @Override
    public void setFocusable(int position) {
        adapter.setFocusable(position);
    }

    @Override
    public void showSuccessSubmit(String msg) {
        ToastUtil.showToastCenter( msg);
        clear();
    }

    @Override
    public void showLoadingView() {
        loading.show();
    }

    @Override
    public void hideLoadingView() {
        loading.dismiss();
    }

    @Override
    public void onEditorActionListener(@Nullable EditText view, @NonNull EntityBean bean, int position) {
        String id = getArguments().getString("id");
        String taskId = getArguments().getString("taskId");
        AnalysisReq req = new AnalysisReq(Integer.parseInt(taskId), id);
        switch (bean.getKey()) {
            case "in":
                if (TextUtils.isEmpty(view.getText().toString().trim())) {
                    ToastUtil.showToastCenter("请先扫描转入箱码");
                    return;
                }
                req.setCode(view.getText().toString().trim());
                req.setType("pack");
                req.setPackCodeType("in");
                presenter.analysisPackBarcodeByTransfer(mContext, req, position);
                break;
            case "out":
                if ("in".equals(adapter.getKey(position - 1)) && TextUtils.isEmpty(adapter.getEditTextString(position - 1))) {
                    ToastUtil.showToastCenter("请先扫描转入箱码");
                    return;
                }
                if (TextUtils.isEmpty(view.getText().toString().trim())) {
                    ToastUtil.showToastCenter( "请先扫描转出箱码");
                    return;
                }
                req.setCode(view.getText().toString().trim());
                req.setType("pack");
                req.setPackCodeType("out");
                presenter.analysisPackBarcodeByTransfer(mContext, req, position);
                break;
        }
    }

    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        if (view.getId() == R.id.iv_base_info_query) {
            Intent intent = new Intent(getActivity(), PrintTemplateListActivity.class);
            intent.putExtra("formId", URLPath.Trans.PATH_TRANS_FORM_ID);
            launcher.launch(intent);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_repacking) {
            if ("label".equals(adapter.getKey(0)) && printTemplateBean == null && TextUtils.isEmpty(adapter.getEditTextString(0))) {
                ToastUtil.showToastCenter("请先选择标签模板");
                return;
            }
            if ("label".equals(adapter.getKey(0)) && printTemplateBean == null && !TextUtils.isEmpty(adapter.getEditTextString(0))) {
                ToastUtil.showToastCenter("请选择正确的标签模板");
                return;
            }
            if ("label".equals(adapter.getKey(0)) && printTemplateBean != null && TextUtils.isEmpty(adapter.getEditTextString(0))) {
                ToastUtil.showToastCenter( "请先选择标签模板");
                return;
            }
            if ("label".equals(adapter.getKey(0)) && printTemplateBean != null && !printTemplateBean.getTempName().equals(adapter.getEditTextString(0))) {
                ToastUtil.showToastCenter("请选择正确的标签模板");
                return;
            }
            presenter.rePacking(mContext, getArguments().getString("id"), printTemplateBean == null ? "" : printTemplateBean.getTempId());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clearInterface(EmptyEvent event) {
        clear();
    }

    private void clear() {
        if (bean != null && "2".equals(bean.getTransferInPackCode())) {
            adapter.getEditTextSetValue("label", "");
            adapter.setFocusable("label");
            printTemplateBean = null;
        } else {
            adapter.getEditTextSetValue("in", "");
            adapter.setFocusable("in");
        }
        adapter.getEditTextSetValue("out", "");
        inBarcode = null;
    }

    @Override
    public void initObserve() {

    }
}
