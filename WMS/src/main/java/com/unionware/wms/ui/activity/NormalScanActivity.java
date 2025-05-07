package com.unionware.wms.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.tamsiree.rxkit.RxFileTool;
import com.tencent.mmkv.MMKV;
import com.unionware.printer.FileUtil;
import com.unionware.printer.PrintUtils;
import com.unionware.printer.print.PermissionUtils;
import com.unionware.printer.print.PrinterInterface;
import com.unionware.printer.print.ThreadPoolManager;
import com.unionware.wms.R;
import com.unionware.wms.URLPath;
import com.unionware.wms.databinding.NormalScanActivityBinding;
import com.unionware.wms.inter.scan.CommonListContract;
import com.unionware.wms.inter.scan.CommonListPresenter;
import com.unionware.wms.inter.wms.scan.DefaultSetContract;
import com.unionware.wms.inter.wms.scan.DefaultSetPresenter;
import com.unionware.wms.inter.wms.scan.NorMalScanContract;
import com.unionware.wms.inter.wms.scan.NormalScanPresenter;
import com.unionware.wms.model.bean.DefaultInfoBean;
import com.unionware.wms.model.bean.NormalScanConfigBean;
import com.unionware.wms.model.event.RefreshCommonListEvent;
import com.unionware.wms.model.event.RefreshSourceOrTaskListEvent;
import com.unionware.wms.model.req.PageIdReq;
import com.unionware.wms.ui.adapter.NormalScanBillInfoAdapter;
import com.unionware.wms.view.BatchFieldsPop;
import com.unionware.wms.view.DefaultValuePop;
import com.unionware.wms.view.InformationConfirmationPop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.utils.sound.SoundUtils;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;
import unionware.base.model.bean.BarcodeBean;
import unionware.base.model.bean.BaseInfoBean;
import unionware.base.model.bean.BillBean;
import unionware.base.model.bean.EntityBean;
import unionware.base.model.bean.PropertyBean;
import unionware.base.model.bean.SimpleViewAndModelBean;
import unionware.base.model.bean.TaskIdBean;
import unionware.base.model.req.BarcodePrintExportReq;
import unionware.base.model.req.ClientCustomParametersReq;
import unionware.base.model.req.ItemBean;
import unionware.base.model.req.TaskIdReq;
import unionware.base.model.req.TaskReq;
import unionware.base.model.req.ViewReq;
import unionware.base.model.resp.ActionResp;
import unionware.base.model.resp.AnalysisResp;

/**
 * @Author : pangming
 * @Time : On 2024/7/12 15:57
 * @Description : NormalScanActivity
 */
@AndroidEntryPoint
public class NormalScanActivity extends BaseBindActivity<NormalScanActivityBinding> implements NorMalScanContract.View,
        CommonListContract.View, OnRefreshListener, TextView.OnEditorActionListener, DefaultSetContract.View {
    @Inject
    DefaultSetPresenter defaultSetPresenter;
    @Inject
    NormalScanPresenter presenter;
    @Inject
    CommonListPresenter summaryPresenter;
    private LoadingPopupView loading;
    private BillBean billBean;
    private NormalScanConfigBean normalScanConfigBean;
    private ArrayList<String> primaryIds;
    private boolean isTask = false; // 是否从任务列表点击进去的
    private String taskId;
    private String scene;
    private ViewReq req;
    private NormalScanBillInfoAdapter billInfoAdapter;
    private SimpleViewAndModelBean simpleViewAndModelBean;//创建视图，返回View,pageid,data
    private AnalysisResp analysisResp;//更新视图回来数据
    private String type = "0";
    private InformationConfirmationPop informationConfirmationPop;
    private DefaultValuePop defaultValuePop;
    private ActivityResultLauncher<Intent> launcher;
    private ActivityResultLauncher<Intent> batchFieldsLauncher;
    private BatchFieldsPop batchFieldsPop;
    private MMKV kv;
    private boolean isFirst = true;
    private String creatorId;
    private String fromPage;
    private List<ClientCustomParametersReq.Param> preservationParams;//默认值数据
    private PrinterInterface printerInterface;
    private String NoBarCodeRowId = null;
    private boolean isCreateNewTask;
    private boolean isFirstScanBarcode = false;
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
            case 4:
            case 5:
                setState(msg, Color.BLUE);
                break;
        }
    };

    private void setState(String msg, int color) {
        runOnUiThread(() -> {
            getMBind().tvPrintState.setText(msg);
            getMBind().tvPrintState.setBackgroundColor(color);
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach(this);
        summaryPresenter.attach(this);
        defaultSetPresenter.attach(this);
        registerActivityResult();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                destroyScanView();
                taskCancel();
            }
        });
        getMBind().rvList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                etRequestFocus();
                getMBind().rvList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        //添加水印
        //Watermark.getInstance().show(NormalScanActivity.this,R.id.ll_bg,"测试");
    }

    private void connectPrint() {
        try {
            printerInterface = PrintUtils.connectPrint(this, callBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void destroyScanView() {
        if (null != req && null != req.getPageId()) {
            presenter.destroyScanView(new PageIdReq(req.getPageId()));
        } else {
            finishUI();
        }
    }

    private void taskCancel() {
        if (isCreateNewTask && !isFirstScanBarcode && taskId != null && !taskId.isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", Integer.parseInt(taskId));
            presenter.taskCancel(map);
        }
    }

    @Override
    public int onBindLayout() {
        return R.layout.normal_scan_activity;
    }

    @Override
    public void initView() {
        kv = MMKV.mmkvWithID("app");
        getMBind().toolbar.setNavigationOnClickListener(v -> {
                    destroyScanView();
                    taskCancel();
                }
        );

        loading = new XPopup.Builder(this).dismissOnTouchOutside(false).asLoading("初始化配置中...");
        billBean = (BillBean) getIntent().getSerializableExtra("bean");
        scene = getIntent().getStringExtra("scene");
        normalScanConfigBean = (NormalScanConfigBean) getIntent().getSerializableExtra("normalScanConfigBean");
        primaryIds = getIntent().getStringArrayListExtra("primaryIds");
        creatorId = getIntent().getStringExtra("creatorId");
        fromPage = getIntent().getStringExtra("fromPage");
        isCreateNewTask = getIntent().getBooleanExtra("createNewTask", false);

        getMBind().tbTitle.setText(billBean.getCode());
        getMBind().tbSubmit.setVisibility(View.VISIBLE);
        getMBind().tbSubmit.setText("提交");
        getMBind().tbSubmit.setOnClickListener(v -> {
            if (taskId != null && !taskId.isEmpty()) {
                if (normalScanConfigBean.getBatchFill()) {//normalScanConfigBean.getBatchFill()
                    if (null == req && null == req.getPageId()) {
                        return;
                    }
                    //构建 输入数据 调用  command 接口 提交
                    if (batchFieldsPop == null) {
                        batchFieldsPop = new BatchFieldsPop(this, scene, normalScanConfigBean, batchFieldsLauncher, presenter, req.getPageId());
                    }
                    new XPopup.Builder(this)
                            .maxWidth(this.getResources().getDisplayMetrics().widthPixels)
                            .maxHeight(this.getResources().getDisplayMetrics().heightPixels / 10 * 5)
                            .popupPosition(PopupPosition.Bottom)
                            .isViewMode(true)
                            .dismissOnTouchOutside(false)
                            .moveUpToKeyboard(true)
                            .isCoverSoftInput(true)
                            .enableDrag(false)
                            .asCustom(batchFieldsPop)
                            .show();
                } else {
                    new XPopup.Builder(NormalScanActivity.this).asConfirm("提示", "是否提交？", () -> {
                        if (null != req && null != req.getPageId()) {
                            TaskIdReq taskIdReq = new TaskIdReq("INVOKE_SUBMITTASK", req.getPageId());
                            presenter.flowTaskSubmit(taskIdReq);
                        }
                    }).show();
                }
            }
        });

        getMBind().tvQueryFilter.setVisibility(View.VISIBLE);
        billInfoAdapter = new NormalScanBillInfoAdapter(0, this);
        getMBind().smRefresh.setOnRefreshListener(this);
        getMBind().smRefresh.setEnableLoadMore(false);
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(mContext));
        getMBind().rvList.setAdapter(billInfoAdapter);
        getMBind().tvQueryFilter.setOnClickListener(view -> {
            String[] strings = new String[]{"全部", "已完成", "未完成"};
            new XPopup.Builder(this).maxHeight((int) getResources().getDimension(unionware.base.R.dimen.dp_400))
                    .asBottomList("", strings, (position, text) -> {
                        getMBind().tvQueryFilter.setText(strings[position]);
                        billInfoAdapter.getData().clear();
                        Map<String, Object> map = req.getParams();
                        type = String.valueOf(position);
                        map.put("type", type);
                        req.setParams(map);
                        summaryPresenter.getSummaryList(req);
                    }).show();
        });
        getMBind().actionSerialNumber.setOnClickListener(v -> {
            if (req == null) {
                ToastUtil.showToastCenter("初始化配置中...");
                return;
            }
            Intent intent = new Intent(NormalScanActivity.this, SourceSerialNumberActivity.class);
            intent.putExtra("pageId", req.getPageId());
            launcher.launch(intent);
        });
        getMBind().actionRecord.setOnClickListener(v -> {
            if (req == null) {
                ToastUtil.showToastCenter("初始化配置中...");
                return;
            }
            Intent intent = new Intent(NormalScanActivity.this, NormalRecordActivity.class);
            intent.putExtra("taskId", taskId);
            intent.putExtra("name", "663DE5F52B90F6");
            intent.putExtra("scene", scene);
            intent.putExtra("pageId", req.getPageId());
            launcher.launch(intent);
        });
        getMBind().actionDefault.setOnClickListener(v -> showDefaultValuePop());
        getMBind().etScanInput.setOnEditorActionListener(this);
        //是否启用手工指定分录扫描
        //是否启用指定分录建档
        if (isSelectEntry()) {
            billInfoAdapter.setSelect(true);
        }
        billInfoAdapter.setQuery(haveInventoryFilterItems());

        billInfoAdapter.addChildClickViewIds(R.id.tbQuery);
        billInfoAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.tbQuery) {
                ViewReq queryClick = new ViewReq("INVOKE_QUERYINVENTORY", req.getPageId());
                Map<String, Object> map = new HashMap<>();
                map.put("RowId", billInfoAdapter.getData().get(position).getLinkId());
                queryClick.setParams(map);
                presenter.commandQueryViewData(queryClick);
            }
        });
        billInfoAdapter.setOnItemClickListener((adapter, view, position) -> {
            ViewReq invokeClick = new ViewReq("INVOKE_CLIENTCLICKROW", req.getPageId());
            invokeClick.setParams(billInfoAdapter.getData().get(position).getDataMap());
            presenter.commandViewData(invokeClick);

            if (isNoBarCodeEntry(position)) {
                if (req != null) {
                    ViewReq viewReq = new ViewReq("INVOKE_CLIENTROWDOUBLECLICK", req.getPageId());
                    Map map = new HashMap<>();
                    NoBarCodeRowId = billInfoAdapter.getData().get(position).getLinkId();
                    map.put("ByRow", NoBarCodeRowId);
                    viewReq.setParams(map);
                    analysisResp = null;//重新扫要清空上次保存的信息
                    presenter.clientRowDoubleClick(viewReq, !(normalScanConfigBean.getNoCode() && normalScanConfigBean.getNoCodeMode().equals("FullBill")));
                }
            } else if (isSelectEntry()) {
                billInfoAdapter.setSelectItem(position);
            }
        });
        getMBind().tvScanTitle.setOnClickListener(v -> {
            // 启动二维码扫描界面
            IntentIntegrator integrator = new IntentIntegrator(NormalScanActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("请对准二维码进行扫描");
            integrator.setCameraId(0);
            integrator.setOrientationLocked(true);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            Intent zxingIntent = integrator.createScanIntent();
            launcher.launch(zxingIntent);
        });
        //是否显示扫描框，根据无条码制单方式
        if (normalScanConfigBean.getNoCode() && normalScanConfigBean.getNoCodeMode().equals("FullBill")) {
            getMBind().cScan.setVisibility(View.GONE);
        }
        billInfoAdapter.setNoCode(normalScanConfigBean.getNoCode() && normalScanConfigBean.getNoCodeMode().equals("FullBill"));
        billInfoAdapter.setNoCodeByMaterial(normalScanConfigBean.getNoCode() && normalScanConfigBean.getNoCodeMode().equals("ByMaterial"));
        billInfoAdapter.setSTK_StockCountInput(normalScanConfigBean.getTarFormId().equals("STK_StockCountInput"));
    }

    private boolean isSelectEntry() {
        return (normalScanConfigBean.getMatchByIndex() != null && normalScanConfigBean.getMatchByIndex()) || (normalScanConfigBean.getPointEntryParse() != null && normalScanConfigBean.getPointEntryParse());
    }

    private boolean haveInventoryFilterItems() {
        return (normalScanConfigBean.getInventoryFilterItems() != null
                && !normalScanConfigBean.getInventoryFilterItems().isEmpty());
    }

    private boolean isNoBarCodeEntry(int position) {
        return normalScanConfigBean.getNoCode() && normalScanConfigBean.getNoCodeMode().equals("FullBill") || (normalScanConfigBean.getNoCode() && normalScanConfigBean.getNoCodeMode().equals("ByMaterial") && billInfoAdapter.getData().get(position).getDataMap().containsKey("IsBarCodeManage") && billInfoAdapter.getData().get(position).getDataMap().get("IsBarCodeManage").toString().equals("false"));
    }

    @Override
    public void initData() {
        //盘点没有提交
        if ("2".equals(normalScanConfigBean.getFormEventMode()) || normalScanConfigBean.getTarFormId().equals("STK_StockCountInput")) {
            getMBind().tbSubmit.setVisibility(View.GONE);
        } else if ("1".equals(normalScanConfigBean.getFormEventMode())) {
            getMBind().tbSubmit.setVisibility(View.VISIBLE);
        } else if ("3".equals(normalScanConfigBean.getFormEventMode())) {
            //再根据任务列表返回的creatorId
            //有任务创建人id，优先创建人id
            if (creatorId != null && !creatorId.isEmpty()) {
                if (kv.getString("userId", "").equals(creatorId)) {//进行中
                    getMBind().tbSubmit.setVisibility(View.VISIBLE);
                } else if (creatorId != null && creatorId.equals("null")) {//点源单列表
                    getMBind().tbSubmit.setVisibility(View.VISIBLE);
                } else {//进行中不是改创建人
                    getMBind().tbSubmit.setVisibility(View.GONE);
                }
            } else {
                //新建创建任务
                getMBind().tbSubmit.setVisibility(View.VISIBLE);
            }
        }
        //注：启用多单合并扫描-按条码自动获取源单信息场景中，未开始扫描条码时任务明细信息为空。
        //有源单创建
        //新任务扫描
        //合并扫描
        isTask = URLPath.WMS.MENU_WMS_APP_SCAN_TASK.equals(billBean.getFormId());
        if (isFirst) {
            isFirst = false;
            if (billBean.getId() == null || billBean.getId().isEmpty()) {
                if (normalScanConfigBean.getMode().equals("3")) {
                    //无源单
                    //新任务扫描
                    List<TaskIdBean> items = new ArrayList<>();
                    presenter.createScanTask(new TaskReq(billBean.getPrimaryId(), Integer.parseInt(normalScanConfigBean.getId()), items));
                } else if (normalScanConfigBean.getMultiCombineScan() && normalScanConfigBean.getMultiMode().equals("1")) {
                    //合并扫描
                    if (primaryIds != null && primaryIds.size() > 0) {
                        List<TaskIdBean> items = new ArrayList<>();
                        for (int i = 0; i < primaryIds.size(); i++) {
                            items.add(new TaskIdBean(billBean.getFormId(), primaryIds.get(i)));
                        }
                        presenter.createScanTask(new TaskReq(billBean.getPrimaryId(), Integer.parseInt(normalScanConfigBean.getId()), items));
                    }
                } else if (normalScanConfigBean.getMultiCombineScan() && normalScanConfigBean.getMultiMode().equals("2")) {
                    List<TaskIdBean> items = new ArrayList<>();
                    if (normalScanConfigBean.getMode().equals("4")) {
                        items.add(new TaskIdBean(normalScanConfigBean.getTarFormId()));
                    } else {
                        items.add(new TaskIdBean(normalScanConfigBean.getSrcFormId()));
                    }

                    //新任务扫描
                    presenter.createScanTask(new TaskReq(billBean.getPrimaryId(), Integer.parseInt(normalScanConfigBean.getId()), items));
                }
            } else {
                List<TaskIdBean> items = new ArrayList<>();
                items.add(new TaskIdBean(billBean.getFormId(), billBean.getId()));
                //点击列表
                if (isTask) {
                    Map<String, Object> test = new HashMap<>();
                    taskId = billBean.getId();
                    test.put("TaskId", taskId);
                    presenter.createViewGetMore(new ViewReq("UNW_WMS_INPUT_NORMAL", test, true));
                } else {
                    presenter.createScanTask(new TaskReq(billBean.getPrimaryId(), Integer.parseInt(normalScanConfigBean.getId()), items));
                }
            }
            defaultSetPresenter.getClientDefinesDefaultSetByLocal(kv, kv.decodeString("orgId") == null ? 0 : Integer.valueOf(kv.decodeString("orgId"))
                    , kv.decodeString("userId") == null ? 0 : Integer.valueOf(kv.decodeString("userId")), billBean.getPrimaryId(), false);
        }
        connectPrint();
    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void showList(List<BillBean> list) {
        //如果有勾选分录方式重新保存选择
        if (isSelectEntry()) {
            List<BillBean> billBeans = billInfoAdapter.getData();
            for (int i = 0; i < billBeans.size(); i++) {
                if (billBeans.get(i).isSelect()) {
                    list.get(i).setSelect(true);
                }
            }
        }
        billInfoAdapter.setNewInstance(list);
    }

    @Override
    public void showEmptyView() {
        billInfoAdapter.getData().clear();
        billInfoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCompleteUpdateView(AnalysisResp data, int pos) {
        //有一次更新，就不用退出删除任务
        if (!isFirstScanBarcode) {
            isFirstScanBarcode = true;
        }
        analysisResp = data;
        if (pos == 300) {//无条码制单，设置默认值返回
            showScanPop();
        } else if (pos == -300) {//点击分录无条码返回
            if (isDefaultValue()) {
                List<ItemBean> list = getUpDataItemAll();
                if (list.size() > 0) {
                    //更新默认值
                    ViewReq viewReq = new ViewReq(req.getPageId());
                    viewReq.setItems(list);
                    if (isSelectEntry()) {
                        Map map = new HashMap<>();
                        map.put("ByRow", billInfoAdapter.getSelectItem());
                        viewReq.setParams(map);
                    } else viewReq.setParams(new HashMap<>());
                    presenter.updateScanView(viewReq, 300);
                } else {
                    showScanPop();
                }
            } else {
                showScanPop();
            }
        } else if (pos == -1) {//扫描条码处理
            if (isAbnormalPop()) {//异常弹窗
                if (isDefaultValue()) {
                    List<ItemBean> list = getUpDataItemAll();
                    if (list.size() > 0) {
                        //更新默认值
                        ViewReq viewReq = new ViewReq(req.getPageId());
                        viewReq.setItems(list);
                        if (isSelectEntry()) {
                            Map map = new HashMap<>();
                            map.put("ByRow", billInfoAdapter.getSelectItem());
                            viewReq.setParams(map);
                        } else viewReq.setParams(new HashMap<>());
                        presenter.updateScanView(viewReq, -100);
                    } else {
                        submitScanData();
                    }
                } else {
                    //如果是“仅异常时弹出”，扫描条码后，直接提交
                    submitScanData();
                }
            } else {
                if (isDefaultValue()) {
                    List<ItemBean> list = getUpDataItemAll();
                    if (!list.isEmpty()) {
                        //更新默认值
                        ViewReq viewReq = new ViewReq(req.getPageId());
                        viewReq.setItems(list);
                        if (isSelectEntry()) {
                            Map map = new HashMap<>();
                            map.put("ByRow", billInfoAdapter.getSelectItem());
                            viewReq.setParams(map);
                        } else viewReq.setParams(new HashMap<>());
                        presenter.updateScanView(viewReq, -100);
                    } else {
                        showScanPop();
                    }
                } else {
                    showScanPop();
                }
            }
        } else if (pos == -100) {//更新默认值回来处理
            if (isAbnormalPop()) {
                submitScanData();
            } else {
                showScanPop();
            }
        } else if (pos == -200) {//更新text值回来处理
            submitScanData();
        } else {
            if (isInformationConfirmationPopShow()) {
                informationConfirmationPop.onCompleteUpdateView(pos, analysisResp);
            }
        }
    }

    private void showScanPop() {
        if (informationConfirmationPop == null) {
            informationConfirmationPop = new InformationConfirmationPop(this, scene, launcher, req.getPageId());
        }
        new XPopup.Builder(this)
                .maxWidth(2000)
                .popupPosition(PopupPosition.Bottom)
                .isViewMode(true)
                .dismissOnTouchOutside(false)
                .moveUpToKeyboard(false)
                .enableDrag(false)
                .asCustom(informationConfirmationPop)
                .show();
        informationConfirmationPop.setViewAndData(simpleViewAndModelBean.getPropertyBeans(), analysisResp, getDefaultValue());
        informationConfirmationPop.setPreservationParams(preservationParams);
        informationConfirmationPop.setonConfirmClickListener(code -> {
            if (code.equals("取消")) {
                Log.e("informationConfirmationPop", "取消");
            } else {
                if (analysisResp == null || analysisResp.getFBillHead() == null || analysisResp.getFBillHead().size() == 0) {
                    ToastUtil.showToastCenter("解析异常请重新扫描");
                    return;
                }
                List<ItemBean> list = getUpDataItemByText();
                if (list.size() > 0) {
                    //更新text字段
                    ViewReq viewReq = new ViewReq(req.getPageId());
                    viewReq.setItems(list);
                    if (isSelectEntry()) {
                        Map map = new HashMap<>();
                        map.put("ByRow", billInfoAdapter.getSelectItem());
                        viewReq.setParams(map);
                    } else viewReq.setParams(new HashMap<>());
                    presenter.updateScanView(viewReq, -200);
                } else {
                    submitScanData();
                }
            }
        });
        informationConfirmationPop.setOnEditorActionListener((val, bean, position) -> {
            if (analysisResp == null || analysisResp.getFBillHead() == null || analysisResp.getFBillHead().size() == 0) {
                ToastUtil.showToastCenter("解析异常请重新扫描");
                return;
            }
            List<ItemBean> list = getUpDataItemByText();
            list.add(new ItemBean(bean.getKey(), val));
            ViewReq viewReq = new ViewReq(req.getPageId());
            viewReq.setItems(list);
            if (isSelectEntry()) {
                Map map = new HashMap<>();
                map.put("ByRow", billInfoAdapter.getSelectItem());
                viewReq.setParams(map);
            } else viewReq.setParams(new HashMap<>());
            presenter.updateScanView(viewReq, position);
        });
        informationConfirmationPop.setDismissListener(() -> {
            NoBarCodeRowId = null;
            etRequestFocus();
//                if(informationConfirmationPop.isHaveDefault()){
//                    defaultSetPresenter.setClientDefinesDefaultSetByLocal(kv,informationConfirmationPop.getDefaultList(),getControl(),kv.decodeString("orgId")==null?0:Integer.valueOf(kv.decodeString("orgId"))
//                            ,kv.decodeString("userId")==null?0:Integer.valueOf(kv.decodeString("userId")),billBean.getPrimaryId());
//                }
        });
        informationConfirmationPop.setDefaultSetChangeClickListener((editList, control) -> defaultSetPresenter.setClientDefinesDefaultSetByLocal(kv, editList, control, kv.decodeString("orgId") == null ? 0 : Integer.valueOf(kv.decodeString("orgId"))
                , kv.decodeString("userId") == null ? 0 : Integer.valueOf(kv.decodeString("userId")), billBean.getPrimaryId()));
    }

    @Override
    public void onSuccessSubmit(String tips, boolean isContainer) {
        if (!isContainer) {
            analysisResp = null;
            ToastUtil.showToastCenter(tips);
            getSummaryList();
            getMBind().etScanInput.setText("");
            etRequestFocus();
            if (isInformationConfirmationPopShow()) {
                informationConfirmationPop.dismiss();
            }
        } else if (normalScanConfigBean.getMultiMode().equals("2")) {
            if (MMKV.mmkvWithID("AppConfig").decodeBool("scanDocumentExit", false)) {
                destroyScanView();
            } else {
                if (null != req && null != req.getPageId()) {
                    presenter.closeOpenScanView(new PageIdReq(req.getPageId()));
                } else {
                    restartUI();
                }
            }
        } else {
            destroyScanView();
        }
    }

    @Override
    public void errorUpdate(String tips, int pos) {
        ToastUtil.showToastCenter(tips);
        if (pos == -300 && analysisResp != null) {
            showScanPop();
        } else if (pos == -1 && analysisResp != null) {
            showScanPop();
        } else if (isInformationConfirmationPopShow()) {
            informationConfirmationPop.setEditTextValue(pos, "");
        }

    }

    private void etRequestFocus() {
        new Handler().postDelayed(() -> {
            getMBind().etScanInput.requestFocus();
            getMBind().etScanInput.setSelection(0, getMBind().etScanInput.length());
        }, 50);
    }

    @Override
    public void requestFocus() {
        etRequestFocus();
    }


    @Override
    public void barcodePrintExportReq(List<ActionResp> actions) {
        if (actions != null && actions.size() > 0) {
            for (int i = 0; i < actions.size(); i++) {
                if (actions.get(i).getName().equals("WMS_BARCODEPRINT")) {
                    //打印处理
                    BarcodePrintExportReq req = new BarcodePrintExportReq();
                    req.setItems(actions.get(i).getActionDetailResp().getItems());
                    req.setParams(actions.get(i).getActionDetailResp().getParams());
                    req.setServer(actions.get(i).getActionDetailResp().getServer());
                    presenter.barcodePrintExportReq(scene, req);
                }
            }

        }
    }

    @Override
    public void printExportReq(List<ActionResp> actions, boolean isFinish) {
        if (actions != null && actions.size() > 0) {
            for (int i = 0; i < actions.size(); i++) {
                if (actions.get(i).getName().equals("WMS_FORMPRINT")) {
                    //打印处理
                    BarcodePrintExportReq req = new BarcodePrintExportReq();
                    req.setItems(actions.get(i).getActionDetailResp().getItems());
                    req.setParams(actions.get(i).getActionDetailResp().getParams());
                    req.setServer(actions.get(i).getActionDetailResp().getServer());
                    req.setFormId(actions.get(i).getActionDetailResp().getFormId());
                    presenter.printExportReq(scene, req, isFinish);
                }
            }

        }
    }

    @Override
    public void print(String data) {
        if (!PermissionUtils.hasPermissions(NormalScanActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
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
    public void printEndToFinish(String data) {
        if (!PermissionUtils.hasPermissions(NormalScanActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ToastUtil.showToast("没有文件读取权限");
            return;
        }
        ThreadPoolManager.getInstance().addTask(() -> {
            if (data != null && !data.isEmpty()) {
                FileUtil.SaveToPDF(data, this);
                FileUtil.DeZip(this);
                printPDF(RxFileTool.listFilesInDir(mContext.getFilesDir().toString() + "/pdf_android"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(() -> {
                            destroyScanView();
                        }, 1000);
                    }
                });
            }
        });
    }

    public void printPDF(List<File> files) {
        try {
            if (printerInterface != null) {
                if (!PrintUtils.isConnectPrint(printerInterface)) {
                    ToastUtil.showToastCenter("没有连接打印机，请连接打印机");
                    return;
                }
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
    public void playVoice(String type) {
        SoundUtils.playVoice(this, type);
    }

    @Override
    public void refreshSourceOrTaskList() {
        RefreshSourceOrTaskListEvent event = new RefreshSourceOrTaskListEvent();
        event.setFromPage(fromPage);
        EventBus.getDefault().post(event);
    }

    @Override
    public void openImageViewer(String name, String uri) {
        Bundle bundle = new Bundle();
        bundle.putString("imageName", name);
        bundle.putString("imageUrl", uri);
        startActivity(ImageViewerActivity.class, bundle);
    }

    private boolean isDefaultValue() {
        if (preservationParams != null && preservationParams.size() > 0) {
            int position = 0;
            for (int i = 0; i < preservationParams.size(); i++) {
                if (preservationParams.get(i).getKey().equals("editList")) {
                    position = i;
                }
            }
            String json = preservationParams.get(position).getValue();
            if (json != null && !json.isEmpty()) {
                List<PropertyBean> propertyBeans = new Gson().fromJson(json, new TypeToken<List<PropertyBean>>() {
                }.getType());
                if (propertyBeans != null && propertyBeans.size() > 0) {
                    for (int i = 0; i < propertyBeans.size(); i++) {
                        if (propertyBeans.get(i).getValue() != null && !propertyBeans.get(i).getValue().isEmpty())
                            return true;
                    }
                }
            }
        }
        return false;
    }

    private List<PropertyBean> getDefaultValue() {
        if (preservationParams != null && preservationParams.size() > 0) {
            int position = 0;
            for (int i = 0; i < preservationParams.size(); i++) {
                if (preservationParams.get(i).getKey().equals("editList")) {
                    position = i;
                }
            }
            String json = preservationParams.get(position).getValue();
            if (json != null && !json.isEmpty()) {
                List<PropertyBean> propertyBeans = new Gson().fromJson(json, new TypeToken<List<PropertyBean>>() {
                }.getType());
                return propertyBeans;
            }
        }
        return null;
    }

    private List<PropertyBean> getEditList(List<PropertyBean> propertyBeanLists) {
        List<PropertyBean> editList = new ArrayList<>();
        for (int i = 0; i < propertyBeanLists.size(); i++) {
            String key = propertyBeanLists.get(i).getKey();
            String type = propertyBeanLists.get(i).getType(); // 字段类型
            Map<String, BarcodeBean> map = analysisResp.getFBillHead().get(0);
            if (key.equals("FBarCodeId")) {
                String val = "ASSISTANT".equals(type) || "BASEDATA".equals(type) ? map.get(key).getNumber() : map.get(key).getValue();
                propertyBeanLists.get(i).setEnable(true);
                propertyBeanLists.get(i).setValue(val);
                continue;
            }
            if (map.get(key).isEnabled()) {
                String val = "FLEXVALUE".equals(type) || "ITEMCLASS".equals(type) || "ASSISTANT".equals(type) || "BASEDATA".equals(type) ? map.get(key).getNumber() : map.get(key).getValue();
                propertyBeanLists.get(i).setEnable(true);
                propertyBeanLists.get(i).setValue(val);
//                    包装条码数量解析会有返回是否编辑
//                    if (key.equals("FBarcodeType")) {
//                        isPack = "PackageBarCode".equals(map.get("FBarcodeType").getValue());
//                        propertyBeanLists.get(i).setEnable(!isPack);
//                    }
                editList.add(propertyBeanLists.get(i));
            } else if (!map.get(key).isEnabled()) {
                String val = "FLEXVALUE".equals(type) || "ITEMCLASS".equals(type) || "ASSISTANT".equals(type) || "BASEDATA".equals(type) ? map.get(key).getNumber() : map.get(key).getValue();
                propertyBeanLists.get(i).setEnable(false);
                propertyBeanLists.get(i).setValue(val);
            }

        }
        return editList;
    }

    /**
     * //根据扫描条码返回，再更新默认值，分2步可能会导致，返回的编辑项不一样,目前先调整全部更新
     * @return
     */
    private List<ItemBean> getUpDataItemAll() {
        List<ItemBean> list = new ArrayList<>();
        List<PropertyBean> defaultValueList = getDefaultValue();
        for (PropertyBean d : defaultValueList) {
            ItemBean item = new ItemBean(d.getKey(), d.getValue());
            list.add(item);
        }
        return list;
    }

    /**
     * 根据扫描条码返回，再更新默认值，分2步可能会导致，返回的编辑项不一样,这里根据编辑项，仓库仓位特殊处理
     * @return
     */
    private List<ItemBean> getUpDataItem() {
        List<ItemBean> list = new ArrayList<>();
        //有默认值组装更新值
        List<PropertyBean> editList = getEditList(simpleViewAndModelBean.getPropertyBeans());
        List<PropertyBean> defaultValueList = getDefaultValue();
        boolean fStockIdFlag = false;
        boolean fInStockLocIdFlag = false;
        String let_key = "";
        String let_key2 = "";
        //有编辑字段，有默认值
        if (editList.size() > 0 && defaultValueList != null && defaultValueList.size() > 0) {
            for (int i = 0; i < editList.size(); i++) {
                for (PropertyBean d : defaultValueList) {
                    if (editList.get(i).getKey().equals(d.getKey()) && d.getValue() != null && !d.getValue().isEmpty()) {
                        ItemBean item = new ItemBean(editList.get(i).getKey(), d.getValue());
                        if (editList.get(i).getKey().equals("FStockId") || editList.get(i).getKey().equals("FInStockId")) {
                            if ("FStockId".equals(editList.get(i).getKey())) {
                                let_key = "FStockLocId.FF";
                                fStockIdFlag = true;
                            } else {
                                let_key2 = "FInStockLocId.FF";
                                fInStockLocIdFlag = true;
                            }

                        }
                        list.add(item);
                    }
                }
            }

        }
        //特殊处理扫描条码，只返回仓库字段，如果默认值仓库仓位都有值，仓位也更新
        //仓库
        if (fStockIdFlag) {
            for (PropertyBean d : defaultValueList) {
                if (d.getKey().contains(let_key)) {
                    ItemBean item = new ItemBean(d.getKey(), d.getValue());
                    list.add(item);
                }
            }
        }
        //调入仓库
        if (fInStockLocIdFlag) {
            for (PropertyBean d : defaultValueList) {
                if (d.getKey().contains(let_key2)) {
                    ItemBean item = new ItemBean(d.getKey(), d.getValue());
                    list.add(item);
                }
            }
        }
        return list;
    }

    private List<ItemBean> getUpDataItemByText() {
        //后续可能需要处理文本和数量字段不回车的更新
        //获取文本和数量字段
        List<ItemBean> list = new ArrayList<>();
        List<PropertyBean> editList = informationConfirmationPop.getData();
        List<PropertyBean> originalList = getEditList(simpleViewAndModelBean.getPropertyBeans());//更新条码解析回来的数据
        Set<String> garCodeGroup = new HashSet<>(Arrays.asList("FBarCodeId_Proxy", "FBoxCodeId_Proxy", "FNewCodeId"));
        Set<String> baseDataGroup = new HashSet<>(Arrays.asList("BASEDATA", "ASSISTANT", "COMBOBOX", "COMBOX", "CHECKBOX", "RADIOBOX", "ITEMCLASS"));
        if (editList.size() > 0) {
            for (int i = 0; i < editList.size(); i++) {
                boolean isBaseData = baseDataGroup.contains(editList.get(i).getType());
                boolean isDataTime = "DATETIME".equals(editList.get(i).getType());
                boolean isBarCode = garCodeGroup.contains(editList.get(i).getKey());
                if (!isBaseData && !isDataTime && !isBarCode) {
                    if (editList.get(i).getValue() != null && !editList.get(i).getValue().isEmpty()) {
                        for (int j = 0; j < originalList.size(); j++) {
                            //过滤 只有真正修改才保存
                            if (editList.get(i).getKey().equals(originalList.get(j).getKey()) && !editList.get(i).getValue().equals(originalList.get(j).getValue())) {
                                ItemBean item = new ItemBean(editList.get(i).getKey(), editList.get(i).getValue());
                                list.add(item);
                                break;
                            }
                        }

                    }
                }

            }
        }
        if (list.size() > 0) {
            Log.e("文本改变", "有文本变动");
        }


        return list;
//        List<ItemBean> list = getUpDataItemByText();
//        if(list.size()>0) {
//            //更新默认值
//            ViewReq viewReq = new ViewReq(req.getPageId());
//            viewReq.setItems(list);
//            if (normalScanConfigBean.getMatchByIndex() != null && normalScanConfigBean.getMatchByIndex()) {
//                Map map = new HashMap<>();
//                map.put("ByRow", billInfoAdapter.getSelectItem());
//                viewReq.setParams(map);
//            } else viewReq.setParams(new HashMap<>());
//            presenter.updateScanView(viewReq, -100);
//        }
    }

    private List<ItemBean> getUpDataItemByPop() {
        List<ItemBean> list = new ArrayList<>();
        List<PropertyBean> editList = informationConfirmationPop.getData();
        if (editList.size() > 0) {
            for (int i = 0; i < editList.size(); i++) {
                if (editList.get(i).getValue() != null && !editList.get(i).getValue().isEmpty()) {
                    ItemBean item = new ItemBean(editList.get(i).getKey(), editList.get(i).getValue());
                    list.add(item);
                }
            }

        }
        return list;
    }

    @Override
    public void initScanItem(List<EntityBean> list, boolean isContainer) {

    }

    @Override
    public void initConfigInfo(TaskIdBean bean) {
        Map<String, Object> map = new HashMap<>();
        map.put("TaskId", bean.getPrimaryId());
        taskId = bean.getPrimaryId();
        //EventBus.getDefault().post(new TaskIdEvent(bean.getPrimaryId()));
        presenter.createViewGetMore(new ViewReq("UNW_WMS_INPUT_NORMAL", map, true));
    }

    @Override
    public void onCompleteView(String id) {
        req = new ViewReq("INVOKE_SUMMARY", id);
    }

    @Override
    public void onCompleteView(SimpleViewAndModelBean bean) {
        simpleViewAndModelBean = bean;
        req = new ViewReq("INVOKE_SUMMARY", bean.getPageId());
        // 汇总（未完成）
        getSummaryList();
    }

    private void getSummaryList() {

        Map<String, Object> map = new HashMap<>();
        map.put("type", type);  // 显示【未完成】任务汇总
        req.setParams(map);
        summaryPresenter.getSummaryList(req);
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
    public void showTipsDialog(TaskIdReq req, ViewReq viewReq, String tips) {
        if (viewReq != null && !isInformationConfirmationPopShow()) {
            //同时弹出采集框，这个交互点确定的时候，条码就确定进去
            showScanPop();
        }
        new XPopup.Builder(NormalScanActivity.this).asConfirm("提示", tips, () -> {
            if (req != null) {
                //提交任务
                presenter.flowTaskSubmit(req);
            } else if (viewReq != null) {
                //提交条码
                presenter.submitScanView(viewReq);
            }
        }).show();
    }

    private boolean isInformationConfirmationPopShow() {
        return informationConfirmationPop != null && informationConfirmationPop.isShow();
    }

    @Override
    public void finishUI() {
        finish();
//        initData();
    }

    @Override
    public void restartUI() {
        showDialog("初始化配置中...");
        new Handler().postDelayed(() -> {
            isFirst = true;
            billInfoAdapter.setNewInstance(new ArrayList<>());
            initData();
        }, 200);
    }

    private void showDefaultValuePop() {
        if (simpleViewAndModelBean != null) {
            defaultValuePop = new DefaultValuePop(this, scene, billBean.getPrimaryId(), launcher, presenter, req.getPageId(), normalScanConfigBean);
            new XPopup.Builder(this).maxWidth(2000).popupPosition(PopupPosition.Bottom).isViewMode(true).dismissOnTouchOutside(false).moveUpToKeyboard(false).enableDrag(false).asCustom(defaultValuePop).show();
            defaultValuePop.setViewAndData(simpleViewAndModelBean.getPropertyBeans());
            defaultValuePop.setDefault(preservationParams);
            defaultValuePop.setonConfirmClickListener(code -> {
                if (code.equals("取消")) {
                    Log.e("defaultValuePop", "取消");
                } else if (code.equals("清空并保存")) {
                    defaultSetPresenter.setClientDefinesDefaultSetByLocal(kv, null, null, kv.decodeString("orgId") == null ? 0 : Integer.valueOf(kv.decodeString("orgId"))
                            , kv.decodeString("userId") == null ? 0 : Integer.valueOf(kv.decodeString("userId")), billBean.getPrimaryId());
                } else {
                    defaultSetPresenter.setClientDefinesDefaultSetByLocal(kv, defaultValuePop.getDefaultConfigure(), defaultValuePop.getControl(), kv.decodeString("orgId") == null ? 0 : Integer.valueOf(kv.decodeString("orgId"))
                            , kv.decodeString("userId") == null ? 0 : Integer.valueOf(kv.decodeString("userId")), billBean.getPrimaryId());
                }
            });
            defaultValuePop.setDismissListener(() -> etRequestFocus());
        } else {
            ToastUtil.showToastCenter("初始化配置中...");
        }

    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
        if (billInfoAdapter != null) {
            billInfoAdapter.getData().clear();
        }
        if (req != null) {
            getSummaryList();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return scanBarcode(v.getText().toString());
    }

    private boolean scanBarcode(String val) {
        //是否启用手工指定分录扫描
        if (isSelectEntry() && !billInfoAdapter.isSelectItem()) {
            ToastUtil.showToastCenter("请指定扫描分录");
            return false;
        }
        if (simpleViewAndModelBean == null) {
            ToastUtil.showToastCenter("正在加载请稍等");
            return false;
        }
        List<ItemBean> list = new ArrayList<>();
        ItemBean clearItem = new ItemBean(simpleViewAndModelBean.getPropertyBeans().get(0).getKey(), "");
        list.add(clearItem);
        ItemBean item = new ItemBean(simpleViewAndModelBean.getPropertyBeans().get(0).getKey(), val);
        list.add(item);
        if (req != null) {
            ViewReq viewReq = new ViewReq(req.getPageId());
            viewReq.setItems(list);
            if (isSelectEntry()) {
                Map map = new HashMap<>();
                map.put("ByRow", billInfoAdapter.getSelectItem());
                viewReq.setParams(map);
            } else viewReq.setParams(new HashMap<>());
            analysisResp = null;//重新扫要清空上次保存的信息
            presenter.updateScanView(viewReq, -1);
        }
        return false;
    }

    private void submitScanData() {
        if (analysisResp == null || null == analysisResp.getFBillHead()) {
            ToastUtil.showToastCenter("请先扫描条码！");
            return;
        }
        ViewReq viewReq = new ViewReq("INVOKE_SUBMITCODE", req.getPageId());
        if (isSelectEntry()) {
            Map map = new HashMap<>();
            map.put("ByRow", billInfoAdapter.getSelectItem());
            viewReq.setParams(map);
        }
        //无条码是否要添加字段区别
        if (NoBarCodeRowId != null) {
            Map map = new HashMap<>();
            map.put("ByRow", NoBarCodeRowId);
            viewReq.setParams(map);
        }
        presenter.submitScanView(viewReq);
    }

    private boolean isAbnormalPop() {
        if (preservationParams != null && preservationParams.size() > 0) {
            for (int i = 0; i < preservationParams.size(); i++) {
                if (preservationParams.get(i).getKey().equals("control")) {
                    String json = preservationParams.get(i).getValue();
                    if (json != null && !json.isEmpty()) {
                        DefaultInfoBean defaultInfoBean = new Gson().fromJson(json, DefaultInfoBean.class);
                        if ("1".equals(defaultInfoBean.getPopControl())) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }

        }
        // true  默认“仅异常时弹出”  fasle 默认“固定弹出”
        return DefaultValuePop.isDefault;
    }

    private void registerActivityResult() {
        batchFieldsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            if (data == null) return;
            int resultCode = result.getResultCode();
            BaseInfoBean infoBean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                    data.getSerializableExtra("baseInfo", BaseInfoBean.class) :
                    (BaseInfoBean) data.getSerializableExtra("baseInfo");
            batchFieldsPop.setUpdateEditTextValue(resultCode, infoBean);
        });
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            if (data == null) return;
            int resultCode = result.getResultCode();
            if (RESULT_OK == resultCode) {
                IntentResult zxingResult = IntentIntegrator.parseActivityResult(result.getResultCode(), data);
                if (zxingResult.getContents() != null) {
                    // 获取扫描结果
                    String content = zxingResult.getContents().trim();
                    getMBind().etScanInput.setText(content);
                    scanBarcode(content);
                }
            } else {
                BaseInfoBean infoBean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                        data.getSerializableExtra("baseInfo", BaseInfoBean.class) :
                        (BaseInfoBean) data.getSerializableExtra("baseInfo");
                if (infoBean != null && isInformationConfirmationPopShow()) {
//                    adapter.setDataInfo(resultCode, infoBean);
                    informationConfirmationPop.setUpdataEditTextValue(resultCode,
                            null != infoBean.getCode() ? infoBean.getCode() : infoBean.getName(), infoBean);
                } else if (infoBean != null && defaultValuePop != null && defaultValuePop.isShow()) {
                    defaultValuePop.setUpdataEditTextValue(resultCode,
                            null != infoBean.getCode() ? infoBean.getCode() : infoBean.getName(), infoBean);
                }
            }

        });
    }

    @Override
    public boolean enableEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void createTask(RefreshCommonListEvent event) {
        if (req != null) {
            getSummaryList();
        }
    }

    @Override
    public void getClientDefinesDefaultSet(List<ClientCustomParametersReq.Param> params) {
        preservationParams = params;
        if (informationConfirmationPop != null) {
            informationConfirmationPop.setPreservationParams(preservationParams);
            informationConfirmationPop.setDefaultPropertyBean(getDefaultValue());
        }
    }

    private String getControl() {
        String control = null;
        if (preservationParams != null && preservationParams.size() > 0) {
            control = preservationParams.get(1).getValue();
        } else {
            return control;
        }
        return control;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (printerInterface != null) {
            printerInterface.stopHeartBeat();
        }
    }
}
