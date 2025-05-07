package com.unionware.wms.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.tamsiree.rxkit.RxFileTool;
import com.tencent.mmkv.MMKV;
import com.unionware.printer.FileUtil;
import com.unionware.printer.PrintUtils;
import com.unionware.printer.print.PermissionUtils;
import com.unionware.printer.print.PrinterInterface;
import com.unionware.printer.print.ThreadPoolManager;
import com.unionware.wms.R;
import com.unionware.wms.URLPath;
import com.unionware.wms.app.RouterWMSPath;
import com.unionware.wms.databinding.BarcodeSplittingActivityBinding;
import com.unionware.wms.inter.box.SplitContract;
import com.unionware.wms.inter.box.SplitPresenter;
import com.unionware.wms.model.req.PageIdReq;
import com.unionware.wms.model.resp.BarcodesResp;
import com.unionware.wms.ui.adapter.EditTextAdapter;
import com.unionware.wms.ui.adapter.NormalBarcodeInfoContentAdapter;
import com.unionware.wms.ui.adapter.WMSScanAdapter;
import com.unionware.wms.ui.dialog.PrintTempPop;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.jvm.JvmField;
import unionware.base.app.utils.LoadingUtil;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;
import unionware.base.model.bean.PropertyBean;
import unionware.base.model.req.BarcodePrintExportReq;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.ItemBean;
import unionware.base.model.req.ViewReq;
import unionware.base.model.resp.AnalysisResp;

/**
 * @Author : pangming
 * @Time : On 2024/7/29 17:30
 * @Description : BarcodeSplittingActivity
 */
@AndroidEntryPoint
@Route(path = RouterWMSPath.WMS.PATH_WMS_BOX_SPLIT_MAIN)
public class BarcodeSplittingActivity extends BaseBindActivity<BarcodeSplittingActivityBinding> implements SplitContract.View, WMSScanAdapter.OnEditorActionChangeListener {
    @Inject
    SplitPresenter presenter;

    @JvmField
    @Autowired(name = "primaryId")
    String schemaId;

    @JvmField
    @Autowired(name = "scene")
    String scene;

    @JvmField
    @Autowired(name = "title")
    String title;

    @JvmField
    @Autowired(name = "formId")
    String formId;

    @JvmField
    @Autowired(name = "name")
    String name;

    @JvmField
    @Autowired(name = "splitType")
    String splitType;

    @JvmField
    @Autowired(name = "unpackAndTransfer")
    boolean unpackAndTransfer;

    @JvmField
    @Autowired(name = "primaryId")
    String allowPackCodeCreate;
    private ViewReq req;
    private WMSScanAdapter adapter;
    private String primaryId;
    private NormalBarcodeInfoContentAdapter barcodeInfoContentAdapter;
    private EditTextAdapter editTextAdapter;
    private ActivityResultLauncher<Intent> printLauncher;
    private LauncherCallback launcherCallback;
    private PrintTempPop printTempPop;

    private PrinterInterface printerInterface;
    private final PrinterInterface.PrintCallBack callBack = (msg, type) -> {
        switch (type) {
            case 0:
                setState(msg, Color.YELLOW);
                break;
            case 2:
                setState(msg, Color.GREEN);
                break;
            case 3:
                setState(msg, Color.RED);
                break;
        }
    };

    private void setState(String msg, int color) {
        this.runOnUiThread(() -> {
            getMBind().tvPrintState.setText(msg);
            getMBind().tvPrintState.setBackgroundColor(color);
        });
    }

    interface LauncherCallback {
        void onResult(Intent result);
    }

    @Override
    public int onBindLayout() {
        return R.layout.barcode_splitting_activity;
    }

    @Override
    public void initView() {
        LoadingUtil.init(this);
        presenter.attach(this);
        getMBind().icWmsToolbar.tbTitle.setText(title);
        getMBind().icWmsToolbar.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        adapter = new WMSScanAdapter();
        getMBind().rvEditList.setLayoutManager(new LinearLayoutManager(this));
        getMBind().rvEditList.setAdapter(adapter);
        adapter.setOnEditorActionChangeListener(this);
        adapter.addChildClickViewIds(R.id.tv_scan_lock);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.tv_scan_lock) {
                PropertyBean bean = this.adapter.getData().get(position);
                bean.setLock(!bean.isLock());
                adapter.notifyItemChanged(position);
            }
        });
        barcodeInfoContentAdapter = new NormalBarcodeInfoContentAdapter();
        getMBind().rvShowList.setLayoutManager(new LinearLayoutManager(this));
        getMBind().rvShowList.setAdapter(barcodeInfoContentAdapter);

        editTextAdapter = new EditTextAdapter();
        getMBind().rvSplitList.setLayoutManager(new LinearLayoutManager(this));
        getMBind().rvSplitList.setAdapter(editTextAdapter);

        getMBind().btnWmsScanSetting.setOnClickListener(v -> {
            printTempPop = new PrintTempPop(this, schemaId, new PrintTempPop.OnBtnListener() {
                @Override
                public void onConfirm() {
                    printTempPop.dismiss();
                }

                @Override
                public void onQuery(@NonNull String key) {
                    launcherCallback = result -> {
                        String tempId = result.getStringExtra("tempId");
                        String tempName = result.getStringExtra("tempName");
                        printTempPop.setData(key, tempName, tempId);
                    };
                    Intent intent = new Intent(BarcodeSplittingActivity.this, PrintTemplateListActivity.class);
                    intent.putExtra("formId", URLPath.BarcodeReprinting.PATH_BARCODEREPRINTING_FORM_ID);
                    printLauncher.launch(intent);
                }
            });
            new XPopup.Builder(this).maxHeight(mContext.getResources().getDisplayMetrics().heightPixels * 3 / 4).popupPosition(PopupPosition.Bottom).asCustom(printTempPop).show();
        });
        getMBind().btnWmsScanConfirm.setOnClickListener(v -> {
            if (TextUtils.isEmpty(getPrintTemp())) {
                showFailedView("未选择打印模板");
                getMBind().btnWmsScanSetting.performClick();
                return;
            }
            /*if (printerInterface == null || !printerInterface.isConnect()) {
                Toast.makeText(mContext, getString(R.string.conn_first), Toast.LENGTH_SHORT).show();
                return;
            }*/
            if (!PermissionUtils.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showFailedView("没有文件读取权限");
                return;
            }

            //INVOKE_SETSPLITQTY
            ViewReq viewReq = new ViewReq(req.getPageId());
            viewReq.setCommand("INVOKE_SETSPLITQTY");
            HashMap<String, Object> parmas = new HashMap<>();
            if ("Standard".equals(splitType)) {
                parmas.put("type", "Standard");
                parmas.put("count", adapter.getTextByKey("FSplitCount_Proxy"));
                if (!adapter.getTextByKey("FSplitQty_Proxy").isEmpty()) {
                    parmas.put("qty", adapter.getTextByKey("FSplitQty_Proxy"));
                } else {
                    showFailedView("没有填写标准数量");
                    return;
                }
            } else {//if ("Custom".equals(splitType)) {
                parmas.put("type", "Custom");
                parmas.put("qtys", editTextAdapter.getData());
            }
            viewReq.setParams(parmas);
            presenter.commandScanView(viewReq);
        });
        if ("Standard".equals(splitType)) {
            getMBind().rlSplit.setVisibility(View.GONE);
        } else {
            getMBind().rlSplit.setVisibility(View.VISIBLE);
        }

        presenter.getAnalysisLiveData().observe(this, analysisResp -> {
            // 数据更新渲染
            analysisResp.getFBillHead().get(0).forEach((key, value) -> adapter.setValue(key, value));
            analysisResp.getFBillHead().get(0).forEach((key, value) -> barcodeInfoContentAdapter.setValue(key, value));
            primaryId = analysisResp.getPrimaryId();
            AtomicInteger count = new AtomicInteger();
//            AtomicReference<Double> qty = new AtomicReference<>((double) 0);
            analysisResp.getFBillHead().get(0).entrySet().stream().filter(entry -> {
                if (entry.getKey() == null) {
                    return false;
                }
                return (entry.getKey().equals("FSplitCount_Proxy") || entry.getKey().equals("FBarQty_Proxy")) && !TextUtils.isEmpty(entry.getValue().getValue());
            }).forEach(stringBarcodeBeanEntry -> {
                if (stringBarcodeBeanEntry.getKey().equals("FSplitCount_Proxy")) {
                    count.set(new BigDecimal(stringBarcodeBeanEntry.getValue().getValue()).intValue());
                }
                /*else if (stringBarcodeBeanEntry.getKey().equals("FBarQty_Proxy")) {
                    qty.set(new BigDecimal(stringBarcodeBeanEntry.getValue().getValue()).doubleValue());
                }*/
            });
            if (count.get() > 0) {
                editTextAdapter.submitList(getEditTextList(count.get()));
            }
        });

        Map<String, Object> params = new HashMap<>();
        params.put("schemaId", schemaId);
        FiltersReq filtersReq = new FiltersReq(params);
        filtersReq.setPageIndex(1);
        filtersReq.setIndex(1);
        presenter.getBoxStateId(scene, name, filtersReq);

        presenter.getPrintData().observe(this, fileBase64 -> {
            if (printerInterface == null || !printerInterface.isConnect()) {
                ToastUtil.showToast("请先连接打印机！");
                dismissLoading();
                presenter.restartView(req.getPageId());
                return;
            }
            if (!TextUtils.isEmpty(fileBase64)) {
                ThreadPoolManager.getInstance().addTask(() -> {
                    FileUtil.SaveToPDF(fileBase64, this);
                    FileUtil.DeZip(this);
                    printPDF(RxFileTool.listFilesInDir(mContext.getFilesDir().toString() + "/pdf_android"));
                    runOnUiThread(() -> {
                        presenter.restartView(req.getPageId());
                        dismissLoading();
                    });
                });
            } else {
                dismissLoading();
                presenter.restartView(req.getPageId());
            }
        });
    }

    private void connectPrint() {
        try {
            printerInterface = PrintUtils.connectPrint(this, callBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        printLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData(); //int code = result.getResultCode();
            if (data != null && launcherCallback != null) {
                launcherCallback.onResult(data);
                data.getStringExtra("tempId");
                data.getStringExtra("tempName");
            }
        });
    }

    private List<String> getEditTextList(int num) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            list.add("");
        }
        return list;
    }

    @Override
    public void initData() {
        connectPrint();
    }

    @Override
    public void initPageId(String pageId) {
        req = new ViewReq(pageId);
        req.setParams(new HashMap<>());
    }

    @Override
    public void initStateId(String primaryId) {
        ViewReq req;
        Map<String, Object> params = new HashMap<>();
        req = new ViewReq(formId, params);
        if (null == primaryId) {
            //创建视图
            req.getParams().put("schemaId", schemaId);
        } else {
            //打开视图
            req.setPrimaryId(primaryId);
        }
        req.setCompact(true);
        presenter.getPageId(req);
    }

    @Override
    public boolean initScanItem(List<PropertyBean> editList, List<PropertyBean> showList) {
        if ("Standard".equals(splitType)) {
            PropertyBean propertyBean = new PropertyBean("FSplitQty_Proxy", "标准数量");
            propertyBean.setEnable(true);
            propertyBean.setType("DECIMAL");
            propertyBean.setValue("");
            editList.add(propertyBean);
            adapter.setNewInstance(editList);
        } else {
            adapter.setNewInstance(editList);
        }
        barcodeInfoContentAdapter.setNewInstance(showList);
        return true;
    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void onCompleteUpdateView(AnalysisResp data, int pos) {
        //光标下移
        adapter.focusMoveDown(pos);
    }

    @Override
    public void onSuccessSubmit(String tips, boolean isContainer) {

    }

    @Override
    public void errorUpdate(String tips, int pos) {
        showFailedView(tips);
        adapter.setEditTextValue(pos, "");
    }

    @Override
    public void onEditorActionListener(@Nullable EditText view, @NonNull PropertyBean bean, int position) {
        if ("FSplitQty_Proxy".equals(bean.getKey())) {
            return;
        }
        String val = view != null ? view.getText().toString().trim() : "";
        if (req != null) {
            req.setItems(Collections.singletonList(new ItemBean(bean.getKey(), val)));
            presenter.updateScanView(req, position);
        }
    }

    private void printPDF(List<File> files) {
        try {
            if (printerInterface != null) {
                printerInterface.print(files, 1);
                RxFileTool.delAllFile(mContext.getFilesDir().toString() + "/pdf_android");
            }
        } catch (Exception e) {
            showFailedView("打印报错" + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (printerInterface != null) {
            printerInterface.stopHeartBeat();
        }
        LoadingUtil.unInit();
        if (!MMKV.mmkvWithID(schemaId).decodeBool("isSave", false)) {
            MMKV.mmkvWithID(schemaId).clear();
        }
        //关闭数据视图
        if (req == null) {
            super.onBackPressed();
        } else {
            presenter.destroyScanView(new PageIdReq(this.req.getPageId()));
        }
    }

    @Override
    public void closeView() {
        getOnBackPressedDispatcher().onBackPressed();
    }

    @Override
    public void submitView(List<BarcodesResp> barcodes) {
        BarcodePrintExportReq barcodePrintExportReq = new BarcodePrintExportReq();
        List<Map<String, Object>> items = new ArrayList<>();
        barcodes.forEach(barcodesResp -> {
            Map<String, Object> item = new HashMap<>();
            item.put("code", barcodesResp.getBarCodeId());
            item.put("template", getPrintTemp());
            items.add(item);
        });
        barcodePrintExportReq.setItems(items);
        Map<String, Object> params = new HashMap<>();
        params.put("count", 1);
        params.put("template", getPrintTemp());
        params.put("type", "AABB");
        barcodePrintExportReq.setParams(params);
//        barcodePrintExportReq.setFormId(URLPath.BarcodeReprinting.PATH_BARCODEREPRINTING_FORM_ID);
        presenter.barcodePrintExportReq(scene, barcodePrintExportReq, req.getPageId());
    }

    @Override
    public void showLoading(String msg) {
        LoadingUtil.show(msg);
    }

    @Override
    public void dismissLoading() {
        LoadingUtil.dismiss();
    }

    /**
     * 获取打印模板
     * @return
     */
    public String getPrintTemp() {
        return MMKV.mmkvWithID(schemaId).decodeString("printTemp", "");
    }
}
