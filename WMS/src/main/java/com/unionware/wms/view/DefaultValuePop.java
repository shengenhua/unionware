package com.unionware.wms.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.tencent.mmkv.MMKV;
import com.unionware.wms.R;
import com.unionware.wms.inter.basedata.BasicDataOnEditorActionContract;
import com.unionware.wms.inter.basedata.BasicDataOnEditorActionPresenter;
import com.unionware.wms.inter.wms.scan.NormalScanPresenter;
import com.unionware.wms.model.bean.DefaultInfoBean;
import com.unionware.wms.model.bean.NormalScanConfigBean;
import com.unionware.wms.ui.activity.BasicDataActivity;
import com.unionware.wms.ui.adapter.NorMalScanAdapter;
import com.unionware.wms.utlis.CommonUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.app.utils.DateFormatUtils;
import unionware.base.app.utils.ToastUtil;
import unionware.base.model.bean.BarcodeBean;
import unionware.base.model.bean.BaseInfoBean;
import unionware.base.model.bean.PropertyBean;
import unionware.base.model.req.ClientCustomParametersReq;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.resp.AnalysisResp;
import unionware.base.ui.datepicker.CustomDatePicker;

/**
 * @Author : pangming
 * @Time : On 2024/7/15 14:56
 * @Description : DefaultValuePop
 */
@AndroidEntryPoint
public class DefaultValuePop extends BottomPopupView implements NorMalScanAdapter.OnEditorActionChangeListener, BasicDataOnEditorActionContract.View {
    private RecyclerView rlv_scan;
    private CheckBox cb_default, cb_abnormal, cb_fixed;
    private TextView tv_cancel, tv_clear_and_save, tv_confirm;
    private onConfirmClickListener listener;
    private DismissListener dismissListener;
    private List<PropertyBean> editList; // 可编辑的列表
    private List<PropertyBean> originalEditList; //原始编辑数据

    /**
     *  是否默认 false 固定弹框+
     *  wms
     *  燕麦 默认 固定弹窗
     *  其他 默认 仅异常时弹出
     */
    public static final boolean isDefault = false;

    private NorMalScanAdapter adapter;
    private MMKV kv;
    private String primaryId;
    private Context context;
    private String scene;
    private ActivityResultLauncher<Intent> launcher;
    private NormalScanPresenter presenter;
    @Inject
    BasicDataOnEditorActionPresenter basicDataOnEditorActionPresenter;
    private String pageId;
    private List<ClientCustomParametersReq.Param> preservationParams;
    private NormalScanConfigBean normalScanConfigBean;

    public DefaultValuePop(@NonNull Context context) {
        super(context);
    }

    public DefaultValuePop(@NonNull Context context, String scene, String primaryId, ActivityResultLauncher<Intent> launcher, NormalScanPresenter presenter, String pageId, NormalScanConfigBean normalScanConfigBean) {
        super(context);
        basicDataOnEditorActionPresenter.attach(this);
        this.context = context;
        this.primaryId = primaryId;
        this.scene = scene;
        this.launcher = launcher;
        this.presenter = presenter;
        this.pageId = pageId;
        this.normalScanConfigBean = normalScanConfigBean;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_default_value;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        kv = MMKV.mmkvWithID("app");
        //Pop使用dialog方式手动隐藏软键盘
        //getHostWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        rlv_scan = findViewById(R.id.recyclerView);
        cb_default = findViewById(R.id.cb_default);
        cb_abnormal = findViewById(R.id.cb_abnormal);
        cb_fixed = findViewById(R.id.cb_fixed);

        tv_cancel = findViewById(R.id.tv_cancel);
        tv_clear_and_save = findViewById(R.id.tv_clear_and_save);
        tv_confirm = findViewById(R.id.tv_confirm);


        cb_abnormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_fixed.setChecked(false);
                } else {
                    cb_fixed.setChecked(true);
                }
            }
        });
        cb_fixed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_abnormal.setChecked(false);
                } else {
                    cb_abnormal.setChecked(true);
                }
            }
        });
        tv_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onConfirmClickListener("取消");
                }
            }
        });
        tv_clear_and_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onConfirmClickListener("清空并保存");
                }
            }
        });
        tv_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onConfirmClickListener("保存");
                }
            }
        });
        initAdapter();
        setDefault();
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

    private void initAdapter() {
        adapter = new NorMalScanAdapter();
        adapter.setLockShow(false);
        rlv_scan.setLayoutManager(new LinearLayoutManager(getContext()));
        rlv_scan.setAdapter(adapter);
        adapter.setNewInstance(editList);

        adapter.setOnEditorActionChangeListener(this);
        adapter.addChildClickViewIds(R.id.iv_base_info_query, R.id.tv_scan_lock);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_base_info_query) {
                query(position);
            } else if (view.getId() == R.id.tv_scan_lock) {
                PropertyBean bean = this.adapter.getData().get(position);
                bean.setLock(!bean.isLock());
                adapter.notifyItemChanged(position);
            }
        });
        adapter.setCheckChangeListener(new NorMalScanAdapter.CheckChangeListener() {
            @Override
            public void onCheckChangeListener(@NonNull PropertyBean bean, int position, boolean isCheck) {
                adapter.getData().get(position).setValue(isCheck + "");
            }
        });
    }

    @Override
    public void onEditorActionListener(@Nullable EditText view, @NonNull PropertyBean bean, int position) {
        if (view != null && view.getText() != null && !view.getText().toString().isEmpty()) {
            queryData(position, view.getText().toString());
        } else {
            //空的情况，空回车，重置这个项的值，仓库的话要关联仓位重现处理 对比采集界面处理
            if (view != null && view.getText() != null && view.getText().toString().isEmpty()) {
                if (bean.getKey().equals("FStockId") || bean.getKey().equals("FInStockId")) {
                    initStockItem(bean, position);
                    adapter.focusMoveDown(position);
                } else {
                    initItem(bean, position);
                    adapter.focusMoveDown(position);
                }
            } else {
                adapter.focusMoveDown(position);
            }
        }
    }

    public void onCompleteUpdateView(int position, AnalysisResp analysisResp) {
        //光标下移
        adapter.focusMoveDown(position);
    }

    public void setViewAndData(List<PropertyBean> propertyBeanLists, AnalysisResp analysisResp) {
        editList = new ArrayList<>();
        for (int i = 0; i < propertyBeanLists.size(); i++) {
            String key = propertyBeanLists.get(i).getKey();
            String type = propertyBeanLists.get(i).getType(); // 字段类型
            if (key.equals("FBarCodeId")) continue;
            if (key.equals("FQty")) continue;
            Map<String, BarcodeBean> map = analysisResp.getFBillHead().get(0);
            // if(map.get(key).isEnabled()){
            String val = "ASSISTANT".equals(type) || "BASEDATA".equals(type) ? map.get(key).getNumber() : map.get(key).getValue();
            propertyBeanLists.get(i).setEnable(true);
            propertyBeanLists.get(i).setValue(val);
            editList.add(propertyBeanLists.get(i));
            //  }
        }

    }

    public void setViewAndData(List<PropertyBean> propertyBeanLists) {
        editList = new ArrayList<>();
        for (int i = 0; i < propertyBeanLists.size(); i++) {
            String key = propertyBeanLists.get(i).getKey();
            String type = propertyBeanLists.get(i).getType(); // 字段类型
            if (key.equals("FBarCodeId")) continue;
            List<NormalScanConfigBean.PropertyInfo> propertyInfos = normalScanConfigBean.getGathers();
            for (NormalScanConfigBean.PropertyInfo p : propertyInfos) {
                if (propertyBeanLists.get(i).getKey().equals(p.getProperty().getKey()) && p.isEdit()) {
                    PropertyBean propertyBean = new PropertyBean(key, propertyBeanLists.get(i).getName());
                    propertyBean.setType(type);
                    propertyBean.setName(propertyBeanLists.get(i).getName());
                    propertyBean.setTag(propertyBeanLists.get(i).getTag());
                    propertyBean.setEntity(propertyBeanLists.get(i).getEntity());
                    propertyBean.setEntityId(propertyBeanLists.get(i).getEntityId());
                    propertyBean.setParentId(propertyBeanLists.get(i).getParentId());
                    propertyBean.setSource(propertyBeanLists.get(i).getSource());
                    propertyBean.setEnums(propertyBeanLists.get(i).getEnums());
                    propertyBean.setRelated(propertyBeanLists.get(i).getRelated());
                    propertyBean.setFlexId(propertyBeanLists.get(i).getFlexId());
                    propertyBean.setEnable(true);
                    editList.add(propertyBean);
                }
            }
        }
        originalEditList = CommonUtils.deepClonePropertyBeanList(editList);
    }

    /**
     *  初始化仓库
     * @return
     */
    private void initStockItem(PropertyBean bean, int position) {
        //初始化仓库
        initItem(bean, position);
        //初始化仓位
        initPositionItem(getPositionKey(bean.getKey()));
    }

    private String getPositionKey(String key) {
        String pkey = "";
        if (key.equals("FStockId")) {
            pkey = "FStockLocId.FF";
        } else if (key.equals("FInStockId")) {
            pkey = "FInStockLocId.FF";
        }
        return pkey;
    }

    /**
     * 初始化仓位
     */
    private void initPositionItem(String pkey) {
        int firstPosition = -1;
        int endPosition = -1;
        for (int i = 0; i < editList.size(); i++) {
            if (editList.get(i).getKey().contains(pkey)) {
                if (firstPosition == -1) {
                    firstPosition = i;
                }
                endPosition = i;
            }
        }
        int num = endPosition - firstPosition + 1;
        //删除已有
        for (int i = 0; i < num; i++) {
            editList.remove(firstPosition);
        }
        //添加新的
        List<PropertyBean> items = getOriginalPositionItem(pkey);
        if (items.size() > 0) {
            editList.addAll(firstPosition, items);
            adapter.notifyItemRangeInserted(firstPosition, items.size());
        }
    }

    private List<PropertyBean> getOriginalPositionItem(String pkey) {
        List<PropertyBean> newEditList = new ArrayList<>();
        List<PropertyBean> newOriginalEditList = CommonUtils.deepClonePropertyBeanList(originalEditList);
        for (int i = 0; i < newOriginalEditList.size(); i++) {
            if (newOriginalEditList.get(i).getKey().contains(pkey)) {
                newEditList.add(newOriginalEditList.get(i));
            }
        }
        return newEditList;
    }

    /**
     *  初始化某项
     * @return
     */
    private void initItem(PropertyBean bean, int position) {
        List<PropertyBean> newOriginalEditList = CommonUtils.deepClonePropertyBeanList(originalEditList);
        for (int i = 0; i < newOriginalEditList.size(); i++) {
            if (newOriginalEditList.get(i).getKey().equals(bean.getKey())) {
                editList.set(position, newOriginalEditList.get(i));
            }
        }
        adapter.notifyItemChanged(position);
    }

    public void setDefault(List<ClientCustomParametersReq.Param> preservationParams) {
        this.preservationParams = preservationParams;
    }

    private void setDefault() {
        if (preservationParams != null && preservationParams.size() > 0) {
            int position = 0;
            for (int i = 0; i < preservationParams.size(); i++) {
                if (preservationParams.get(i).getKey().equals("control")) {
                    String json = preservationParams.get(i).getValue();
                    if (json != null && !json.isEmpty()) {
                        DefaultInfoBean defaultInfoBean = new Gson().fromJson(json, DefaultInfoBean.class);
                        cb_default.setChecked(defaultInfoBean.isDefault());
                        if ("1".equals(defaultInfoBean.getPopControl())) {
                            cb_abnormal.setChecked(true);
                        } else {
                            cb_fixed.setChecked(true);
                        }
                    } else {
                        //默认选中“仅异常时弹出”
                        if (isDefault) {
                            cb_abnormal.setChecked(true);
                        } else {
                            cb_fixed.setChecked(true);
                        }
                    }
                } else if (preservationParams.get(i).getKey().equals("editList")) {
                    position = i;
                }
            }
            String json = preservationParams.get(position).getValue();
            List<PropertyBean> newEditList = new Gson().fromJson(json, new TypeToken<List<PropertyBean>>() {
            }.getType());
            if (newEditList != null && newEditList.size() > 0) {
                PropertyBean FStockIdPropertyBean = null;
                PropertyBean FInStockIdPropertyBean = null;
                //更新默认值
                for (PropertyBean p : newEditList) {
                    for (int i = 0; i < editList.size(); i++) {
                        if (p.getKey().equals(editList.get(i).getKey())) {
                            PropertyBean newBean = p.clone();
                            editList.set(i, newBean);
                            if ("FStockId".equals(p.getKey())) {
                                FStockIdPropertyBean = newBean;
                            } else if ("FInStockId".equals(p.getKey())) {
                                FInStockIdPropertyBean = newBean;
                            }
                        }
                    }

                }
                //过滤仓位维度
                setDimension(FStockIdPropertyBean, FInStockIdPropertyBean);
                //有保存默认值以保存的默认值列表显示(如果改变的字段会有问题)
                //editList = newEditList;
                //特殊类型字段处理  比如ITEMCLASS
                setITEMCLASS();
                adapter.setNewInstance(editList);
            }

        } else {
            //默认选中“仅异常时弹出”
            if (isDefault) {
                cb_abnormal.setChecked(true);
            } else {
                cb_fixed.setChecked(true);
            }
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

    private boolean isQuery(int position) {
        PropertyBean bean = this.adapter.getData().get(position);
        if (bean.getType().equals("FLEXVALUE") || bean.getType().equals("ITEMCLASS") || bean.getType().equals("BASEDATA") || bean.getType().equals("ASSISTANT"))
            return true;
        return false;
    }

    private void queryData(int position, String val) {

        PropertyBean bean = this.adapter.getData().get(position);
        String lookupId = "";
        FiltersReq filtersReq = new FiltersReq();
        Map<String, Object> params = new HashMap<>();
        if (bean.getKey().equals("FStockId") || bean.getKey().equals("FInStockId")) {
            //库位条码特殊处理，如何判断
            params.put("primaryCode", val);
            filtersReq.setFilters(params);
            String pkey = getPositionKey(bean.getKey());
            basicDataOnEditorActionPresenter.queryBinCodeData(scene, filtersReq, position, pkey);
        } else {
            if (bean.getType().equals("DATETIME")) {
                ToastUtil.showToastCenter("日期只支持选择");
                return;
            }
            if (bean.getType().equals("COMBOBOX")) {
                ToastUtil.showToastCenter("下拉类别类型只支持选择");
                return;
            }
            if (bean.getType().equals("FLEXVALUE") || bean.getType().equals("ITEMCLASS") || bean.getType().equals("BASEDATA") || bean.getType().equals("ASSISTANT")) {
                switch (bean.getType()) {
                    //仓位字段
                    case "FLEXVALUE":
                        if (bean.getParentId() == null || bean.getParentId().isEmpty()) {
                            ToastUtil.showToastCenter("请先选择" + getRelatedName(bean));
                            return;
                        }
                        lookupId = bean.getTag();
                        params.put("parentId", bean.getParentId());
                        params.put("flexId", bean.getFlexId());
                        //filtersReq.setFilters(params);
                        break;
                    case "ITEMCLASS":
                        if (bean.getParentId() != null && !bean.getParentId().isEmpty()) {
                            lookupId = bean.getTag();
                        } else {
                            //获取关联Related
                            ToastUtil.showToastCenter("请先选择" + getRelatedName(bean));
                            return;
                        }
                        break;
                    case "BASEDATA":
                        lookupId = bean.getTag();
                        break;
                    case "ASSISTANT":
                        lookupId = bean.getTag();
                        params.put("parentId", bean.getParentId());
                        //filtersReq.setFilters(params);
                        break;
                }
                params.put("primaryCode", val);
                filtersReq.setFilters(params);
                basicDataOnEditorActionPresenter.queryBasicData(scene, lookupId, filtersReq, position);
            } else {
                adapter.focusMoveDown(position);
            }

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
                    if (bean.getParentId() == null || bean.getParentId().isEmpty()) {
                        ToastUtil.showToastCenter("请先选择" + getRelatedName(bean));
                        return;
                    } else {
                        intent.putExtra("lookupId", bean.getTag());
                        intent.putExtra("parentId", bean.getParentId());
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
                //单选框   有是否选择，不独立，会相互影响，多个单选框只有一个是true
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

    private boolean isFStockLocIdOrFInStockLocIdContinue(PropertyBean bean, List<PropertyBean> newOriginalEditList) {
        String pkey = getPositionKey(bean.getKey());
        if (pkey.isEmpty()) {
            return false;
        }
        //仓库已选择，但是没关联仓位
        if ((bean.getParentId() == null || bean.getParentId().isEmpty()) && isHaveWarehouseValue(pkey, newOriginalEditList)) {
            return true;
        }
        return false;
    }

    private boolean isHaveWarehouseValue(String key, List<PropertyBean> newOriginalEditList) {
        for (int i = 0; i < newOriginalEditList.size(); i++) {
            if (newOriginalEditList.get(i).getKey().equals(key) && (newOriginalEditList.get(i).getValue() != null && !newOriginalEditList.get(i).getValue().isEmpty())) {
                return true;
            }
        }
        return false;
    }

    private void setDimension(PropertyBean FStockIdPropertyBean, PropertyBean FInStockIdPropertyBean) {
        List<PropertyBean> newEditList = new ArrayList<>();
        for (int i = 0; i < editList.size(); i++) {
            //仓库有值，过滤没有的维度
            if (editList.get(i).getType().equals("FLEXVALUE")) {
                if (editList.get(i).getKey().contains("FStockLocId.FF")) {
                    if (FStockIdPropertyBean != null && FStockIdPropertyBean.getFStockFlexItem() != null) {
                        for (int j = 0; j < FStockIdPropertyBean.getFStockFlexItem().size(); j++) {
                            for (String keyName : FStockIdPropertyBean.getFStockFlexItem().get(j).keySet()) {
                                if (keyName.equals("flexId")) {
                                    String pKey = "FStockLocId.FF" + new BigDecimal(FStockIdPropertyBean.getFStockFlexItem().get(j).get(keyName).toString()).stripTrailingZeros().toPlainString();
                                    if (editList.get(i).getKey().equals(pKey)) {
                                        newEditList.add(editList.get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        //仓库没有维度不添加
                    }
                } else if (editList.get(i).getKey().contains("FInStockLocId.FF")) {
                    if (FInStockIdPropertyBean != null && FInStockIdPropertyBean.getFStockFlexItem() != null) {
                        for (int j = 0; j < FInStockIdPropertyBean.getFStockFlexItem().size(); j++) {
                            for (String keyName : FInStockIdPropertyBean.getFStockFlexItem().get(j).keySet()) {
                                if (keyName.equals("flexId")) {
                                    String pKey = "FInStockLocId.FF" + new BigDecimal(FInStockIdPropertyBean.getFStockFlexItem().get(j).get(keyName).toString()).stripTrailingZeros().toPlainString();
                                    if (editList.get(i).getKey().equals(pKey)) {
                                        newEditList.add(editList.get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        //仓库没有维度不添加
                    }
                }
            } else {
                newEditList.add(editList.get(i));
            }
        }
        editList = newEditList;
    }

    /**
     * 设置仓位维度显示,并返回仓库的位置
     */
    private int setDimension(BaseInfoBean infoBean, int position) {
        //FStockId   FStockLocId
        //FInStockId  FInStockLocId
        //基础资料返回仓库信息，根据返回的维度过滤 FInStockLocId+返回数据 过滤
        //先赋值到原始列表中
        List<PropertyBean> newOriginalEditList = CommonUtils.deepClonePropertyBeanList(originalEditList);
        for (int i = 0; i < editList.size(); i++) {
            for (int j = 0; j < newOriginalEditList.size(); j++) {
                if (editList.get(i).getKey().equals(newOriginalEditList.get(j).getKey())) {
                    newOriginalEditList.set(j, editList.get(i));
                    // Log.e("setDimension","value = "+editList.get(i).getValue());
                }
            }
        }
        String key = editList.get(position).getKey();
        if ("FStockId".equals(key) || "FInStockId".equals(key)) {
            String let_key;
            if ("FStockId".equals(key)) {
                let_key = "FStockLocId.FF";
            } else {
                let_key = "FInStockLocId.FF";
            }
            if (infoBean.getFStockFlexItem() != null && infoBean.getFStockFlexItem().size() > 0) {
                List<PropertyBean> newEditList = new ArrayList<>();
                for (int i = 0; i < newOriginalEditList.size(); i++) {
                    if (!newOriginalEditList.get(i).getKey().contains(let_key)) {
                        //同时存在FStockId 和 FInStockId 时 要特殊处理
                        if (!isFStockLocIdOrFInStockLocIdContinue(newOriginalEditList.get(i), newOriginalEditList)) {
                            newEditList.add(newOriginalEditList.get(i));
                        }
                    } else {
                        for (int j = 0; j < infoBean.getFStockFlexItem().size(); j++) {
                            for (String keyName : infoBean.getFStockFlexItem().get(j).keySet()) {
                                if (keyName.equals("flexId")) {
                                    String pKey = let_key + new BigDecimal(infoBean.getFStockFlexItem().get(j).get(keyName).toString()).stripTrailingZeros().toPlainString();
                                    if (newOriginalEditList.get(i).getKey().equals(pKey)) {
                                        newEditList.add(newOriginalEditList.get(i));
                                    }
                                }
                            }
                        }
                    }
                }

                editList = newEditList;
                initAdapter();
                //仓库有可能会变动，这里调整position
                int newPosition = 0;
                for (int i = 0; i < editList.size(); i++) {
                    if (editList.get(i).getKey().equals(key)) {
                        newPosition = i;
                        //存储关联的仓位维度
                        editList.get(i).setFStockFlexItem(infoBean.getFStockFlexItem());
                    }
                }
                rlv_scan.scrollToPosition(position);
                return newPosition;
            } else {
                //不显示任何仓位
                List<PropertyBean> newEditList = new ArrayList<>();
                for (int i = 0; i < newOriginalEditList.size(); i++) {
                    if (!newOriginalEditList.get(i).getKey().contains(let_key)) {
                        if (!isFStockLocIdOrFInStockLocIdContinue(newOriginalEditList.get(i), newOriginalEditList)) {
                            newEditList.add(newOriginalEditList.get(i));
                        }
                    }
                }
                editList = newEditList;
                initAdapter();
                //仓库有可能会变动，这里调整position
                int newPosition = 0;
                for (int i = 0; i < editList.size(); i++) {
                    if (editList.get(i).getKey().equals(key)) {
                        newPosition = i;
                        editList.get(i).setFStockFlexItem(infoBean.getFStockFlexItem());
                    }
                }
                rlv_scan.scrollToPosition(position);
                return newPosition;
            }

        }
        return position;
    }

    private void setRelated(BaseInfoBean infoBean, int position) {
        //如果选择了仓库，清空仓位，并重现关联
        if ("FStockId".equals(adapter.getData().get(position).getKey())) {
            for (int i = 0; i < adapter.getData().size(); i++) {
                if ("FLEXVALUE".equals(adapter.getData().get(i).getType()) && adapter.getData().get(i).getKey().contains("FStockLocId.FF")) {
                    adapter.getData().get(i).setValue("");
                    adapter.getData().get(i).setParentId(infoBean.getId());
                    adapter.notifyItemChanged(i);
                }
            }
        } else if ("FInStockId".equals(adapter.getData().get(position).getKey())) {
            for (int i = 0; i < adapter.getData().size(); i++) {
                if ("FLEXVALUE".equals(adapter.getData().get(i).getType()) && adapter.getData().get(i).getKey().contains("FInStockLocId.FF")) {
                    adapter.getData().get(i).setValue("");
                    adapter.getData().get(i).setParentId(infoBean.getId());
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }

    private void initRADIOBOX(int pos) {
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asBottomList(editList.get(pos).getName(), new String[]{"是", "否"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if (text.equals("是")) {
                                    editList.get(pos).setValue("true");
                                } else {
                                    editList.get(pos).setValue("false");
                                }
                                adapter.notifyItemChanged(pos);
                                adapter.focusMoveDown(pos);
                                for (int i = 0; i < editList.size(); i++) {
                                    if (editList.get(i).getType().equals("RADIOBOX") && i != pos) {
                                        if (text.equals("是")) {
                                            editList.get(i).setValue("false");
                                            adapter.notifyItemChanged(i);
                                        }
                                    }
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
                                    editList.get(pos).setValue("true");
                                } else {
                                    editList.get(pos).setValue("false");
                                }
                                adapter.notifyItemChanged(pos);
                                adapter.focusMoveDown(pos);
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

                                //关联Related
                                boolean isRelated = false;
                                for (int i = 0; i < editList.size(); i++) {
                                    if (editList.get(pos).getKey().equals(editList.get(i).getRelated())) {
                                        if (!(Enums.get(position).get("Value").equals(editList.get(i).getParentId()))) {
                                            isRelated = true;
                                            editList.get(i).setValue("");
                                            editList.get(i).setParentId(Enums.get(position).get("Value"));
                                            adapter.notifyItemChanged(i);
                                            editList.get(pos).setValue(Enums.get(position).get("Value"));
                                            adapter.notifyItemChanged(pos);
                                            //更新视图，这里默认不用更新
                                            adapter.focusMoveDown(pos);
                                        }

                                    }
                                }
                                if (!isRelated) {
                                    editList.get(pos).setValue(Enums.get(position).get("Value"));
                                    adapter.notifyItemChanged(pos);
                                    adapter.focusMoveDown(pos);
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
            adapter.getData().get(pos).setValue(time);
            adapter.setEditTextValue(pos, time);
        }, beginTimestamp, endTimestamp);

        picker.setCancelable(false);
        picker.setCanShowPreciseTime(false);
        picker.setScrollLoop(false);
        picker.setCanShowAnim(false);
        picker.show(System.currentTimeMillis());
    }

    public void setUpdataEditTextValue(int position, String content, BaseInfoBean infoBean) {
        if (adapter != null) {
            //选择仓库后过滤维度仓位维度
            position = setDimension(infoBean, position);
            adapter.setEditTextValue(position, content);
            adapter.getData().get(position).setValue(content);
            //设置关联
            setRelated(infoBean, position);
        }
    }

    private void setUpDataByOnPosition(int position, List<BaseInfoBean> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < editList.size(); j++) {
                if (list.get(i).getKey().equals(editList.get(j).getKey())) {
                    //setUpDataByOnEditorAction(j,null != list.get(i).getCode() ? list.get(i).getCode() : list.get(i).getName(),list.get(i));
                    adapter.getData().get(j).setValue(null != list.get(i).getCode() ? list.get(i).getCode() : list.get(i).getName());
                    adapter.notifyItemChanged(j);
                }
            }
        }
        adapter.focusMoveDown(position);
    }

    public void setUpDataByOnEditorAction(int position, String content, BaseInfoBean infoBean) {
        if (adapter != null) {
            //选择仓库后过滤维度仓位维度
            position = setDimension(infoBean, position);
            adapter.getData().get(position).setValue(content);
            //设置关联
            setRelated(infoBean, position);
            adapter.focusMoveDown(position);
        }
    }

    public List<PropertyBean> getDefaultConfigure() {
        //只保存有值的
        List<PropertyBean> saveList = new ArrayList<>();
        for (int i = 0; i < editList.size(); i++) {
            if (editList.get(i).getValue() != null && !editList.get(i).getValue().isEmpty()) {
                saveList.add(editList.get(i));
            }
        }
        return saveList;
    }

    public String getControl() {
        DefaultInfoBean defaultInfoBean = new DefaultInfoBean();
        defaultInfoBean.setDefault(cb_default.isChecked());
        if (cb_abnormal.isChecked()) {
            defaultInfoBean.setPopControl("1");
        } else {
            defaultInfoBean.setPopControl("2");
        }
        String json = new Gson().toJson(defaultInfoBean);
        return json;
    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void showBasicDataList(@Nullable List<? extends BaseInfoBean> list, int postion) {
        if (list.size() == 0) {
            ToastUtil.showToastCenter(editList.get(postion).getName() + ":" + adapter.getEditTextString(postion) + "找不到相应的数据");
            adapter.setEditTextValue(postion, "");
            adapter.getData().get(postion).setValue("");
            return;
        } else {
            setUpDataByOnEditorAction(postion,
                    null != list.get(0).getCode() ? list.get(0).getCode() : list.get(0).getName(), list.get(0));
            if (list.size() > 1) {
                //扫描库位条码返回，仓位赋值处理
                setUpDataByOnPosition(postion, (List<BaseInfoBean>) list.subList(1, list.size()));
            }
        }
    }


    public interface onConfirmClickListener {
        void onConfirmClickListener(String code);
    }

    public void setonConfirmClickListener(onConfirmClickListener listener) {
        this.listener = listener;
    }

    public interface DismissListener {
        void onDismissListener();
    }

    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dismissListener != null) {
            dismissListener.onDismissListener();
        }
    }
}
