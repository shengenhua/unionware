package com.unionware.wms.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
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
import com.unionware.wms.command.CommandInvoke;
import com.unionware.wms.databinding.WmsBoxScanActivityBinding;
import com.unionware.wms.inter.box.WMSBoxContract;
import com.unionware.wms.inter.box.WMSBoxPresenter;
import com.unionware.wms.model.req.PageIdReq;
import com.unionware.wms.ui.adapter.WMSScanAdapter;
import com.unionware.wms.ui.dialog.DefaultValuePop;
import com.unionware.wms.ui.dialog.NewCodePop;
import com.unionware.wms.utlis.CommonUtils;
import com.unionware.wms.utlis.DataTypeUtilsKt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.jvm.JvmField;
import unionware.base.app.utils.DateFormatUtils;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.utils.sound.SoundUtils;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;
import unionware.base.model.bean.BaseInfoBean;
import unionware.base.model.bean.PropertyBean;
import unionware.base.model.req.BarcodePrintExportReq;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.ItemBean;
import unionware.base.model.req.ViewReq;
import unionware.base.model.resp.ActionResp;
import unionware.base.model.resp.AnalysisResp;
import unionware.base.room.DatabaseProvider;
import unionware.base.room.ThreadTask;
import unionware.base.room.table.DefaultKey;
import unionware.base.room.table.DefaultValueInfo;
import unionware.base.ui.datepicker.CustomDatePicker;

/**
 * 新版WMS条码装拆箱
 */
@AndroidEntryPoint
@Route(path = RouterWMSPath.WMS.PATH_WMS_BOX_MAIN)
public class WMSBoxActivity extends BaseBindActivity<WmsBoxScanActivityBinding> implements WMSBoxContract.View, WMSScanAdapter.OnEditorActionChangeListener, View.OnClickListener {
    @Inject
    WMSBoxPresenter presenter;

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
    @Autowired(name = "unpackAndTransfer")
    boolean unpackAndTransfer;

    @JvmField
    @Autowired(name = "allowPackCodeCreate")
    String allowPackCodeCreate;
    private LoadingPopupView loading;

    private WMSScanAdapter adapter;

    private ViewReq req;
    private String primaryId;

    private ActivityResultLauncher<Intent> launcher;

    private Packing packing;
    private Optional<PropertyBean> newCodeOptional;
    private DefaultValuePop defaultValuePop;
    private List<PropertyBean> originalList;//保存进来的原始数据 有2种情况 一种是没数据的情况，一种是保存上次记录的情况
    private List<PropertyBean> analysisList;//每次更新后值后，更新
    private boolean isOpen = true;//打开界面
    private PrinterInterface printerInterface;
    private PrinterInterface.PrintCallBack callBack = (msg, type) -> {
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
        runOnUiThread(() -> {
            getMBind().tvPrintState.setText(msg);
            getMBind().tvPrintState.setBackgroundColor(color);
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
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void onCompleteUpdateView(AnalysisResp data, int pos) {
        if (pos == -200) {//确定前更新文本和数量字段
            confirmBarCode();
        } else {
            if (formId.equals(URLPath.WMS.MENU_WMS_UNPACKING_FROM_ID)) {
                if (isAutomaticConfirmation(pos)) {
                    confirmBarCode();
                } else {
                    //如果是包装条码扫描成功，条码数量锁定不可放开
                    adapter.focusMoveDown(pos);
                }
            } else if (formId.equals(URLPath.WMS.MENU_WMS_PACKING_FROM_ID)) {
                if (isAutomaticConfirmation(pos)) {
                    confirmBarCode();
                } else {
                    adapter.focusMoveDown(pos);
                }
            }
        }

    }

    private void confirmBarCode() {
        List<ItemBean> list = getUpDataItemByText();
        if (list.size() > 0) {
            req.setItems(list);
            presenter.updateScanView(req, -200);
        } else {
            presenter.confirmBarCode(
                    new ViewReq(CommandInvoke.Pack.WMS_CONFIRM_BARCODE, this.req.getPageId()),
                    packing.confirmBarCodePos(adapter));
        }
    }

    private boolean isAutomaticConfirmation(int pos) {
        if (pos != -1 && adapter.getData().get(pos).getKey().equals("FBarCodeId_Proxy") && pos + 1 == adapter.getData().size() - 1
                && adapter.getData().get(pos + 1).getKey().equals("FBarQty_Proxy") && adapter.getData().get(pos + 1).isLock()) {
            //如果是拆箱，扫描明细条码下，一个是锁定的条码数量，自动确定
            return true;
        } else if (pos == adapter.getData().size() - 1) {
            return true;
        }
        return false;
    }

    private boolean updateDefault() {
        MMKV kv = MMKV.mmkvWithID("app");
        DefaultKey defaultKey = ThreadTask.getTwo(() -> {
            List<DefaultKey> defaultKeys = DatabaseProvider.getInstance().getDefaultKeyDao().queryKey(
                    kv.decodeString("userId", ""),
                    kv.decodeString("dbId", ""),
                    formId + "_" + schemaId);
            if (defaultKeys.isEmpty()) {
                return defaultKeys.get(0);
            }
            return null;
        });

        List<DefaultValueInfo> defaultValueInfoList = ThreadTask.getTwo(() -> {
            if (defaultKey != null) {
                return DatabaseProvider.getInstance().getDefaultValueInfoDao().queryByKey(String.valueOf(defaultKey.getDefaultKey()));
            }
            return null;
        });
        //开启默认  2种情况 1.当前修改  2.开启下次作业
        //当前修改
        if (defaultKey != null && defaultValueInfoList != null && defaultValueInfoList.size() > 0 && req != null) {
            List<ItemBean> list = getUpDataItemByText();
            list.addAll(defaultValueInfoList
                    .stream()
                    .filter(propertyBean -> propertyBean.getValue() != null && !propertyBean.getValue().isEmpty())
                    .map(defaultValueInfo -> new ItemBean(defaultValueInfo.getKey(), defaultValueInfo.getValue()))
                    .collect(Collectors.toList()));
            req.setItems(list);
            presenter.updateScanView(req, -1);
            return true;
        }
        return false;
    }

    private boolean isDefault() {
        MMKV kv = MMKV.mmkvWithID("app");

        DefaultKey defaultKey = ThreadTask.getTwo(() -> {
            List<DefaultKey> defaultKeys = DatabaseProvider.getInstance().getDefaultKeyDao().queryKey(
                    kv.decodeString("userId", ""),
                    kv.decodeString("dbId", ""),
                    formId + "_" + schemaId);
            if (defaultKeys.isEmpty()) {
                return defaultKeys.get(0);
            }
            return null;
        });
        /*DefaultKey defaultKey = ManagerFactory.getInstance(this).getDefaultManager().queryKey(
                kv.decodeString("userId", ""),
                kv.decodeString("dbId", ""),
                formId + "_" + schemaId);*/
        //开启默认  2种情况 1.当前修改  2.开启下次作业
        //开启下次作业
        if (defaultKey != null && defaultKey.isDefault() && req != null) {
            return true;
        }
        return false;
    }

    private void initDefault() {
        MMKV kv = MMKV.mmkvWithID("app");
        DefaultKey defaultKey = ThreadTask.getTwo(() -> {
            List<DefaultKey> defaultKeys = DatabaseProvider.getInstance().getDefaultKeyDao().queryKey(
                    kv.decodeString("userId", ""),
                    kv.decodeString("dbId", ""),
                    formId + "_" + schemaId);
            if (defaultKeys.isEmpty()) {
                return defaultKeys.get(0);
            }
            return null;
        });
        if (defaultKey != null && !defaultKey.isDefault()) {
            ThreadTask.start(() -> {
                DatabaseProvider.getInstance().getDefaultValueInfoDao()
                        .deleteList(String.valueOf(defaultKey.getDefaultKey()));
                DatabaseProvider.getInstance().getDefaultKeyDao().insert(defaultKey);
            });
        }
        /*DefaultManager defaultManager = ManagerFactory.getInstance(this).getDefaultManager();
        DefaultKey defaultKey = defaultManager.queryKey(
                kv.decodeString("userId", ""),
                kv.decodeString("dbId", ""),
                formId + "_" + schemaId);
        if (defaultKey != null && !defaultKey.getIsDefault()) {
            DefaultValueManager defaultValueManager = ManagerFactory.getInstance(this).getDefaultValueManager();
            defaultValueManager.delete(defaultKey.getDefaultValueInfo());
            defaultKey.resetDefaultValueInfo();
            defaultManager.saveOrUpdate(defaultKey);
        }*/

    }

    private boolean updateDefaultByInit() {
        if (!isOpen) {
            return updateDefault();
        }
        isOpen = false;
        MMKV kv = MMKV.mmkvWithID("app");
        DefaultKey defaultKey = ThreadTask.getTwo(() -> {
            List<DefaultKey> defaultKeys = DatabaseProvider.getInstance().getDefaultKeyDao().queryKey(
                    kv.decodeString("userId", ""),
                    kv.decodeString("dbId", ""),
                    formId + "_" + schemaId);
            if (defaultKeys.isEmpty()) {
                return defaultKeys.get(0);
            }
            return null;
        });

        List<DefaultValueInfo> defaultValueInfoList = ThreadTask.getTwo(() -> {
            if (defaultKey != null) {
                return DatabaseProvider.getInstance().getDefaultValueInfoDao().queryByKey(String.valueOf(defaultKey.getDefaultKey()));
            }
            return null;
        });
        //开启默认  2种情况 1.当前修改  2.开启下次作业
        //开启下次作业
        if (defaultKey != null && defaultKey.isDefault() && req != null) {
            List<ItemBean> list = getUpDataItemByText();
            list.addAll(defaultValueInfoList
                    .stream()
                    .filter(propertyBean -> propertyBean.getValue() != null && !propertyBean.getValue().isEmpty())
                    .map(defaultValueInfo -> new ItemBean(defaultValueInfo.getKey(), defaultValueInfo.getValue()))
                    .collect(Collectors.toList()));
            req.setItems(list);
            presenter.updateScanView(req, -1);
            return true;
        }
       /* DefaultKey defaultKey = ManagerFactory.getInstance(this).getDefaultManager().queryKey(
                kv.decodeString("userId", ""),
                kv.decodeString("dbId", ""),
                formId + "_" + schemaId);
        //开启默认  2种情况 1.当前修改  2.开启下次作业
        //开启下次作业
        if (defaultKey != null && defaultKey.getIsDefault() && req != null) {
            List<ItemBean> list = getUpDataItemByText();
            list.addAll(defaultKey
                    .getDefaultValueInfo()
                    .stream()
                    .filter(propertyBean -> propertyBean.getValue() != null && !propertyBean.getValue().isEmpty())
                    .map(defaultValueInfo -> new ItemBean(defaultValueInfo.getKey(), defaultValueInfo.getValue()))
                    .collect(Collectors.toList()));
            req.setItems(list);
            presenter.updateScanView(req, -1);
            return true;
        }*/
        return false;
    }

    @Override
    public void onSuccessSubmit(String tips, boolean isContainer) {
        ToastUtil.showToastCenter(tips);
    }

    @Override
    public void onConfirmBarCode(String tips) {
        ToastUtil.showToastCenter(tips);
    }

    @Override
    public void showTipsDialog(ViewReq submitReq, ViewReq viewReq, ViewReq enterReq, String tips, int position) {
        new XPopup.Builder(WMSBoxActivity.this).asConfirm("提示", tips, () -> {
            if (submitReq != null) {
                //提交任务相关,分别是关箱，关箱后提交和提交
                presenter.submitScanView(submitReq);
            } else if (viewReq != null) {
                //解析条码或更新其它字段
                presenter.updateScanView(viewReq, position);
            } else if (enterReq != null) {
                //确认录入
                presenter.confirmBarCode(enterReq, position);
            }
        }).show();
    }

    @Override
    public void barcodePrintExportReq(List<ActionResp> actions) {
        if (actions != null && actions.size() > 0) {
            for (int i = 0; i < actions.size(); i++) {
                if (actions.get(i).getName().equals("WMS_BARCODEPRINT")) {
                    //条码打印处理
                    BarcodePrintExportReq req = new BarcodePrintExportReq();
                    req.setItems(actions.get(i).getActionDetailResp().getItems());
                    req.setParams(actions.get(i).getActionDetailResp().getParams());
                    req.setServer(actions.get(i).getActionDetailResp().getServer());
                    presenter.barcodePrintExportReq(scene, req);
                } else if (actions.get(i).getName().equals("WMS_FORMPRINT")) {
                    //拆装箱打印处理
                    BarcodePrintExportReq req = new BarcodePrintExportReq();
                    req.setItems(actions.get(i).getActionDetailResp().getItems());
                    req.setParams(actions.get(i).getActionDetailResp().getParams());
                    req.setFormId(actions.get(i).getActionDetailResp().getFormId());
                    req.setServer(actions.get(i).getActionDetailResp().getServer());
                    presenter.boxPrintExportReq(scene, req);
                }
            }

        }
    }

    @Override
    public void print(String data) {
        if (!PermissionUtils.hasPermissions(WMSBoxActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ToastUtil.showToast("没有文件读取权限");
            return;
        }
        ThreadPoolManager.getInstance().addTask(() -> {
            if (data != null && !data.isEmpty()) {
                FileUtil.SaveToPDF(data, this);
                FileUtil.DeZip(this);
                printPDF(RxFileTool.listFilesInDir(mContext.getFilesDir().toString() + "/pdf_android"));
            }
        });
    }

    @Override
    public void showDialog(String msg) {
        loading.setTitle(msg);
        loading.show();
    }

    @Override
    public void dismissDialog() {
        loading.dismiss();
    }

    @Override
    public void errorUpdate(String tips, int pos) {
        showFailedView(tips);
        adapter.setEditTextValue(pos, "");
    }

    @Override
    public void cancelTask() {
        adapter.resetData();
    }

    @Override
    public void restartView() {
        initStateId(null);
    }

    public void updateData() {
        presenter.getScanView(req, adapter.getMarkPosition() - 1);
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
    public int onBindLayout() {
        return R.layout.wms_box_scan_activity;
    }

    @Override
    public void initView() {
        presenter.attach(this);
        initDefault();
        adapter = new WMSScanAdapter();
        adapter.setFormId(formId);
        getMBind().icWmsToolbar.tbTitle.setText(title);
        getMBind().icWmsToolbar.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        loading = new XPopup.Builder(this).dismissOnTouchOutside(false).asLoading("初始化配置中...");
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(this));
        getMBind().rvList.setAdapter(adapter);
        adapter.setOnEditorActionChangeListener(this);
        adapter.addChildClickViewIds(R.id.iv_base_info_query, R.id.iv_add, R.id.tv_scan_lock);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_base_info_query) {
                query(position);
            } else if (view.getId() == R.id.iv_add) {
                addCode(position);
            } else if (view.getId() == R.id.tv_scan_lock) {
                PropertyBean bean = this.adapter.getData().get(position);
                bean.setLock(!bean.isLock());
                adapter.notifyItemChanged(position);
            }
        });
        //

        List<String> show = new ArrayList<>();
        switch (formId) {
            case URLPath.WMS.MENU_WMS_PACKING_FROM_ID:
                packing = new PackingBox();
                //装箱
                getMBind().icWmsToolbar.tbCloseBox.setVisibility(View.VISIBLE);
                if ("true".equals(allowPackCodeCreate)) {
                    show.add("FBoxCodeId_Proxy");
                }
                break;
            case URLPath.WMS.MENU_WMS_UNPACKING_FROM_ID:
                packing = new UnPackingBox();
                //拆箱
                getMBind().icWmsToolbar.tbSubmit.setVisibility(View.VISIBLE);
//                getMBind().fabRecord.setVisibility(View.GONE);
                if ("true".equals(allowPackCodeCreate)) {
                    show.add("FNewCodeId");
                }
                break;
            case URLPath.WMS.MENU_WMS_SPLIT_FROM_ID:
                packing = new SplitBox();
                //拆分
                getMBind().icWmsToolbar.tbSubmit.setVisibility(View.VISIBLE);
                getMBind().btnWmsScanConfirm.setVisibility(View.GONE);
                getMBind().fabRecord.setVisibility(View.GONE);
                break;
        }
        adapter.setAddShow(show);

        getMBind().icWmsToolbar.tbCloseBox.setOnClickListener(this);
        getMBind().icWmsToolbar.tbSubmit.setOnClickListener(this);
        getMBind().fabRecord.setOnClickListener(this);
        getMBind().fabNullify.setOnClickListener(this);
        getMBind().fabDefaultValue.setOnClickListener(this);
        getMBind().btnWmsScanConfirm.setOnClickListener(this);

        registerActivityResult();

        presenter.getAnalysisLiveData().observe(this, analysisResp -> {
            //查看返回的条码数量是否可编辑，因为扫描明细是包装条码时，条码数量返回是不可编辑的
            analysisResp.getFBillHead().get(0).forEach((key, value) -> {
                if (key.equals("FBarQty_Proxy") && !value.isEnabled()) {
                    //锁定不能放开
                    adapter.setEnabledLock("FBarQty_Proxy");
                } else if (key.equals("FBarQty_Proxy") && value.isEnabled()) {
                    //回复放开选项
                    adapter.removeEnabledLock("FBarQty_Proxy");
                }
            });
            // 数据更新渲染
            analysisResp.getFBillHead().get(0).forEach((key, value) -> adapter.setValue(key, value));
            //保存更新后的原始数据
            analysisList = CommonUtils.deepClonePropertyBeanList(adapter.getData());
            primaryId = analysisResp.getPrimaryId();
        });

        Map<String, Object> params = new HashMap<>();
        params.put("schemaId", schemaId);
        FiltersReq req = new FiltersReq(params);
        req.setPageIndex(1);
        req.setIndex(1);
        presenter.getBoxStateId(scene, name, req);
    }

    /**
     * 生成 add 按钮
     *
     * @param position
     */
    private void addCode(int position) {
        String key = this.adapter.getData().get(position).getKey();
        if ("FNewCodeId".equals(key)) {
            presenter.createBoxCode(new ViewReq(CommandInvoke.Pack.WMS_CREATE_NEW_CODE, this.req.getPageId()), position);
        } else if ("FBoxCodeId_Proxy".equals(key)) {
            presenter.createBoxCode(new ViewReq(CommandInvoke.Pack.WMS_CREATE_BOX_CODE, this.req.getPageId()), position);
        }
    }

    /**
     * query 图标 点击
     *
     * @param position
     */
    private void query(int position) {
        PropertyBean bean = this.adapter.getData().get(position);
        switch (bean.getType()) {
            case "BASEDATA":
            case "ASSISTANT":
                //基础资料
                Intent intent = new Intent(this, BasicDataActivity.class);
                intent.putExtra("title", bean.getName());
                intent.putExtra("scene", scene);
                intent.putExtra("lookupId", bean.getTag());
                intent.putExtra("parentId", bean.getKey());
                intent.putExtra("position", position);
                //initStateId(this.primaryId);
                launcher.launch(intent);
                break;
            case "DATETIME":
                //日期选择
                initTimePick(position);
                break;
        }
    }

    private void initTimePick(int pos) {
        //时间选择器
        long beginTimestamp = DateFormatUtils.str2Long("1980-01-01", false);
        long endTimestamp = DateFormatUtils.str2Long("2100-01-01", false);
        CustomDatePicker picker = new CustomDatePicker(this, timestamp -> {
            String time = DateFormatUtils.long2Str(timestamp, false);
            adapter.setEditTextValue(pos, time);
        }, beginTimestamp, endTimestamp);

        picker.setCancelable(false);
        picker.setCanShowPreciseTime(false);
        picker.setScrollLoop(false);
        picker.setCanShowAnim(false);
        picker.show(System.currentTimeMillis());
    }

    private void registerActivityResult() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            if (data == null) return;
            int resultCode = result.getResultCode();
            if (RESULT_OK == resultCode) {
                updateData();
            } else {
                BaseInfoBean infoBean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                        data.getSerializableExtra("baseInfo", BaseInfoBean.class) :
                        (BaseInfoBean) data.getSerializableExtra("baseInfo");
                if (infoBean != null) {
//                    adapter.setDataInfo(resultCode, infoBean);
                    adapter.setEditTextValue(resultCode,
                            null != infoBean.getCode() ? infoBean.getCode() : infoBean.getName());
                }
            }
        });
    }

    @Override
    public void onClick(@Nullable View v) {
        if (v == null) {
            return;
        }
        if (v.getId() == getMBind().fabRecord.getId() && req != null) {
            //条码明细
            Intent intent = new Intent(this, DetailListActivity.class);
            intent.putExtra("title", "已扫数据");
            intent.putExtra("scene", scene);
            intent.putExtra("primaryId", primaryId);
            intent.putExtra("name", packing.billListName());
            intent.putExtra("pageId", req.getPageId());
            launcher.launch(intent);
        } else if (v.getId() == getMBind().fabDefaultValue.getId()) {
            //默认值
            List<PropertyBean> propertyBeanList = new ArrayList<>();
            if (formId.equals(URLPath.WMS.MENU_WMS_UNPACKING_FROM_ID)) {
                //拆箱 固定字段数量
//                if(isDefault()){
//                    propertyBeanList = adapter.getData().stream()
//                            .filter(propertyBean ->
//                                    propertyBean.getKey().equals("FBarQty_Proxy")
//                            )
//                            .collect(Collectors.toList());
                //               }
//                else {
                propertyBeanList = originalList.stream()
                        .filter(propertyBean ->
                                propertyBean.getKey().equals("FBarQty_Proxy")
                        )
                        .collect(Collectors.toList());
                propertyBeanList.get(0).setValue("");
                propertyBeanList.get(0).setEnable(true);
//                }

            } else {
                //装箱 固定字段包括：默认值保存至下次作业、包装容量、条码数量，动态字段则为装拆箱配置中定义的装箱补充字段。
//                if(isDefault()){
//                    propertyBeanList = adapter.getData().stream()
//                            .filter(propertyBean -> ((!propertyBean.isLock() &&
//                                    !propertyBean.getKey().equals("FBoxCodeId_Proxy") &&
//                                    !propertyBean.getKey().equals("FBarCodeId_Proxy"))
//                                    || propertyBean.getKey().equals("FCapacity_Proxy") || propertyBean.getKey().equals("FBarQty_Proxy"))
//                            )
//                            .collect(Collectors.toList());
//                }
//                else {
                for (PropertyBean p : originalList) {
                    if ((!p.isLock() &&
                            !p.getKey().equals("FBoxCodeId_Proxy") &&
                            !p.getKey().equals("FBarCodeId_Proxy") &&
                            !p.getKey().equals("FCount_Proxy"))
                            || p.getKey().equals("FCapacity_Proxy") || p.getKey().equals("FBarQty_Proxy")) {
                        PropertyBean bean = new PropertyBean(p.getKey(), p.getName());
                        bean.setType(p.getType());
                        bean.setName(p.getName());
                        bean.setTag(p.getTag());
                        bean.setEntity(p.getEntity());
                        bean.setEntityId(p.getEntityId());
                        bean.setParentId(p.getParentId());
                        bean.setSource(p.getSource());
                        bean.setEnums(p.getEnums());
                        bean.setRelated(p.getRelated());
                        bean.setFlexId(p.getFlexId());
                        bean.setEnable(true);
                        propertyBeanList.add(bean);
                    }
                }
//                }

            }
            new XPopup.Builder(this)
                    .maxWidth(2000)
                    .autoDismiss(false)
                    .asCustom(getDefaultValuePop(propertyBeanList))
                    .show();
            //          initDataByDefaultValuePop();
        } else if (v.getId() == getMBind().fabNullify.getId()) {
            //作废任务
            presenter.cancelTask(new ViewReq(CommandInvoke.Pack.WMS_CANCEL_TASK, this.req.getPageId()));
        } else if (v.getId() == getMBind().btnWmsScanConfirm.getId()) {
            //确认录入
            confirmBarCode();
        } else if (v.getId() == getMBind().icWmsToolbar.tbSubmit.getId()) {
            if (unpackAndTransfer) {
                new XPopup.Builder(this)
                        .maxWidth(2000)
                        .autoDismiss(false)
                        .asCustom(unpackingPop())
                        .show();
            } else {
                //提交任务
                new XPopup.Builder(this).asConfirm("确认提交", "确认提交数据进行拆箱？", () -> {
                    presenter.submitScanView(new ViewReq(CommandInvoke.Pack.WMS_SUBMIT_TASK, this.req.getPageId()));
                }).show();
            }
        } else if (v.getId() == getMBind().icWmsToolbar.tbCloseBox.getId()) {
            //关箱
            new XPopup.Builder(this).asConfirm("确认关箱", "确认提交数据进行装箱？", () -> {
                presenter.closeBoxCode(new ViewReq(CommandInvoke.Pack.WMS_SUBMIT_TASK, this.req.getPageId()));
            }).show();
        }
    }

    private NewCodePop unpackingPop() {
        NewCodePop newCodePop = new NewCodePop(
                (newCodeOptional != null && newCodeOptional.isPresent()) ? newCodeOptional.get().getName() : "装入箱",
                presenter.getAnalysisLiveData(),
                presenter.getCodeData(),
                this);
        newCodePop.setOnNewCodeListener(value -> {
            List<ItemBean> list = getUpDataItemByText();
            list.addAll(Collections.singletonList(new ItemBean("FNewCodeId", value)));
            req.setItems(list);
            presenter.updateScanView(req, -1);
            return null;
        });
        newCodePop.setOnAddNewCodeListener(() -> {
            presenter.createBoxCode(
                    new ViewReq(CommandInvoke.Pack.WMS_CREATE_NEW_CODE, this.req.getPageId()), -1);
            return null;
        });
        newCodePop.setOnConfirmListener(() -> {
            presenter.submitScanView(
                    new ViewReq(CommandInvoke.Pack.WMS_SUBMIT_TASK, this.req.getPageId()));
            return null;
        });
        return newCodePop;
    }

    private @NonNull DefaultValuePop getDefaultValuePop(List<PropertyBean> propertyBeanList) {
        if (defaultValuePop == null) {
            //默认值是否可编辑目前从propertyBeanList来
            defaultValuePop = new DefaultValuePop(CommonUtils.deepClonePropertyBeanList(propertyBeanList), formId + "_" + schemaId, this);
            defaultValuePop.setConfirmListener(this::updateDefault);
        } else {
            initDataByDefaultValuePop();
        }
        return defaultValuePop;
    }

    private void initDataByDefaultValuePop() {
        defaultValuePop.setData();
    }

    @Override
    public void initData() {
        connectPrint();
    }

    public void printPDF(List<File> files) {
        try {
            if (printerInterface != null) {
                printerInterface.print(files, 1);
                //可能存在这样的问题，打印请求过快，最近一次没打印，就把文件删除掉了
                RxFileTool.delAllFile(mContext.getFilesDir().toString() + "/pdf_android");
            }
//                Toast.makeText(mContext, result ? getString(R.string.send_success) : getString(R.string.send_fail), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
//                Toast.makeText(mContext, getString(R.string.disconnect) + "\n" + getString(R.string.print_fail) + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
//                Toast.makeText(mContext, getString(R.string.print_fail) + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
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
    public void playVoice(int pos, String type) {
        if (pos >= 0 && pos <= adapter.getData().size() - 1 && adapter.getData().get(pos).getKey().equals("FBoxCodeId_Proxy")) {
            SoundUtils.playVoice(this, type);
        }
    }

    @Override
    public void playVoice(String type) {
        SoundUtils.playVoice(this, type);
    }

    @Override
    public boolean initScanItem(List<PropertyBean> list) {
        if (URLPath.WMS.MENU_WMS_UNPACKING_FROM_ID.equals(formId)) {
            newCodeOptional = list.stream().filter(propertyBean -> "FNewCodeId".equals(propertyBean.getKey())).findFirst();

            list = list.stream()
                    .filter(propertyBean -> !"FNewCodeId".equals(propertyBean.getKey()))
                    .collect(Collectors.toList());
            //条码数量 初始默认锁定
            for (PropertyBean propertyBean : list) {
                if ("FBarQty_Proxy".equals(propertyBean.getKey())) {
                    propertyBean.setLock(true);
                }
            }
        } else if (URLPath.WMS.MENU_WMS_PACKING_FROM_ID.equals(formId)) {
            //条码数量,包装容量 初始默认锁定
            for (PropertyBean propertyBean : list) {
                if ("FCapacity_Proxy".equals(propertyBean.getKey()) || "FBarQty_Proxy".equals(propertyBean.getKey())) {
                    propertyBean.setLock(true);
                }
            }
        }
        adapter.setNewInstance(list);
        originalList = CommonUtils.deepClonePropertyBeanList(list);
        return !updateDefaultByInit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (printerInterface != null) {
            printerInterface.stopHeartBeat();
        }
    }

    @Override
    public void onEditorActionListener(@Nullable EditText view, @NonNull PropertyBean bean, int position) {
        String val = view != null ? view.getText().toString().trim() : "";
        if (req != null) {
            List<ItemBean> list = getUpDataItemByText();
            list.addAll(Collections.singletonList(new ItemBean(bean.getKey(), val)));
            req.setItems(list);
            presenter.updateScanView(req, position);
        }
    }

    //处理文本和数量字段不回车的更新
    private List<ItemBean> getUpDataItemByText() {
        List<ItemBean> list = new ArrayList<>();
        List<PropertyBean> editList = adapter.getData();
        Set<String> garCodeGroup = DataTypeUtilsKt.garCodeGroup();
        Set<String> baseDataGroup = DataTypeUtilsKt.baseDataGroup();
        if (editList.size() > 0) {
            list.addAll(editList.stream()
                    .filter(propertyBean -> !baseDataGroup.contains(propertyBean.getType()) && !"DATETIME".equals(propertyBean.getType()) && !garCodeGroup.contains(propertyBean.getKey()) && propertyBean.getValue() != null && !propertyBean.getValue().isEmpty())
                    .filter(propertyBean -> analysisList.stream().anyMatch(analysisBean -> propertyBean.getKey().equals(analysisBean.getKey()) && !propertyBean.getValue().equals(analysisBean.getValue())))
                    .map(propertyBean -> new ItemBean(propertyBean.getKey(), propertyBean.getValue()))
                    .collect(Collectors.toList()));
        }
        return list;
    }

    public interface Packing {

        String billListName();

        int confirmBarCodePos(WMSScanAdapter adapter);
    }

    public static class PackingBox implements Packing {

        @Override
        public String billListName() {
            return URLPath.WMS.Name.PATH_WMS_PACKING_QUERY_LIST_CODE;
        }

        @Override
        public int confirmBarCodePos(WMSScanAdapter adapter) {
            return adapter.getPosByKey("FBarCodeId_Proxy");
        }
    }


    public static class UnPackingBox implements Packing {
        @Override
        public String billListName() {
            return URLPath.WMS.Name.PATH_WMS_UNPACKING_LIST_CODE;
        }

        @Override
        public int confirmBarCodePos(WMSScanAdapter adapter) {
            return adapter.getPosByKey("FBarCodeId_Proxy");
        }
    }

    public static class SplitBox implements Packing {

        @Override
        public String billListName() {
            return "";
        }

        @Override
        public int confirmBarCodePos(WMSScanAdapter adapter) {
            return 0;
        }
    }
}
