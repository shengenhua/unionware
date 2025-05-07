package com.unionware.wms.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.unionware.wms.R;
import com.unionware.wms.URLPath;
import com.unionware.wms.databinding.CommonScanFragmentBinding;
import com.unionware.wms.inter.scan.ScanContract;
import com.unionware.wms.inter.scan.ScanPresenter;
import com.unionware.wms.inter.trans.PrintTemplateContract;
import com.unionware.wms.inter.trans.PrintTemplatePresenter;
import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;

import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.model.bean.BaseInfoBean;
import unionware.base.model.bean.EntityBean;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.event.RefreshTaskIdEvent;
import com.unionware.wms.model.req.AnalysisReq;
import com.unionware.wms.strategy.PackConfig;
import com.unionware.wms.ui.activity.BaseInfoListActivity;
import com.unionware.wms.ui.activity.InProgressListActivity;
import com.unionware.wms.ui.adapter.ScanAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.model.bean.PrintTemplateBean;

@AndroidEntryPoint
public class CommonScanFragment extends BaseBindFragment<CommonScanFragmentBinding>
        implements ScanContract.View, PrintTemplateContract.View,
        ScanAdapter.OnEditorActionChangeListener, OnItemChildClickListener {

    @Inject
    ScanPresenter presenter;
    @Inject
    PrintTemplatePresenter printPresenter;

    private ScanAdapter adapter;
    private AnalysisReq req; // 条码解析请求
    private int mBoxId; // 箱码ID
    private int mDetaliId;
    private ActivityResultLauncher<Intent> launcher;
    private List<EntityBean> list;
    private String taskId;
    private String id;
    private String packType; // 装箱类型
    private String count; // 已装件数
    private String printId; // 打印模板id

    public static CommonScanFragment newInstance(String id, String taskId) {
        Bundle args = new Bundle();
        CommonScanFragment fragment = new CommonScanFragment();
        args.putString("id", id);
        args.putString("taskId", taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach(this);
        printPresenter.attach(this);
        taskId = getArguments().getString("taskId");
        id = getArguments().getString("id");
        registerActivityResult(); // 注册监听Activity（基础资料）
        requireActivity().findViewById(R.id.tv_scan_in_progress).setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), InProgressListActivity.class);
            intent.putExtra("id", getArguments().getString("id"));
            launcher.launch(intent);
        });

    }

    private void registerActivityResult() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            if (data == null) return;
            int code = result.getResultCode(); // 更新位置
            if (code == 1001) { // 进行中
                adapter.resetData();
                taskId = data.getStringExtra("taskId");
                EventBus.getDefault().post(new RefreshTaskIdEvent(taskId)); // 发送 taskid 扫描记录
                req = new AnalysisReq(Integer.parseInt(taskId), id);
                String count = data.getStringExtra("count"); // 已装件数
                adapter.setValue("count", count);
                String capacity = data.getStringExtra("capacity"); // 箱容量
                String boxcode = data.getStringExtra("boxcode");
                adapter.setValue("FCapacity", capacity);
                if (null != boxcode) {
                    adapter.setValue("package", boxcode);
                }
                req.setCode(boxcode);
                req.setType("Pack");
                presenter.analysisBarcode(req, 0);
                adapter.findCurrentFocusable();
            } else {  // 基础资料转过来的
                BaseInfoBean infoBean = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ?
                        data.getSerializableExtra("baseInfo", BaseInfoBean.class) : (BaseInfoBean) data.getSerializableExtra("baseInfo");
                EntityBean bean = adapter.getData().get(code);
                bean.setValue(infoBean.getCode());
                bean.setId(Integer.valueOf(infoBean.getId()));
                adapter.notifyItemChanged(code);
            }
        });
    }


    @Override
    public void initData() {
        presenter.getScanConfigDetalisInfo(getArguments().getString("id"));
        /*成功则刷新扫描界面的已装件数，并清空子项条码及未勾选默认的单据体字段的值*/
        getMBind().btnScanConfirm.setOnClickListener(v -> {  // 全部更新
            if (mBoxId == 0) {
                EditText view = (EditText) adapter.getViewByPosition(0, R.id.et_scan_input);
                view.postDelayed(() -> view.requestFocus(), 100);
                ToastUtil.showToastCenter( "请先扫描包装条码！");
                return;
            }
            if (mDetaliId == 0) {
                EditText view = (EditText) adapter.getViewByPosition(adapter.getDetealisPos(), R.id.et_scan_input);
                view.postDelayed(() -> view.requestFocus(), 100);
                ToastUtil.showToastCenter("请先扫描明细条码！");
                return;
            }
            upDateInfo("0");
            upDateInfo("1");
            clearUI("FBillHead");
            adapter.notifyDataSetChanged();
        });


        /**
         * 关箱（提交）
         * 扫描界面最后一个可编辑的字段回车或点击【确认录入】按钮时，PDA端在做必填、值规范性检查后将界面数据提交到后台
         */

        getMBind().btnScanClose.setOnClickListener(view -> { // 提交(关箱)
            int pos = adapter.checkRequired();
            if (pos == -1) {
                String id = getArguments().getString("id");
                Map map = new HashMap();
                map.put("taskId", taskId);
                map.put("setId", id);
                presenter.submitInfo(map);
                adapter.findCurrentFocusable();
            } else {
                adapter.setFocusable(pos);
                String name = adapter.getData().get(pos).getName();
                ToastUtil.showToastCenter("请填写必填项【" + name + "】");
            }

        });


        /**
         * 换箱 ，
         * 则清空当前界面除设置默认以外的数据
         * 并提示“数据已暂存，如有需要可在【进行中列表】中继续操作。”，
         * 界面不需要跳转，仍继续停留在扫描界面
         */
        getMBind().btnScanChange.setOnClickListener(view -> {
            if (mBoxId == 0 && mDetaliId == 0) {
                ToastUtil.showToastCenter( "暂无数据，请先扫描后在换箱");
            } else {
                clearUI("defalut");
                ToastUtil.showToastCenter("数据已暂存，如有需要可在【进行中列表】中继续操作");
            }
        });


    }

    /**
     * 更细信息
     *
     * @param type 表示更新类型为单据头还是单据体
     */
    private void upDateInfo(String type) {
        Map<String, String> map = new HashMap<>();
        for (EntityBean bean : adapter.getData()) {
            if (null != bean.getRequired() && type.equals(bean.getRequired()) && null != bean.getValue() && !"".equals(bean.getValue())) {
                map.put(bean.getKey(), bean.getValue());
            }
        }
        if (map.isEmpty()) return;
        req.setExtendItems(map);
        req.setCodeId("0".equals(type) ? mBoxId : mDetaliId);
        req.setType("0".equals(type) ? "Pack" : "Details");
        presenter.updateBarcodeInfo(req, 0);
    }


    public void clearUI(String type) {
        switch (type) {
            case "defalut":
                list.stream().filter(it -> !it.isDefalut()).forEach(it -> it.setValue(""));
                break;

            case "FBillHead": // 清空子项条码及未勾选默认的单据体字段的值
                list.stream().filter(it -> "FQTY".equals(it.getKey()) || "details".equals(it.getKey()) || (!it.isDefalut() && (null != it.getProperties() && "FBillHead".equals(it.getProperties().getEntity())))).forEach(it -> it.setValue(""));
                break;
        }


        adapter.findCurrentFocusable();
    }

    @Override
    public void showPrintTemplate(List<PrintTemplateBean> list) {
        String[] dbs = list.stream().map(i -> i.getTempName()).toArray(String[]::new);
        new XPopup.Builder(mContext).asBottomList("请选择打印模板", dbs, (position, text) -> {
            printId = list.get(position).getTempId();
            adapter.setValue("TemplateTag", text);
            adapter.findCurrentFocusable();
        }).show();

    }

    public void showFailedView(String msg) {
        ToastUtil.showToastCenter( msg);
    }


    @Override
    public void initScanConfigItem(ScanConfigBean data) {
        packType = data.getPackType();
        list = PackConfig.getInstance(data.getOperType(), packType, data.getUnPackType()).getLocalScanConfigList();
        /*后台后期处理*/
        for (EntityBean bean : data.getEntity()) {
            if (bean.getKey() == null) continue;
            list.add(bean);
        }
        adapter = new ScanAdapter();
        adapter.addChildClickViewIds(R.id.iv_base_info_query, R.id.tv_scan_default, R.id.tv_scan_lock, R.id.tv_scan_combox);
        getMBind().rvScanList.setLayoutManager(new LinearLayoutManager(getActivity()));
        getMBind().rvScanList.setAdapter(adapter);
        adapter.setNewInstance(list);
        adapter.setOnEditorActionChangeListener(this);
        adapter.setOnItemChildClickListener(this);
        getMBind().rvScanList.postDelayed(() -> adapter.findCurrentFocusable(), 50); // 初始化焦点
    }

    @Override
    public void jumpToProgressList() {

    }

    @Override
    public void requestFocus(int pos) {
        adapter.findCurrentFocusable();
    }

    @Override
    public void resetBill(String id) {
        taskId = id;
    }


    /**
     * 解析回调
     *
     * @param position
     */
    @Override
    public void onEditorActionListener(@Nullable EditText editText, @NonNull EntityBean bean, int position) {
        bean.setValue(editText.getText().toString());
        req = new AnalysisReq(Integer.parseInt(taskId), id);
        switch (bean.getKey()) {
            case "package":
                req.setCode(editText.getText().toString().trim());
                req.setType("Pack");
                presenter.analysisBarcode(req, position);
                break;

            case "details":
                req.setCode(editText.getText().toString().trim());
                req.setType("Details");
                presenter.analysisBarcode(req, position);
                break;


            default:
                if (mBoxId == 0 && mDetaliId == 0) {
                    String tips = "";
                    if (("1".equals(packType) || "3".equals(packType))) {
                        requestFocous(0);
                        tips = "请先扫描包装条码！";
                    } else {
                        requestFocous(adapter.getDetealisPos());
                        tips = "请先扫描明细条码！";
                    }

                    ToastUtil.showToastCenter( tips);
                    return;
                }

                Map<String, String> map = new HashMap<>();
                map.put(bean.getKey(), editText.getText().toString());
                req.setExtendItems(map);
                req.setCodeId("0".equals(bean.getRequired()) || "FCapacity".equals(bean.getKey()) ? mBoxId : mDetaliId);
                req.setType("0".equals(bean.getRequired()) || "FCapacity".equals(bean.getKey()) ? "Pack" : "Details");
                req.setAutoComfirmCode(true);
                presenter.updateBarcodeInfo(req, position);
                break;
        }


    }

    private void requestFocous(int pos) {
        EditText view = (EditText) adapter.getViewByPosition(pos, R.id.et_scan_input);
        view.postDelayed(() -> view.requestFocus(), 100);
    }

    @Override
    public void showSuccessUpdateEvent() {
        adapter.findCurrentFocusable();
        ToastUtil.showToastCenter( "更新成功");
    }

    /*提交成功*/
    @Override
    public void submitSuccessEvent() {
        clearUI("defalut");
        ToastUtil.showToastCenter( "提交成功");
    }

    @Override
    public void showSuccessAnalysisEvent(BarcodeDetailsInfoBean data, int pos) {
        mBoxId = data.getId();
        req.setCodeId(data.getId());
        presenter.confirmInfo(req, pos);
    }

    @Override
    public void showSuccessDetailsEvent(BarcodeDetailsInfoBean data, int pos) {
        adapter.setValue("FQTY", data.getQty());
        mDetaliId = data.getId();
        req.setCodeId(data.getId());
        presenter.confirmInfo(req, pos);

    }

    @Override
    public void showFailAnalysisEvent(int pos, String msg) {
        ToastUtil.showToastCenter( msg);
        EditText editText = (EditText) adapter.getViewByPosition(pos, R.id.et_scan_input);
        editText.setSelectAllOnFocus(true);
        editText.setSelection(0, editText.getText().length());

    }

    @Override
    public void showTipsAnalysisEvent(int pos, AnalysisReq request, String msg) {
        new XPopup.Builder(mContext).dismissOnTouchOutside(false)
                .asConfirm("提示", msg, () -> {
                    presenter.confirmInfo(request, pos);
                }).show();
    }

    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
        int id = view.getId();
        if (id == R.id.iv_base_info_query) {
            EntityBean bean = (EntityBean) adapter.getData().get(position);
            Intent intent = new Intent(getActivity(), BaseInfoListActivity.class);
            intent.putExtra("key", bean.getProperties().getTag());
            intent.putExtra("position", position);
            launcher.launch(intent);
        } else if (id == R.id.tv_scan_lock) { // 锁定
            EditText editText = getMBind().rvScanList.getChildAt(position).findViewById(R.id.et_scan_input); //获取当前文本输入框
            TextView textView = getMBind().rvScanList.getChildAt(position).findViewById(R.id.tv_scan_lock);
            EntityBean bean = (EntityBean) adapter.getData().get(position);
            boolean isLock = bean.isLock(); // 当前锁住状态
            bean.setLock(!isLock);
            textView.setText(!isLock ? "已锁定" : "锁定");
            requireViewFocusable(editText, !bean.isLock());
            adapter.findCurrentFocusable();
        } else if (id == R.id.tv_scan_default) { // 默认
            EditText editText = getMBind().rvScanList.getChildAt(position).findViewById(R.id.et_scan_input); //获取当前文本输入框
            TextView textView = getMBind().rvScanList.getChildAt(position).findViewById(R.id.tv_scan_default);
            EntityBean bean = adapter.getData().get(position);
            boolean isDefault = bean.isDefalut(); // 当前默认状态
            bean.setDefalut(!isDefault);
            textView.setText(!isDefault ? "已默认" : "默认");
            requireViewFocusable(editText, !bean.isDefalut());
            adapter.findCurrentFocusable();
        } else if (id == R.id.tv_scan_combox) {
            printPresenter.getPrintTemplate(URLPath.Trans.PATH_TRANS_FORM_ID);
        }
    }

    private void requireViewFocusable(EditText view, boolean enable) {
        view.setEnabled(enable);
        view.setFocusable(enable);
        view.setFocusableInTouchMode(enable);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(RefreshTaskIdEvent event) {
    }

    @Override
    public boolean enableEventBus() {
        return true;
    }

    @Override
    public void initObserve() {

    }
}
