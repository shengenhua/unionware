package com.unionware.wms.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.unionware.wms.R;
import com.unionware.wms.URLPath;
import com.unionware.wms.databinding.RepackingFragmentBinding;
import com.unionware.wms.inter.trans.BDTransContract;
import com.unionware.wms.inter.trans.BDTransPresenter;
import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;

import unionware.base.app.utils.SoftKeyBoardUtils;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.model.bean.PrintTemplateBean;
import unionware.base.room.DatabaseProvider;
import unionware.base.room.ThreadTask;
import unionware.base.room.table.TransScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/6/8 19:25
 * @Description : BDTransFragment
 */
@AndroidEntryPoint
public class BDTransFragment extends BaseBindFragment<RepackingFragmentBinding> implements BDTransContract.View, ScanAdapter.OnEditorActionChangeListener, OnItemChildClickListener, View.OnClickListener {
    @Inject
    BDTransPresenter presenter;
    private TransAdapter adapter;
    private ScanConfigBean bean;

    private ActivityResultLauncher<Intent> launcher;
    private BarcodeDetailsInfoBean inBarcode = null;
    private BarcodeDetailsInfoBean detailData = null;

    private LoadingPopupView loading;
    private PrintTemplateBean printTemplateBean;

    public static BDTransFragment newInstance(String id, String taskId) {
        Bundle args = new Bundle();
        BDTransFragment fragment = new BDTransFragment();
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
        Objects.requireNonNull(getMBind()).btnRepacking.setOnClickListener(this);
        getMBind().btnScanConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_scan_confirm) {
            scanConfirm();
        } else if (view.getId() == R.id.btn_repacking) {
            if ("label".equals(adapter.getKey(0)) && printTemplateBean == null && TextUtils.isEmpty(adapter.getEditTextString(0))) {
                ToastUtil.showToastCenter( "请先选择标签模板");
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
                ToastUtil.showToastCenter( "请选择正确的标签模板");
                return;
            }
            presenter.rePacking(mContext, getArguments().getString("id"),printTemplateBean == null ? "" : printTemplateBean.getTempId());
        }
    }

    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        int id = view.getId();
        if (id == R.id.iv_base_info_query) {
            Intent intent = new Intent(getActivity(), PrintTemplateListActivity.class);
            intent.putExtra("formId", URLPath.Trans.PATH_TRANS_FORM_ID);
            launcher.launch(intent);
        } else if (id == R.id.tv_scan_lock) { // 锁定  //设置默认与锁定的操作是互斥
            EditText editText = getMBind().rvScanList.getChildAt(position).findViewById(R.id.et_scan_input); //获取当前文本输入框
            TextView textView = getMBind().rvScanList.getChildAt(position).findViewById(R.id.tv_scan_lock);
            EntityBean bean = (EntityBean) adapter.getData().get(position);
            boolean isLock = bean.isLock(); // 当前锁住状态
            bean.setLock(!isLock);
            textView.setText(!isLock ? "已锁定" : "锁定");

            if (textView.getText().toString().equals("已锁定")) {
                TextView defaultTextView = getMBind().rvScanList.getChildAt(position).findViewById(R.id.tv_scan_default);
                defaultTextView.setText(isLock ? "已默认" : "默认");
                bean.setDefalut(isLock);
            }

            requireViewFocusable(editText, isLock);
        } else if (id == R.id.tv_scan_default) { // 默认
            EditText editText = getMBind().rvScanList.getChildAt(position).findViewById(R.id.et_scan_input); //获取当前文本输入框
            TextView textView = getMBind().rvScanList.getChildAt(position).findViewById(R.id.tv_scan_default);
            EntityBean bean = (EntityBean) adapter.getData().get(position);
            boolean isDefault = bean.isDefalut(); // 当前默认状态
            bean.setDefalut(!isDefault);
            textView.setText(!isDefault ? "已默认" : "默认");

            if (textView.getText().toString().equals("已默认")) {
                TextView lockTextView = getMBind().rvScanList.getChildAt(position).findViewById(R.id.tv_scan_lock);
                lockTextView.setText(isDefault ? "已锁定" : "锁定");
                bean.setLock(isDefault);
            }


            requireViewFocusable(editText, !bean.isDefalut());
        }
    }

    private void requireViewFocusable(EditText view, boolean enable) {
        view.setEnabled(enable);
        view.setFocusable(enable);
        view.setFocusableInTouchMode(enable);

    }

    @Override
    public void initScanConfigItem(ScanConfigBean data) {
        bean = data;
        List<EntityBean> list = new ArrayList<>();
        list = PackConfig.getInstanceByTrans(data.getTransferPackType(), data.getTransferInPackCode()).getLocalScanConfigList();
        adapter = new TransAdapter();
        getMBind().rvScanList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter.addChildClickViewIds(R.id.iv_base_info_query, R.id.tv_scan_default, R.id.tv_scan_lock);
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
        ToastUtil.showToastCenter( msg);
    }

    @Override
    public void showSuccessAnalysisEvent(BarcodeDetailsInfoBean data, int pos) {
        switch (adapter.getKey(pos)) {
            case "in":
                inBarcode = data;
                adapter.setFocusable(pos + 1);
                break;
            case "details":
                if ("PackageBarCode".equals(data.getType())) {
                    List<TransScanInfo> transScanInfos = ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao()
                            .queryByCodeAndDetailCode(getArguments().getString("id"), data.getBarCode()));
                    if (transScanInfos.size() > 0) {
                        ToastUtil.showToastCenter( "条码类型为包装条码,不允许重复扫描");
                    } else {
                        detailData = data;
                        adapter.getEditTextSetValue("detailsQty", data.getQty());
                        if (adapter.getItem(pos + 1).isLock() || adapter.getItem(pos + 1).isDefalut()) {
                            Objects.requireNonNull(getMBind()).rvScanList.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    scanConfirm();
                                }
                            }, 500);
                        } else {
                            adapter.setFocusable(pos + 1);
                        }
                    }
                } else {
                    //如果属于明细条码，则需按拆箱配置参数【明细条码重复扫描】的值进行区分控制
                    if (!"2".equals(bean.getDetailRepeatScanT()) && !"3".equals(bean.getDetailRepeatScanT())) {
                        detailData = data;
                        adapter.getEditTextSetValue("detailsQty", data.getQty());
                        if (adapter.getItem(pos + 1).isLock() || adapter.getItem(pos + 1).isDefalut()) {
                            Objects.requireNonNull(getMBind()).rvScanList.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    scanConfirm();
                                }
                            }, 500);
                        } else {
                            adapter.setFocusable(pos + 1);
                        }
                    }
                    switch (bean.getDetailRepeatScanT()) {
                        case "2":
                            List<TransScanInfo> transScanInfos = ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao()
                                    .queryByCodeAndDetailCode(getArguments().getString("id"), data.getBarCode()));
                            if (transScanInfos.size() > 0) {
                                new XPopup.Builder(getContext()).asConfirm("标题", "当前子项条码重复扫描，确认是否继续？",
                                                new OnConfirmListener() {
                                                    @Override
                                                    public void onConfirm() {
                                                        detailData = data;
                                                        adapter.getEditTextSetValue("detailsQty", data.getQty());
                                                        if (adapter.getItem(pos + 1).isLock() || adapter.getItem(pos + 1).isDefalut()) {
                                                            Objects.requireNonNull(getMBind()).rvScanList.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    scanConfirm();
                                                                }
                                                            }, 500);
                                                        } else {
                                                            adapter.setFocusable(pos + 1);
                                                        }
                                                    }
                                                }, new OnCancelListener() {
                                                    @Override
                                                    public void onCancel() {
                                                        adapter.getEditTextSetValue("details", "");
                                                    }
                                                })
                                        .show();
                            } else {
                                detailData = data;
                                adapter.getEditTextSetValue("detailsQty", data.getQty());
                                if (adapter.getItem(pos + 1).isLock() || adapter.getItem(pos + 1).isDefalut()) {
                                    Objects.requireNonNull(getMBind()).rvScanList.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            scanConfirm();
                                        }
                                    }, 500);
                                } else {
                                    adapter.setFocusable(pos + 1);
                                }
                            }
                            break;
                        case "3":
                            List<TransScanInfo> transScanInfos2 = ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao()
                                    .queryByCodeAndDetailCode(getArguments().getString("id"), data.getBarCode()));
                            if (transScanInfos2.size() > 0) {
                                ToastUtil.showToastCenter( "扫描失败，当前子项条码重复扫描！");
                                adapter.getEditTextSetValue("details", "");
                            } else {
                                detailData = data;
                                adapter.getEditTextSetValue("detailsQty", data.getQty());
                                if (adapter.getItem(pos + 1).isLock() || adapter.getItem(pos + 1).isDefalut()) {
                                    Objects.requireNonNull(getMBind()).rvScanList.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            scanConfirm();
                                        }
                                    }, 500);
                                } else {
                                    adapter.setFocusable(pos + 1);
                                }
                            }
                            break;
                    }

                }
                break;
            case "detailsQty":
                scanConfirm();
                break;
        }
    }

    @Override
    public void showFailAnalysisEvent(int pos, String msg) {
        ToastUtil.showToastCenter(msg);
        if ("in".equals(adapter.getKey(pos))) {
            adapter.setSelection(pos);
            inBarcode = null;
        }
        if ("details".equals(adapter.getKey(pos))) {
            adapter.setSelection(pos);
            detailData = null;
        }
    }

    @Override
    public void showSuccessConfirmEntry() {
        //清空
        detailData = null;
        adapter.getEditTextSetValue("details", "");
        //若子项条码数量未勾选默认，则也需清空
        adapter.getEditTextSetValue("detailsQty", "");
        Objects.requireNonNull(getMBind()).rvScanList.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setFocusable("details");
            }
        }, 100);
    }

    @Override
    public void showFailConfirmEntry(String msg) {
        ToastUtil.showToastCenter( msg);
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
                    ToastUtil.showToastCenter( "请先扫描转入箱码");
                    return;
                }
                req.setCode(view.getText().toString().trim());
                req.setType("pack");
                req.setPackCodeType("in");
                presenter.analysisPackBarcodeByTransfer(mContext, req, position);
                break;
            case "details":
                if ("in".equals(adapter.getKey(position - 1)) && TextUtils.isEmpty(adapter.getEditTextString(position - 1))) {
                    ToastUtil.showToastCenter( "请先扫描转入箱码");
                    return;
                }
                if (TextUtils.isEmpty(view.getText().toString().trim())) {
                    ToastUtil.showToastCenter( "请先扫描子项条码");
                    return;
                }
                req.setCode(view.getText().toString().trim());
                req.setPackcode(adapter.getEditTextString(position - 1));
                req.setType("detail");
                presenter.analysisPackBarcodeByTransfer(mContext, req, position);
                break;
            case "detailsQty":
                SoftKeyBoardUtils.hideSoftKeyBoard(mContext,adapter.getEditTextByKey("detailsQty"));
                scanConfirm();
                break;
        }
    }

    public void scanConfirm() {
//        String msg = adapter.isEmpty();
//        if (!TextUtils.isEmpty(msg)) {
//            Toast.makeText(mContext, msg + "不为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (!"2".equals(bean.getTransferInPackCode())) {
            if (inBarcode == null && TextUtils.isEmpty(adapter.getEditTextGetValue("in"))) {
                ToastUtil.showToastCenter( "请扫描转入箱码");
                return;
            }
            if (inBarcode == null && !TextUtils.isEmpty(adapter.getEditTextGetValue("in"))) {
                ToastUtil.showToastCenter( "转入箱码请回车做条码解析");
                return;
            }
            if (!inBarcode.getBarCode().equals(adapter.getEditTextGetValue("in"))) {
                ToastUtil.showToastCenter("转入箱码不正确，请重新扫描");
                return;
            }
        }
        if (detailData == null && TextUtils.isEmpty(adapter.getEditTextGetValue("details"))) {
            ToastUtil.showToastCenter("请扫描子项条码");
            return;
        }
        if (detailData == null && !TextUtils.isEmpty(adapter.getEditTextGetValue("details"))) {
            ToastUtil.showToastCenter( "子项条码请回车做条码解析");
            return;
        }
        if (!detailData.getBarCode().equals(adapter.getEditTextGetValue("details"))) {
            ToastUtil.showToastCenter("子项条码不正确，请重新扫描");
            return;
        }
        if (TextUtils.isEmpty(adapter.getEditTextGetValue("detailsQty"))) {
            ToastUtil.showToastCenter( "数量不能为空");
            return;
        }
        //校验数量上限不允许超过解析的数量
        BigDecimal qty = new BigDecimal(detailData.getQty());
        BigDecimal detailsQty = new BigDecimal(adapter.getEditTextGetValue("detailsQty"));
        //数量仅允许录入大于0的数字
        if (detailsQty.doubleValue() < 0) {
            ToastUtil.showToastCenter("数量仅允许录入大于0的数字");
            return;
        }
        if (qty.subtract(detailsQty).doubleValue() < 0) {
            ToastUtil.showToastCenter("解析返回的数量是" + detailData.getQty() + ",设置的子项条码数量不能大于");
            return;
        }
        TransScanInfo transScanInfo = new TransScanInfo();
        transScanInfo.setInternalCodeId(getArguments().getString("id"));
        if (!"2".equals(bean.getTransferInPackCode())) {
            transScanInfo.setInBarcode(inBarcode.getBarCode());
        } else {
            transScanInfo.setInBarcode("");
        }
        transScanInfo.setDetailCode(detailData.getBarCode());
        transScanInfo.setDetailCodeType(detailData.getType());
        transScanInfo.setDetailQty(adapter.getEditTextGetValue("detailsQty"));
        presenter.confirmEntry(mContext, transScanInfo);

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
        adapter.getEditTextSetValue("details", "");
        adapter.getEditTextSetValue("detailsQty", "");
        inBarcode = null;
        detailData = null;
    }

    @Override
    public void initObserve() {

    }
}
