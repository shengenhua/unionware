package com.unionware.wms.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.unionware.wms.R;
import com.unionware.wms.ui.activity.BasicDataActivity;
import com.unionware.wms.ui.adapter.NorMalScanAdapter;
import com.unionware.wms.ui.adapter.NormalBarcodeInfoContentAdapter;
import com.unionware.wms.utlis.CommonUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unionware.base.app.utils.DateFormatUtils;
import unionware.base.app.utils.ToastUtil;
import unionware.base.model.bean.BarcodeBean;
import unionware.base.model.bean.BaseInfoBean;
import unionware.base.model.bean.PropertyBean;
import unionware.base.model.req.ClientCustomParametersReq;
import unionware.base.model.resp.AnalysisResp;
import unionware.base.ui.datepicker.CustomDatePicker;

/**
 * @Author : pangming
 * @Time : On 2024/7/15 17:38
 * @Description : InformationConfirmationPop
 */

public class InformationConfirmationPop extends BottomPopupView implements NorMalScanAdapter.OnEditorActionChangeListener {

    private RecyclerView rlv_scan;
    private RecyclerView rlv_show;
    private TextView tv_cancel;
    private TextView tv_confirm;

    private AnalysisResp analysisResp; //更新字段返回的数据
    private List<PropertyBean> viewData;//创建视图返回的View 数据

    private boolean isPack = false; // 是否为包装条码
    private List<PropertyBean> showList; // 展示数据
    private List<PropertyBean> editList; // 可编辑的列表

    private NorMalScanAdapter adapter;
    private NormalBarcodeInfoContentAdapter normalBarcodeInfoContentAdapter;
    private onConfirmClickListener listener;

    private onMyEditorActionListener onEditorActionListener;
    private DismissListener dismissListener;
    private DefaultSetChangeClickListener defaultSetChangeClickListener;
    private Context context;
    private String scene;
    private ActivityResultLauncher<Intent> launcher;
    private String pageId;
    private List<PropertyBean> defaultPropertyBean;


    private List<ClientCustomParametersReq.Param> preservationParams;//默认值数据

    public InformationConfirmationPop(@NonNull Context context, String scene, ActivityResultLauncher<Intent> launcher, String pageId) {
        super(context);
        this.context = context;
        this.scene = scene;
        this.launcher = launcher;
        this.pageId = pageId;

    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_information_confirmation;
    }

    public void setPreservationParams(List<ClientCustomParametersReq.Param> preservationParams) {
        this.preservationParams = preservationParams;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        Window window = getHostWindow();
        if (window != null)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        rlv_scan = findViewById(R.id.rlv_scan);
        rlv_show = findViewById(R.id.rlv_show);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);
        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onConfirmClickListener("取消");
                }
            }
        });
        tv_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfirmClickListener("确认");
                }
            }
        });
        initScanAdapter();
        normalBarcodeInfoContentAdapter = new NormalBarcodeInfoContentAdapter();
        normalBarcodeInfoContentAdapter.setNewInstance(showList);
        rlv_show.setLayoutManager(new LinearLayoutManager(getContext()));
        rlv_show.setAdapter(normalBarcodeInfoContentAdapter);
        rlv_scan.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                new Handler().postDelayed(() -> {
                    adapter.findCurrentFocusable();
                    rlv_scan.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }, 200);
            }
        });
    }

    private void initScanAdapter() {
        adapter = new NorMalScanAdapter();
        adapter.setDefaultShow(true);
        rlv_scan.setLayoutManager(new LinearLayoutManager(getContext()));
        rlv_scan.setAdapter(adapter);
        adapter.setNewInstance(editList);

        adapter.setOnEditorActionChangeListener(this);
        adapter.addChildClickViewIds(R.id.iv_base_info_query, R.id.tv_scan_lock, R.id.tv_scan_default);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            PropertyBean bean = this.adapter.getData().get(position);
            if (view.getId() == R.id.iv_base_info_query) {
                if (!bean.isLock()) query(position);
            } else if (view.getId() == R.id.tv_scan_lock) {
                bean.setLock(!bean.isLock());
                adapter.notifyItemChanged(position);
            } else if (view.getId() == R.id.tv_scan_default) {
                setDefaultByItemClickListener(bean, position);
            }
        });
        adapter.setCheckChangeListener(new NorMalScanAdapter.CheckChangeListener() {
            @Override
            public void onCheckChangeListener(@NonNull PropertyBean bean, int position, boolean isCheck) {
                onEditorActionListener.onEditorActionListener(isCheck + "", bean, position);
            }
        });
    }

    /**
     *  仓库有默认，判断仓库是否仓位有默认值项
     * @return
     */
    private boolean isPositionDefault(String pkey) {
        if (preservationParams != null && preservationParams.size() > 0) {
            if (preservationParams.get(0).getValue() == null) {
                return false;
            } else {
                List<PropertyBean> list = new ArrayList<>();
                String json = preservationParams.get(0).getValue();
                List<PropertyBean> newEditList = new Gson().fromJson(json, new TypeToken<List<PropertyBean>>() {
                }.getType());
                if (newEditList != null && newEditList.size() > 0) {
                    list.addAll(CommonUtils.deepClonePropertyBeanList(newEditList));
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getKey().contains(pkey)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 在仓库有默认值，获取仓位的情况  仓位没有默认 "1"，仓位部分默认 "2"，仓位全默认 "3"
     * @param pkey
     * @return
     */
    private Map<String, List<PropertyBean>> isPositionDefaultType(String pkey, PropertyBean bean, PropertyBean original) {
        if (preservationParams != null && preservationParams.size() > 0) {
            if (preservationParams.get(0).getValue() == null) {
                Map<String, List<PropertyBean>> map = new HashMap<>();
                map.put("1", new ArrayList<>());
                return map;
            } else {
                List<PropertyBean> list = new ArrayList<>();
                List<PropertyBean> defaultList = new ArrayList<>();
                String json = preservationParams.get(0).getValue();
                List<PropertyBean> newEditList = new Gson().fromJson(json, new TypeToken<List<PropertyBean>>() {
                }.getType());
                if (newEditList != null && newEditList.size() > 0) {
                    list.addAll(CommonUtils.deepClonePropertyBeanList(newEditList));
                }
                // 判断 仓位部分默认 "2"，仓位全默认 "3"
                List<String> defaultKeys = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getKey().contains(pkey)) {
                        defaultKeys.add(list.get(i).getKey());
                        defaultList.add(list.get(i).clone());
                    }
                }
                List<String> originalkeys = new ArrayList<>();
                for (int j = 0; j < original.getFStockFlexItem().size(); j++) {
                    for (String keyName : original.getFStockFlexItem().get(j).keySet()) {
                        if (keyName.equals("flexId")) {
                            String pKey = pkey + new BigDecimal(original.getFStockFlexItem().get(j).get(keyName).toString()).stripTrailingZeros().toPlainString();
                            originalkeys.add(pKey);
                        }
                    }
                }
                if (originalkeys.size() == defaultKeys.size()) {
                    Map<String, List<PropertyBean>> map = new HashMap<>();
                    map.put("3", defaultList);
                    return map;
                } else {
                    Map<String, List<PropertyBean>> map = new HashMap<>();
                    map.put("2", defaultList);
                    return map;
                }
            }
        }
        Map<String, List<PropertyBean>> map = new HashMap<>();
        map.put("1", new ArrayList<>());
        return map;
    }

    /**
     *  清空所有仓位的默认值,
     */
    private void clearPositionDefault(String pkey) {
        if (preservationParams != null && preservationParams.size() > 0) {
            if (preservationParams.get(0).getValue() != null) {
                List<PropertyBean> list = new ArrayList<>();
                List<PropertyBean> newList = new ArrayList<>();
                String json = preservationParams.get(0).getValue();
                List<PropertyBean> newEditList = new Gson().fromJson(json, new TypeToken<List<PropertyBean>>() {
                }.getType());
                if (newEditList != null && newEditList.size() > 0) {
                    list.addAll(CommonUtils.deepClonePropertyBeanList(newEditList));
                }
                for (int i = 0; i < list.size(); i++) {
                    if (!list.get(i).getKey().contains(pkey)) {
                        newList.add(list.get(i));
                    }
                }
                //更新默认值
                String control = null;
                if (preservationParams != null && preservationParams.size() > 0) {
                    control = preservationParams.get(1).getValue();
                }
                if (defaultSetChangeClickListener != null) {
                    defaultSetChangeClickListener.onDefaultSetChangeClickListener(newList, control);
                }
            }
        }
    }

    /**
     *
     * @param position
     * @param bean  更新某一项的默认值
     */
    private void setDefaultByItem(int position, PropertyBean bean) {
        String control = null;
        if (preservationParams != null && preservationParams.size() > 0) {
            control = preservationParams.get(1).getValue();
        }
        if (defaultSetChangeClickListener != null) {
            defaultSetChangeClickListener.onDefaultSetChangeClickListener(getDefaultListByItem(position, bean.isDefault()), control);
        }
    }

    /**
     *  获取当前所有的默认值项
     * @param position
     * @param isDefault
     * @return
     */
    private List<PropertyBean> getDefaultListByItem(int position, boolean isDefault) {
        List<PropertyBean> list = new ArrayList<>();
        PropertyBean bean = this.adapter.getData().get(position);
        //获取当前的默认列表
        if (preservationParams != null && preservationParams.size() > 0) {
            if (preservationParams.get(0).getValue() == null) {
                //当前使用
                if (isDefault) {
                    list.add(editList.get(position));
                }
            } else {
                //下次作业
                String json = preservationParams.get(0).getValue();
                List<PropertyBean> newEditList = new Gson().fromJson(json, new TypeToken<List<PropertyBean>>() {
                }.getType());
                if (newEditList != null && newEditList.size() > 0) {
                    list.addAll(CommonUtils.deepClonePropertyBeanList(newEditList));
                }
                if (isDefault) {
                    list.add(editList.get(position));
                } else {
                    int pos = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getKey().equals(bean.getKey())) {
                            pos = i;
                        }
                    }
                    if (list.size() >= pos + 1) {
                        list.remove(pos);
                    }
                }

            }

        } else {
            //没有设置保存过默认值
            list.add(editList.get(position));
        }
        return list;
    }

    public boolean isHaveDefault() {
        for (int i = 0; i < editList.size(); i++) {
            if (editList.get(i).isDefault()) {
                return true;
            }
        }
        return false;
    }

    /**
     *  取消默认的，没记录到，用不了
     * @return
     */
    public List<PropertyBean> getDefaultList() {
        List<PropertyBean> list = new ArrayList<>();
        List<PropertyBean> defaultList = new ArrayList<>();
        for (int i = 0; i < editList.size(); i++) {
            if (editList.get(i).isDefault()) {
                defaultList.add(editList.get(i).clone());
            }
        }
        //获取当前的默认列表,有默认过
        if (preservationParams != null && preservationParams.size() > 0) {
            if (preservationParams.get(0).getValue() == null) {
                //当前使用
                list.addAll(defaultList);
            } else {
                //下次作业
                String json = preservationParams.get(0).getValue();
                List<PropertyBean> newEditList = new Gson().fromJson(json, new TypeToken<List<PropertyBean>>() {
                }.getType());
                if (newEditList != null && newEditList.size() > 0) {
                    list.addAll(CommonUtils.deepClonePropertyBeanList(newEditList));
                }
                //相同的覆盖,没有的添加
                for (int i = 0; i < defaultList.size(); i++) {
                    boolean isHave = false;
                    for (int j = 0; j < list.size(); j++) {
                        if (defaultList.get(i).getKey().equals(list.get(j).getKey())) {
                            isHave = true;
                            list.set(j, defaultList.get(i));
                        }
                    }
                    if (!isHave) {
                        list.add(defaultList.get(i));
                    }
                }
            }

        } else {
            //没有设置保存过默认值
            list.addAll(defaultList);
        }
        return list;
    }

    private boolean isFLEXVALUEDefaultValue(PropertyBean bean) {
        String pkey = "";
        if (bean.getKey().contains("FStockLocId.FF")) {
            pkey = "FStockId";
        } else if (bean.getKey().contains("FInStockLocId.FF")) {
            pkey = "FInStockId";
        }
        if (pkey.isEmpty()) {
            return true;
        } else {
            for (int i = 0; i < editList.size(); i++) {
                if (pkey.equals(editList.get(i).getKey()) && !editList.get(i).isDefault()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void structureList(List<PropertyBean> propertyBeanLists, AnalysisResp analysisResp, boolean isFirstShow) {
        if (editList == null) {
            editList = new ArrayList<>();
        } else {
            editList.clear();
        }
        if (showList == null) {
            showList = new ArrayList<>();
        } else {
            showList.clear();
        }
        for (int i = 0; i < propertyBeanLists.size(); i++) {
            String key = propertyBeanLists.get(i).getKey();
            String type = propertyBeanLists.get(i).getType(); // 字段类型
            Map<String, BarcodeBean> map = analysisResp.getFBillHead().get(0);
            if (!map.get(key).isVisible()) continue;
            if (key.equals("FBarCodeId")) {
                String val = "ASSISTANT".equals(type) || "BASEDATA".equals(type) ? map.get(key).getNumber() : map.get(key).getValue();
                propertyBeanLists.get(i).setEnable(true);
                propertyBeanLists.get(i).setValue(val);
                showList.add(propertyBeanLists.get(i));
                continue;
            }
            if (map.get(key).isEnabled()) {
                String val = "FLEXVALUE".equals(type) || "ITEMCLASS".equals(type) || "ASSISTANT".equals(type) || "BASEDATA".equals(type) ? map.get(key).getNumber() : map.get(key).getValue();
                propertyBeanLists.get(i).setEnable(true);
                propertyBeanLists.get(i).setValue(val);
                propertyBeanLists.get(i).setId(map.get(key).getId() == null ? null : map.get(key).getId().toString());
//                    包装条码数量解析会有返回是否编辑
//                    if (key.equals("FBarcodeType")) {
//                        isPack = "PackageBarCode".equals(map.get("FBarcodeType").getValue());
//                        propertyBeanLists.get(i).setEnable(!isPack);
//                    }
                editList.add(propertyBeanLists.get(i));
                showList.add(propertyBeanLists.get(i));
            } else if (!map.get(key).isEnabled()) {
                String val = "FLEXVALUE".equals(type) || "ITEMCLASS".equals(type) || "ASSISTANT".equals(type) || "BASEDATA".equals(type) ? map.get(key).getNumber() : map.get(key).getValue();
                propertyBeanLists.get(i).setEnable(false);
                propertyBeanLists.get(i).setValue(val);
                showList.add(propertyBeanLists.get(i));
            }

        }
        //条码解析,值改变，回来仓库要设置维度
        setDimension();
        setITEMCLASS();
        setDefaultByUpData(isFirstShow);

        if (adapter != null) {
//            if(isFirstShow){
//                //扫描新的条码  返回编辑项，afterTextChanged 有些数据会受影响赋值错误
//                initScanAdapter();
//            }else {
            adapter.setNewInstance(editList);
            adapter.notifyDataSetChanged();
//            }
            Window window = getHostWindow();
            if (window != null)
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            if (isFirstShow) {
                rlv_scan.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        new Handler().postDelayed(() -> {
                            adapter.findCurrentFocusable();
                            rlv_scan.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }, 200);
                    }
                });
            }
        }
        if (normalBarcodeInfoContentAdapter != null) {
            normalBarcodeInfoContentAdapter.setNewInstance(showList);
            normalBarcodeInfoContentAdapter.notifyDataSetChanged();
        }
    }

    private void setDefaultByItemClickListener(PropertyBean bean, int position) {
        if (bean.getValue() == null || bean.getValue().isEmpty()) {
            ToastUtil.showToastCenter("值为空不能保存默认值");
            return;
        }
        //仓位要仓库先默认，才能默认
        if (!bean.isDefault() && !isFLEXVALUEDefaultValue(bean)) {
            ToastUtil.showToastCenter("仓位需关联仓库进行默认值设置");
            return;
        }
        bean.setDefault(!bean.isDefault());
        adapter.notifyItemChanged(position);
        //设置默认
        setDefaultByItem(position, bean);
        //仓库取消默认，仓位也取消
        if ((bean.getKey().equals("FStockId") || bean.getKey().equals("FInStockId")) && !bean.isDefault()) {
            String pkey = "";
            if (bean.getKey().equals("FStockId")) {
                pkey = "FStockLocId.FF";
            } else if (bean.getKey().equals("FInStockId")) {
                pkey = "FInStockLocId.FF";
            }
            for (int i = 0; i < editList.size(); i++) {
                if (editList.get(i).getKey().contains(pkey) && editList.get(i).isDefault()) {
                    editList.get(i).setDefault(false);
                    adapter.notifyItemChanged(i);
                    setDefaultByItem(i, editList.get(i));
                }
            }
        }
    }

    private void setDefaultByonCompleteUpdateView(PropertyBean bean, int position, PropertyBean original) {
        if (bean.getValue() == null || bean.getValue().isEmpty()) {
            //ToastUtil.showToastCenter("值为空不能保存默认值");
            bean.setDefault(false);
            adapter.notifyItemChanged(position);
            return;
        }
        //仓位要仓库先默认，才能默认
        if (!bean.isDefault() && !isFLEXVALUEDefaultValue(bean)) {
            ToastUtil.showToastCenter("仓位需关联仓库进行默认值设置");
            return;
        }
        //只有是扫仓库位置更新，由于可能存在扫库位条码的情况要特殊处理
        if (bean.getKey().equals("FStockId") || bean.getKey().equals("FInStockId")) {
            //设置当前仓库的
            setDefaultByItem(position, bean);
            String pkey = "";
            if (bean.getKey().equals("FStockId")) {
                pkey = "FStockLocId.FF";
            } else if (bean.getKey().equals("FInStockId")) {
                pkey = "FInStockLocId.FF";
            }
            //获取仓位默认情况
            Map<String, List<PropertyBean>> map = isPositionDefaultType(pkey, bean, original);
            if (map.keySet().contains("1")) {//没有默认
                Log.e("仓位", "没有默认");
            } else {
                //清除原先的仓位默认
                if (isPositionDefault(pkey)) {
                    clearPositionDefault(pkey);
                }
                if (map.keySet().contains("2")) {//部分默认
                    List<PropertyBean> items = map.get("2");
                    for (int i = 0; i < items.size(); i++) {
                        for (int j = 0; j < editList.size(); j++) {
                            if (editList.get(j).getKey().equals(items.get(i).getKey())) {
                                editList.get(j).setDefault(true);
                                setDefaultByItem(j, editList.get(i));
                            }
                        }
                    }

                } else if (map.keySet().contains("3")) {//全部默认
                    List<PropertyBean> items = map.get("3");
                    if (items.size() > 0) {
                        for (int i = 0; i < editList.size(); i++) {
                            if (editList.get(i).getKey().contains(pkey)) {
                                editList.get(i).setDefault(true);
                                setDefaultByItem(i, editList.get(i));
                            }
                        }
                    }
                }
            }
//            //清除原先的仓位默认
//            if(isPositionDefault(pkey)){
//                clearPositionDefault(pkey);
//            }
//            //判断相应的仓位是否默认,有默认重新添加
//            for (int i = 0; i < editList.size(); i++) {
//                if(editList.get(i).getKey().contains(pkey) && editList.get(i).isDefault()){
//                    setDefaultByItem(i,editList.get(i));
//                }
//            }
        } else {
            //设置默认
            setDefaultByItem(position, bean);
        }
        //仓库取消默认，仓位也取消(这样没点击事件不需要)
//        if((bean.getKey().equals("FStockId") || bean.getKey().equals("FInStockId")) && !bean.isDefault()){
//            String pkey = "";
//            if(bean.getKey().equals("FStockId")){
//                pkey = "FStockLocId.FF";
//            }else if(bean.getKey().equals("FInStockId")) {
//                pkey = "FInStockLocId.FF";
//            }
//            for (int i = 0; i < editList.size(); i++) {
//                if(editList.get(i).getKey().contains(pkey) && editList.get(i).isDefault()){
//                    editList.get(i).setDefault(false);
//                    adapter.notifyItemChanged(i);
//                    setDefaultByItem(i,editList.get(i));
//                }
//            }
//        }
    }

    private void setDefaultByUpData(boolean isFirstShow) {
        if (defaultPropertyBean != null && defaultPropertyBean.size() > 0) {
            for (PropertyBean p : defaultPropertyBean) {
                if (p.getValue() != null && !p.getValue().isEmpty()) {
                    for (int i = 0; i < editList.size(); i++) {
                        if (isFirstShow) {
                            if (p.getKey().equals(editList.get(i).getKey())) {
                                editList.get(i).setDefault(true);
                            }
                        } else {
                            //勾选默认值，替换的情况
                            if (p.getKey().equals(editList.get(i).getKey()) && editList.get(i).getValue() != null && !editList.get(i).getValue().isEmpty()) {
                                editList.get(i).setDefault(true);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *  //条码解析回来仓库要设置维度,根据是否返回仓位字段和仓位字段是否可编辑和可显示
     */
    private void setDimension() {
        PropertyBean FStockIdBean = null;
        PropertyBean FInStockIdBean = null;
        for (int i = 0; i < editList.size(); i++) {
            if (editList.get(i).getKey().equals("FStockId")) {
                FStockIdBean = editList.get(i);
            } else if (editList.get(i).getKey().equals("FInStockId")) {
                FInStockIdBean = editList.get(i);
            }
        }
        //不管是否仓库还是一样，都重现设置
        if (FStockIdBean != null) {
            List<Map<String, Object>> FStockFlexItem = new ArrayList<>();
            Map<String, Object> map;
            for (int i = 0; i < editList.size(); i++) {
                if (editList.get(i).getKey().contains("FStockLocId.FF")) {
                    map = new HashMap<>();
                    map.put("flexId", editList.get(i).getFlexId());
                    FStockFlexItem.add(map);
                }
            }
            FStockIdBean.setFStockFlexItem(FStockFlexItem);
        }
        if (FInStockIdBean != null) {
            List<Map<String, Object>> FInStockFlexItem = new ArrayList<>();
            Map<String, Object> map;
            for (int i = 0; i < editList.size(); i++) {
                if (editList.get(i).getKey().contains("FInStockLocId.FF")) {
                    map = new HashMap<>();
                    map.put("flexId", editList.get(i).getFlexId());
                    FInStockFlexItem.add(map);
                }
            }
            FInStockIdBean.setFStockFlexItem(FInStockFlexItem);
        }
    }

    /**
     *   ITEMCLASS 字段处理,同时可能存在多个的处理
     */
    private void setITEMCLASS() {

        //ITEMCLASS 与 COMBOBOX 同时存在的处理
        for (PropertyBean p : editList) {
            if (p.getValue() != null && !p.getValue().isEmpty() && p.getRelated() != null && !p.getRelated().isEmpty()) {
                for (int i = 0; i < editList.size(); i++) {
                    if (p.getRelated().equals(editList.get(i).getKey()) && editList.get(i).getValue() != null && !editList.get(i).getValue().isEmpty()) {
                        List<Map<String, String>> enums = editList.get(i).getEnums();
                        if (enums != null && enums.size() > 0) {
                            for (int j = 0; j < enums.size(); j++) {
                                if (enums.get(j).get("Name").equals(editList.get(i).getValue())) {
                                    p.setParentId(enums.get(j).get("Value"));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setViewAndData(List<PropertyBean> propertyBeanLists, AnalysisResp analysisResp, List<PropertyBean> defaultPropertyBean) {
//        editList = origData.stream().filter(EntityBean::isEdit).collect(Collectors.toList());
//        adapter.setNewInstance(editList);
        //这里需要克隆
        this.analysisResp = analysisResp;
        this.viewData = CommonUtils.deepClonePropertyBeanList(propertyBeanLists);
        this.defaultPropertyBean = defaultPropertyBean;
        structureList(this.viewData, this.analysisResp, true);
    }

    public void setDefaultPropertyBean(List<PropertyBean> defaultPropertyBean) {
        this.defaultPropertyBean = defaultPropertyBean;
    }

    public List<PropertyBean> getData() {
        return adapter.getData();
    }

    public NorMalScanAdapter getAdapter() {
        return adapter;
    }

    /**
     * query 图标 点击
     *
     * @param position
     */
    private void query(int position) {
        PropertyBean bean = this.adapter.getData().get(position);
        switch (bean.getType()) {
            //仓位字段
            case "FLEXVALUE":
                //lookupId,filters请求格式如下：
                //{
                //    "parentId" : Related 对应字段的Id（即仓库Id）,
                //    "flexId": FlexId（维度Id，与上面返回的一致）
                //}
            case "ITEMCLASS":
                //多类别资料
                //action=UnionWare.Basic.Query&scene=WMS.Normal&name=BD_OwnerOrg
                //{"filters":{"keyword":"12"},"pageIndex":1,"pageSize":20}
            case "BASEDATA":
                //基础资料
                //action=UnionWare.Basic.Query&scene=WMS.Normal&name=BOS_FLEXVALUE_SELECT
                //{"filters":{"keyword":"12"},"pageIndex":1,"pageSize":20}  keyword
            case "ASSISTANT":
                //辅助属性 要用parentId

                Intent intent = new Intent(context, BasicDataActivity.class);
                intent.putExtra("title", bean.getName());
                intent.putExtra("scene", scene);
                if ("ASSISTANT".equals(bean.getType())) {
                    intent.putExtra("lookupId", bean.getTag());
                    intent.putExtra("parentId", bean.getParentId());
                } else if ("ITEMCLASS".equals(bean.getType())) {
                    if (bean.getParentId() != null && !bean.getParentId().isEmpty()) {
                        intent.putExtra("lookupId", bean.getParentId());
                    } else {
                        //获取关联Related
                        ToastUtil.showToastCenter("请先选择" + getRelatedName(bean));
                        return;
                    }

                } else if ("BASEDATA".equals(bean.getType())) {
                    intent.putExtra("lookupId", bean.getTag());
                } else if ("FLEXVALUE".equals(bean.getType())) {
                    if (getRelatedId(bean).isEmpty()) {
                        ToastUtil.showToastCenter("请先选择" + getRelatedName(bean));
                        return;
                    } else {
                        intent.putExtra("lookupId", bean.getTag());
                        intent.putExtra("parentId", getRelatedId(bean));
                        intent.putExtra("flexId", bean.getFlexId());
                    }
                }
                intent.putExtra("position", position);
                //initStateId(this.primaryId);
                launcher.launch(intent);
                break;
            case "DATETIME":
                //日期选择
                initTimePick(position);
                break;
            case "COMBOBOX":
                //下拉列表
                initComBox(position);
                break;
            case "CHECKBOX":
                //已调整方式，这里不在使用
                //复选框    有是否选择 每个都是独立的互不影响
                initCHECKBOX(position);
                break;
            case "RADIOBOX":
                //待调整
                //单选框   有是否选择，不独立，会相互影响，多个单选框只有一个是true,可以都是false
                initRADIOBOX(position);
                break;
        }
    }

    private String getRelatedName(PropertyBean bean) {
        String name = "";
        for (int i = 0; i < adapter.getData().size(); i++) {
            if (bean.getRelated().equals(adapter.getData().get(i).getKey())) {
                name = adapter.getData().get(i).getName();
            }
        }
        return name;
    }

    private String getRelatedId(PropertyBean bean) {
        String id = "";
        for (int i = 0; i < adapter.getData().size(); i++) {
            if (bean.getRelated().equals(adapter.getData().get(i).getKey())) {
                id = adapter.getData().get(i).getId();
            }
        }
        return id;
    }

    private void initRADIOBOX(int pos) {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asBottomList(editList.get(pos).getName(), new String[]{"是", "否"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if (text.equals("是")) {
                                    adapter.getData().get(pos).setValue("true");
                                    adapter.setEditTextValue(pos, "true");
                                } else {
                                    adapter.getData().get(pos).setValue("false");
                                    adapter.setEditTextValue(pos, "false");
                                }

                            }
                        })
                .show();
    }

    private void initCHECKBOX(int pos) {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asBottomList(editList.get(pos).getName(), new String[]{"是", "否"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if (text.equals("是")) {
                                    adapter.getData().get(pos).setValue("true");
                                    adapter.setEditTextValue(pos, "true");
                                } else {
                                    adapter.getData().get(pos).setValue("false");
                                    adapter.setEditTextValue(pos, "false");
                                }
                            }
                        })
                .show();
    }

    private void initComBox(int pos) {
//        {
//            "Key": "FInOwnerTypeId",
//                "Name": "调入货主类型",
//                "Source": "ITEMCLASSTYPEFIELD",
//                "Entity": "FBillHead",
//                "EntityId": 1,
//                "Type": "COMBOBOX",
//                "Enums": [
//            {
//                "Name": "业务组织",
//                    "Value": "BD_OwnerOrg"
//            },
//            {
//                "Name": "供应商",
//                    "Value": "BD_Supplier"
//            },
//            {
//                "Name": "客户",
//                    "Value": "BD_Customer"
//            }
//        ]
//        },
//        {
//            "Key": "FInOwnerId",
//                "Name": "调入货主",
//                "Source": "ITEMCLASSFIELD",
//                "Entity": "FBillHead",
//                "EntityId": 1,
//                "Type": "ITEMCLASS",
//                "Related": "FInOwnerTypeId"
//        },
        List<Map<String, String>> Enums = editList.get(pos).getEnums();
        String[] names = new String[Enums.size()];
        for (int i = 0; i < Enums.size(); i++) {
            names[i] = Enums.get(i).get("Name");
        }
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asBottomList(editList.get(pos).getName(), names,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {

                                boolean isRelated = false;
                                //关联Related
                                for (int i = 0; i < editList.size(); i++) {
                                    if (editList.get(pos).getKey().equals(editList.get(i).getRelated())) {
                                        if (!(Enums.get(position).get("Value").equals(editList.get(i).getParentId()))) {
                                            isRelated = true;
                                            // editList.get(i).setValue("");
                                            editList.get(i).setParentId(Enums.get(position).get("Value"));
                                            //adapter.notifyItemChanged(i);
                                            adapter.getData().get(pos).setValue(Enums.get(position).get("Value"));
                                            adapter.setEditTextValue(pos, Enums.get(position).get("Value"));
//                                            editList.get(pos).setValue(Enums.get(position).get("Value"));
//                                            adapter.notifyItemChanged(pos);
                                            //更新视图

                                        }

                                    }
                                }
                                if (!isRelated) {
                                    adapter.getData().get(pos).setValue(Enums.get(position).get("Value"));
                                    adapter.setEditTextValue(pos, Enums.get(position).get("Value"));
                                }
                            }
                        })
                .show();
    }

    private void initTimePick(int pos) {
        //时间选择器
        long beginTimestamp = DateFormatUtils.str2Long("1980-01-01", false);
        long endTimestamp = DateFormatUtils.str2Long("2100-01-01", false);
        CustomDatePicker picker = new CustomDatePicker(context, timestamp -> {
            String time = DateFormatUtils.long2Str(timestamp, false);
            adapter.setEditTextValue(pos, time);
        }, beginTimestamp, endTimestamp);

        picker.setCancelable(false);
        picker.setCanShowPreciseTime(false);
        picker.setScrollLoop(false);
        picker.setCanShowAnim(false);
        picker.show(System.currentTimeMillis());
    }

    public void setEditTextValue(int position, String content) {
        //更新可编辑字段
        if (adapter != null) {
            adapter.setEditTextValue(position, content);
        }
    }

    public void setUpdataEditTextValue(int position, String content, BaseInfoBean infoBean) {
        //更新可编辑字段
        if (adapter != null) {
            //仓库要保存关联的维度仓位
            if (editList.get(position).getType().equals("FStockId") || editList.get(position).getType().equals("FInStockId")) {
                editList.get(position).setFStockFlexItem(infoBean.getFStockFlexItem());
            }
            adapter.setEditTextValue(position, content);
        }
    }

    public void onCompleteUpdateView(int position, AnalysisResp analysisResp) {
        this.analysisResp = analysisResp;
        //获取当前原始的item值
        PropertyBean item = editList.get(position).clone();
        //检查显示字段,重新构建editList
        structureList(viewData, analysisResp, false);
        //如果是默认值已勾选，更改值了，需默认值更新
        if (editList.get(position).isDefault()) {
            setDefaultByonCompleteUpdateView(editList.get(position), position, item);
        }
        //光标下移
        rlv_scan.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.focusMoveDown(position);
                //rlv_scan.scrollToPosition(position+1);
            }
        }, 200);
    }

    @Override
    public void onEditorActionListener(@Nullable EditText view, @NonNull PropertyBean bean, int position) {
        String val = view != null ? view.getText().toString().trim() : "";
        //调整复选框，单选框，目前已调整复选框
        if (bean.getType().equals("COMBOBOX") || bean.getType().equals("CHECKBOX") || bean.getType().equals("RADIOBOX")) {
            val = bean.getValue();
        }
//        ViewReq viewReq = new ViewReq(pageId);
//        viewReq.setParams(new HashMap<>());
//        viewReq.setItems(Collections.singletonList(new ItemBean(bean.getKey(), val)));
//        presenter.updateScanView(viewReq, position);
        onEditorActionListener.onEditorActionListener(val, bean, position);
    }

    /**
     * 获取RecyclerView的对应的EditText
     * @param position
     * @return
     */
    public EditText getRecyclerViewItemEditText(int position) {
        if (rlv_scan == null || rlv_scan.getLayoutManager() == null || rlv_scan.getAdapter() == null) {
            return null;
        }
        if (position > rlv_scan.getAdapter().getItemCount()) {
            return null;
        }
        RecyclerView.ViewHolder viewHolder = rlv_scan.getAdapter().createViewHolder(rlv_scan, rlv_scan.getAdapter().getItemViewType(position));
        rlv_scan.getAdapter().onBindViewHolder(viewHolder, position);
        viewHolder.itemView.measure(MeasureSpec.makeMeasureSpec(rlv_scan.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        return viewHolder.itemView.findViewById(R.id.et_scan_input);
    }


    /**
     *  取消，确定事件
     */
    public interface onConfirmClickListener {
        void onConfirmClickListener(String code);
    }

    public interface onMyEditorActionListener {
        void onEditorActionListener(String val, PropertyBean bean, int position);
    }

    public interface DefaultSetChangeClickListener {
        void onDefaultSetChangeClickListener(List<PropertyBean> editList, String control);
    }

    public void setonConfirmClickListener(onConfirmClickListener listener) {
        this.listener = listener;
    }

    public void setOnEditorActionListener(onMyEditorActionListener onEditorActionListener) {
        this.onEditorActionListener = onEditorActionListener;
    }

    public interface DismissListener {
        void onDismissListener();
    }

    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public void setDefaultSetChangeClickListener(DefaultSetChangeClickListener defaultSetChangeClickListener) {
        this.defaultSetChangeClickListener = defaultSetChangeClickListener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dismissListener != null) {
            dismissListener.onDismissListener();
        }
    }
}
