package com.unionware.wms.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.unionware.wms.databinding.CommonScanFragmentBinding;
import com.unionware.wms.inter.unpack.UnPackContract;
import com.unionware.wms.inter.unpack.UnPackingPresenter;
import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;
import com.unionware.wms.model.bean.BoxPackingsBean;

import unionware.base.app.utils.SoftKeyBoardUtils;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.model.bean.EntityBean;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.AnalysisReq;
import com.unionware.wms.model.req.PackingsReq;
import com.unionware.wms.strategy.PackConfig;
import com.unionware.wms.ui.adapter.ScanAdapter;
import com.unionware.wms.ui.adapter.UnpackingAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.room.DatabaseProvider;
import unionware.base.room.ThreadTask;
import unionware.base.room.table.TransScanInfo;
import unionware.base.room.table.UnPackScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/5/30 10:29
 * @Description : UnpackingFragment
 */

@AndroidEntryPoint
public class UnpackingFragment extends BaseBindFragment<CommonScanFragmentBinding> implements UnPackContract.View, ScanAdapter.OnEditorActionChangeListener, OnItemChildClickListener {

    @Inject
    UnPackingPresenter presenter;
    private UnpackingAdapter adapter;
    private AnalysisReq req; // 条码解析请求

    private ScanConfigBean bean;

    private BarcodeDetailsInfoBean packageData;

    private BarcodeDetailsInfoBean detailData;

    private LoadingPopupView loading;

    public static UnpackingFragment newInstance(String id, String taskId) {
        Bundle args = new Bundle();
        UnpackingFragment fragment = new UnpackingFragment();
        args.putString("id", id);
        args.putString("taskId", taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onEditorActionListener(@Nullable EditText editText, @NonNull EntityBean bean, int position) {
        String id = getArguments().getString("id");
        String taskId = getArguments().getString("taskId");
        req = new AnalysisReq(Integer.parseInt(taskId), id);
        switch (bean.getKey()) {
            case "package":
                switch (this.bean.getUnPackType()) {
                    case "1":
                        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                            ToastUtil.showToastCenter("请扫描包装条码");
                            return;
                        }
                        req.setCode(editText.getText().toString().trim());
                        req.setType("pack");
                        presenter.analysisPackBarcodeByBPUnpacking(req, position);
                        break;
                    case "2":
                        break;
                }

                break;

            case "details":
                switch (this.bean.getUnPackType()) {
                    case "1":
                        if (TextUtils.isEmpty(adapter.getEditTextString(position - 1))) {
                            ToastUtil.showToastCenter("请扫描包装条码");
                            return;
                        }
                        if (TextUtils.isEmpty(adapter.getEditTextString(position))) {
                            ToastUtil.showToastCenter("请扫描子项条码");
                            return;
                        }
                        req.setCode(editText.getText().toString().trim());
                        req.setPackcode(adapter.getEditTextString(position - 1));
                        req.setType("detail");
                        presenter.analysisPackBarcodeByBPUnpacking(req, position);
                        break;
                    case "2":
                        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                            ToastUtil.showToastCenter( "请扫描子项条码");
                            return;
                        }
                        req.setCode(editText.getText().toString().trim());
                        req.setType("detail");
                        presenter.analysisPackBarcodeByBPUnpacking(req, position);
                        break;
                }
                break;
            default:
                SoftKeyBoardUtils.hideSoftKeyBoard(mContext,adapter.getEditTextByKey("detailsQty"));
                scanConfirm();
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach(this);
        loading = new XPopup.Builder(mContext).dismissOnTouchOutside(false).dismissOnBackPressed(false).asLoading();
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
    public void initScanConfigItem(ScanConfigBean data) {
        bean = data;
        Objects.requireNonNull(getMBind()).btnScanClose.setText("拆箱");
        //区别按箱指定子项拆箱-箱码，按子项拆箱-子项条码
        switch (data.getUnPackType()) {
            case "1"://按箱指定子项拆箱
                Objects.requireNonNull(getMBind()).btnScanChange.setText("清箱");
                break;
            case "2"://按子项拆箱
                Objects.requireNonNull(getMBind()).btnScanChange.setVisibility(View.INVISIBLE);
                break;
        }
        List<EntityBean> list = PackConfig.getInstance(data.getOperType(), data.getPackType(), data.getUnPackType()).getLocalScanConfigList();
        for (EntityBean bean : data.getEntity()) {
            if (bean.getKey() == null) continue;
            list.add(bean);
        }
        adapter = new UnpackingAdapter();
        adapter.addChildClickViewIds(R.id.tv_scan_default, R.id.tv_scan_lock);
        getMBind().rvScanList.setLayoutManager(new LinearLayoutManager(getActivity()));
        getMBind().rvScanList.setAdapter(adapter);
        adapter.setList(list);
        adapter.setOnEditorActionChangeListener(this);
        adapter.setOnItemChildClickListener(this);
        getMBind().rvScanList.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setFocusable(0);
                if ("package".equals(adapter.getKey(0))) {
                    presenter.setPackCode(mContext, getArguments().getString("id"));
                }
            }
        }, 50);

    }


    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter( msg);
    }

    @Override
    public void showFailAnalysisEvent(int pos, String msg) {
        if ("package".equals(adapter.getKey(pos))) {
            packageData = null;
            adapter.setSelection(pos);
        }
        if ("details".equals(adapter.getKey(pos))) {
            detailData = null;
            adapter.setSelection(pos);
        }
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void showSuccessAnalysisEvent(BarcodeDetailsInfoBean data, int pos) {
        switch (bean.getUnPackType()) {
            case "1":
                if ("package".equals(adapter.getKey(pos))) {
                    List<UnPackScanInfo> unPackScanInfos = ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getUnPackScanInfoDao()
                            .queryByCode(getArguments().getString("id")));
                    if (unPackScanInfos.size() > 0 && !unPackScanInfos.get(0).getPackCode().equals(data.getBarCode())) {
                        new XPopup.Builder(getContext()).asConfirm("标题", "存在正在拆箱的包装条码，是否清空并更换？",
                                        new OnConfirmListener() {
                                            @Override
                                            public void onConfirm() {
                                                clearAndReplace(data, data.getBarCode());
                                                adapter.setFocusable(pos + 1);
                                            }
                                        })
                                .show();
                        return;
                    }
                    packageData = data;
                    adapter.setFocusable(pos + 1);
                }
                checkByDetailBarcode(data, pos);
                break;
            case "2":
                checkByDetailBarcode(data, pos);
                break;
        }
    }


    @Override
    public void initData() {
        presenter.getScanConfigDetalisInfo(getArguments().getString("id"));
        Objects.requireNonNull(getMBind()).btnScanConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanConfirm();
            }
        });
        Objects.requireNonNull(getMBind()).btnScanChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(adapter.getEditTextGetValue("package"))) {
                    ToastUtil.showToastCenter( "请扫描包装条码");
                    return;
                }
                switch (bean.getUnPackType()) {
                    case "1":
                        new XPopup.Builder(getContext()).asConfirm("标题", "清箱操作将删除此包装条码对应的条码拆装箱单据数据，请确认是否继续？",
                                        new OnConfirmListener() {
                                            @Override
                                            public void onConfirm() {
                                                PackingsReq packingsReq = new PackingsReq();
                                                List<BoxPackingsBean> packingsList = new ArrayList<>();
                                                BoxPackingsBean packings = new BoxPackingsBean();
                                                packings.setPackCode(adapter.getEditTextGetValue("package"));
                                                packingsList.add(packings);
                                                packingsReq.setPackings(packingsList);
                                                packingsReq.setSetId(getArguments().getString("id"));
                                                packingsReq.setType("clearPack");
                                                presenter.clearPack(mContext, "1", "清箱", packingsReq);
                                            }
                                        })
                                .show();
                        break;
                    case "2":
                        break;
                }
            }
        });
        Objects.requireNonNull(getMBind()).btnScanClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //后台拆箱处理成功后，PDA自动清空拆箱扫描界面、已扫列表数据
                switch (bean.getUnPackType()) {
                    case "1":
                        presenter.unPack(mContext, getArguments().getString("id"));
                        break;
                    case "2":
                        presenter.unPack(mContext, getArguments().getString("id"));
                        break;
                }
            }
        });
    }


    @Override
    public void showFailUnPackEvent(String msg) {
        ToastUtil.showToastCenter( msg);
    }

    @Override
    public void showSuccessUnPackEvent() {
        ToastUtil.showToastCenter( "拆箱成功");
        switch (bean.getUnPackType()) {
            case "1":
                //后台拆箱处理成功后，PDA自动清空拆箱扫描界面、已扫列表数据。
                packageData = null;
                detailData = null;
                adapter.getEditTextSetValue("package", "");
                adapter.getEditTextSetValue("details", "");
                //若子项条码数量未勾选默认，则也需清空
                adapter.getEditTextSetValue("detailsQty", "");
                adapter.setFocusable("package");
                break;
            case "2":
                detailData = null;
                adapter.getEditTextSetValue("details", "");
                //若子项条码数量未勾选默认，则也需清空
                adapter.getEditTextSetValue("detailsQty", "");
                adapter.setFocusable("details");
                break;
        }
    }

    @Override
    public void showSuccessconfirmEntry() {
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
    public void showSuccessClearPackEvent() {
        packageData = null;
        detailData = null;
        adapter.getEditTextSetValue("package", "");
        adapter.getEditTextSetValue("details", "");
        //若子项条码数量未勾选默认，则也需清空
        adapter.getEditTextSetValue("detailsQty", "");
        adapter.setFocusable("package");
    }

    @Override
    public void showFailClearPackEvent(String msg) {
        ToastUtil.showToastCenter( msg);
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
    public void showPackCode(String packCode) {
        adapter.getEditTextSetValue("package", packCode);
        this.packageData = new BarcodeDetailsInfoBean();
        this.packageData.setBarCode(packCode);
        adapter.setFocusable(1);
    }

    public void scanConfirm() {
        //PDA端在做必填、值规范性检查后将界面数据提交到【查看已扫】中，并清空子项条码，若子项条码数量未勾选默认，则也需清空。
        //校验数据是否都填了
        String msg = adapter.isEmpty();
        if (!TextUtils.isEmpty(msg)) {
            ToastUtil.showToastCenter( msg + "不为空");
            return;
        }
        if (detailData == null && TextUtils.isEmpty(adapter.getEditTextGetValue("details"))) {
            ToastUtil.showToastCenter( "请扫描子项条码");
            return;
        }
        if (detailData == null && !TextUtils.isEmpty(adapter.getEditTextGetValue("details"))) {
            ToastUtil.showToastCenter( "子项条码请回车做条码解析");
            return;
        }
        if (!detailData.getBarCode().equals(adapter.getEditTextGetValue("details"))) {
            ToastUtil.showToastCenter( "子项条码不正确，请重新扫描");
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
        switch (bean.getUnPackType()) {
            case "1":
                if (packageData == null) {
                    ToastUtil.showToastCenter( "包装条码请回车做条码解析");
                    return;
                }
                if (!packageData.getBarCode().equals(adapter.getEditTextGetValue("package"))) {
                    ToastUtil.showToastCenter( "包装条码不正确，请重新扫描");
                    return;
                }
                //录入
                UnPackScanInfo unPackScanInfo = new UnPackScanInfo();
                unPackScanInfo.setPackCode(packageData.getBarCode());
                unPackScanInfo.setDetailCode(detailData.getBarCode());
                unPackScanInfo.setDetailQty(adapter.getEditTextGetValue("detailsQty"));
                unPackScanInfo.setInternalCodeId(getArguments().getString("id"));
                presenter.confirmEntry(mContext, unPackScanInfo);
                break;
            case "2":
                UnPackScanInfo unPackScanInfo2 = new UnPackScanInfo();
                unPackScanInfo2.setPackCode("");
                unPackScanInfo2.setDetailCode(detailData.getBarCode());
                unPackScanInfo2.setDetailQty(adapter.getEditTextGetValue("detailsQty"));
                unPackScanInfo2.setInternalCodeId(getArguments().getString("id"));
                presenter.confirmEntry(mContext, unPackScanInfo2);
                break;
        }
    }

    private void clearAndReplace(BarcodeDetailsInfoBean data, String nowPackCode) {
        ThreadTask.start(() -> DatabaseProvider.getInstance().getUnPackScanInfoDao()
                .deleteList(getArguments().getString("id")));
        adapter.getEditTextSetValue("package", nowPackCode);
        adapter.getEditTextSetValue("details", "");
        adapter.getEditTextSetValue("detailsQty", "");
        packageData = data;
    }

    private void checkByDetailBarcode(BarcodeDetailsInfoBean data, int pos) {
        if ("details".equals(adapter.getKey(pos))) {
            //如果当前扫描的子项条码的条码类型为包装条码，则严格控制不允许重复扫描
            if ("PackageBarCode".equals(data.getType())) {
                List<UnPackScanInfo> unPackScanInfos = ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getUnPackScanInfoDao()
                        .queryByDetailCode(data.getBarCode()));
                if (unPackScanInfos.size() > 0) {
                    ToastUtil.showToastCenter( "条码类型为包装条码,不允许重复扫描");
                } else {
                    detailData = data;
                    adapter.getEditTextSetValue("detailsQty", data.getQty());
                    adapter.setFocusable(pos + 1);
                    if (adapter.getItem(pos + 1).isLock() || adapter.getItem(pos + 1).isDefalut()) {
                        Objects.requireNonNull(getMBind()).rvScanList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scanConfirm();
                            }
                        }, 500);
                    }
                }
            } else {
                //如果属于明细条码，则需按拆箱配置参数【明细条码重复扫描】的值进行区分控制
                if (!"2".equals(bean.getDetailRepeatScanU()) && !"3".equals(bean.getDetailRepeatScanU())) {
                    detailData = data;
                    adapter.getEditTextSetValue("detailsQty", data.getQty());
                    adapter.setFocusable(pos + 1);
                    if (adapter.getItem(pos + 1).isLock() || adapter.getItem(pos + 1).isDefalut()) {
                        Objects.requireNonNull(getMBind()).rvScanList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scanConfirm();
                            }
                        }, 500);
                    }
                }
                switch (bean.getDetailRepeatScanU()) {
                    case "2":
                        List<UnPackScanInfo> unPackScanInfos = ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getUnPackScanInfoDao()
                                .queryByCodeADc(getArguments().getString("id"),data.getBarCode()));
                        if (unPackScanInfos.size() > 0) {
                            new XPopup.Builder(getContext()).asConfirm("标题", "当前子项条码重复扫描，确认是否继续？",
                                            new OnConfirmListener() {
                                                @Override
                                                public void onConfirm() {
                                                    detailData = data;
                                                    adapter.getEditTextSetValue("detailsQty", data.getQty());
                                                    adapter.setFocusable(pos + 1);
                                                    if (adapter.getItem(pos + 1).isLock() || adapter.getItem(pos + 1).isDefalut()) {
                                                        Objects.requireNonNull(getMBind()).rvScanList.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                scanConfirm();
                                                            }
                                                        }, 500);
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
                            adapter.setFocusable(pos + 1);
                            if (adapter.getItem(pos + 1).isLock() || adapter.getItem(pos + 1).isDefalut()) {
                                Objects.requireNonNull(getMBind()).rvScanList.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        scanConfirm();
                                    }
                                }, 500);
                            }
                        }
                        break;
                    case "3":
                        List<UnPackScanInfo> unPackScanInfos2 = ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getUnPackScanInfoDao()
                                .queryByCodeADc(getArguments().getString("id"),data.getBarCode()));
                        if (unPackScanInfos2.size() > 0) {
                            ToastUtil.showToastCenter( "扫描失败，当前子项条码重复扫描！");
                            adapter.getEditTextSetValue("details", "");
                        } else {
                            detailData = data;
                            adapter.getEditTextSetValue("detailsQty", data.getQty());
                            adapter.setFocusable(pos + 1);
                            if (adapter.getItem(pos + 1).isLock() || adapter.getItem(pos + 1).isDefalut()) {
                                Objects.requireNonNull(getMBind()).rvScanList.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        scanConfirm();
                                    }
                                }, 500);
                            }
                        }
                        break;
                }

            }

        }
    }

    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        int id = view.getId();
        //设置默认与锁定的操作是互斥
        if (id == R.id.tv_scan_lock) { // 锁定
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
            // Log.e("onItemChildClick","锁定-->requireViewFocusable isLock="+isLock);
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


            requireViewFocusable(editText, isDefault);
            //Log.e("onItemChildClick","默认-->requireViewFocusable isDefault="+isDefault);
        }
    }

    private void requireViewFocusable(EditText view, boolean enable) {
        view.setEnabled(enable);
        view.setFocusable(enable);
        view.setFocusableInTouchMode(enable);

    }

    @Override
    public void initObserve() {

    }
}
