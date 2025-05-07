package com.unionware.wms.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lxj.xpopup.XPopup;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.unionware.wms.R;
import com.unionware.wms.URLPath;
import com.unionware.wms.databinding.WmsRegularScanCommonListFragmentBinding;
import com.unionware.wms.inter.wms.scan.WMSRegularScanCommonListContract;
import com.unionware.wms.inter.wms.scan.WMSRegularScanCommonListPresenter;
import com.unionware.wms.model.bean.NormalScanConfigBean;
import com.unionware.wms.model.event.NormalScanCreateEvent;
import com.unionware.wms.model.event.RefreshSourceOrTaskListEvent;
import com.unionware.wms.ui.activity.NormalScanActivity;
import com.unionware.wms.ui.adapter.WMSBillInfoAdapter;

import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.model.bean.BillBean;
import unionware.base.model.bean.MenuBean;
import unionware.base.model.req.FiltersReq;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WMSRegularScanCommonListFragment extends BaseBindFragment<WmsRegularScanCommonListFragmentBinding> implements WMSRegularScanCommonListContract.View,
        OnRefreshListener, OnLoadMoreListener, OnItemClickListener, TextView.OnEditorActionListener, SwipeMenuCreator, OnItemMenuClickListener {
    private final List<BillBean> selectBillInfo = new ArrayList<>();
    private WMSBillInfoAdapter adapter;
    private FiltersReq req;
    private MenuBean bean;
    private NormalScanConfigBean normalScanConfigBean;
    private String name;
    private ActivityResultLauncher<Intent> launcher;
    private View emptyView;
    private boolean isActiveSearch = false;
    @Inject
    WMSRegularScanCommonListPresenter presenter;


    public static WMSRegularScanCommonListFragment newInstance(MenuBean bean, String name, NormalScanConfigBean normalScanConfigBean) {
        Bundle args = new Bundle();
        WMSRegularScanCommonListFragment fragment = new WMSRegularScanCommonListFragment();
        args.putSerializable("bean", bean);
        args.putSerializable("normalScanConfigBean", normalScanConfigBean);
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach(this);
        registerActivityResult();
    }


    @Override
    public void initData() {
        emptyView = getLayoutInflater().inflate(unionware.base.R.layout.view_empty, null);
        ImageView iv_empty = emptyView.findViewById(unionware.base.R.id.iv_empty_icon);
        TextView tv_tips = emptyView.findViewById(unionware.base.R.id.tv_empty_tips);
        iv_empty.setImageResource(unionware.base.R.mipmap.ic_empty_bill);
        name = getArguments().getString("name");
        bean = (MenuBean) getArguments().getSerializable("bean");
        normalScanConfigBean = (NormalScanConfigBean) getArguments().getSerializable("normalScanConfigBean");
        getMBind().layoutSearch.etInProgressSearch.setOnEditorActionListener(this);
        getMBind().rvList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getMBind().layoutSearch.etInProgressSearch.requestFocus();
                getMBind().rvList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        tv_tips.setText(URLPath.WMS.MENU_WMS_APP_SCAN_TASK.equals(name) ? "还没有任务哦～" : "还没有单据哦～");
        Map<String, Object> map = new HashMap<>();
        if (URLPath.WMS.MENU_WMS_APP_SCAN_TASK.equals(name)) { // 任务列表
            map.put("setId", bean.getId());
            map.put("jobFlowId", Integer.valueOf(normalScanConfigBean.getId()));
            req = new FiltersReq(1, map);
        } else {
            req = new FiltersReq(1, map);
            Map<String, Object> params = new HashMap<>();
            params.put("jobFlowId", Integer.valueOf(normalScanConfigBean.getId()));
            req.setParams(params);
        }
        adapter = new WMSBillInfoAdapter(1, getActivity());
        adapter.setOnItemClickListener(this);
        if (URLPath.WMS.MENU_WMS_APP_SCAN_TASK.equals(name)) { // 任务列表
            getMBind().rvList.setSwipeMenuCreator(this);
            getMBind().rvList.setOnItemMenuClickListener(this);
        }
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(mContext));
        getMBind().rvList.setAdapter(adapter);
        getMBind().smRefresh.setOnRefreshListener(this);
        getMBind().smRefresh.setOnLoadMoreListener(this);
        isActiveSearch = false;
        presenter.requestList(bean.getScene(), name, req);
        if (!URLPath.WMS.MENU_WMS_APP_SCAN_TASK.equals(name)
                && normalScanConfigBean.getMultiCombineScan()
                && normalScanConfigBean.getMultiMode().equals("1")) {
            //作业流程配置启用多单合并扫描，且多单模式为手工选择源单时，单据列表支持多选
            adapter.setSelect(true);
            adapter.setOnItemCheckedListener((BillBean bean, boolean isSelect) -> {
                if (isSelect) {
                    if (selectBillInfo.stream().noneMatch(billBean -> bean.getId().equals(billBean.getId()))) {
                        selectBillInfo.add(bean);
                    }
                } else {
                    if (selectBillInfo.stream().anyMatch(billBean -> bean.getId().equals(billBean.getId()))) {
                        selectBillInfo.removeIf(billBean -> bean.getId().equals(billBean.getId()));
                    }
                }
            });
        }
    }


    private void registerActivityResult() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == URLPath.Stock.PATH_SUBMIT_FINISH) {
                req.setIndex(1);
                isActiveSearch = false;
                presenter.requestList(bean.getScene(), name, req);
            }
        });
    }

    @Override
    public void showList(List<BillBean> list) {
        list.forEach(billBean -> {
            if (selectBillInfo.stream().anyMatch(bean -> bean.getId().equals(billBean.getId()))) {
                billBean.setSelect(true);
            }
        });
        if (1 == req.getPageIndex()) {
            // adapter.getData().clear();
            adapter.removeEmptyView();
            adapter.setNewInstance(list);
            if (isActiveSearch
                    && !adapter.isSelect()
                    && list != null
                    && list.size() == 1
                    && req.getFilters() != null
                    && req.getFilters().containsKey("keyword")
                    && req.getFilters().get("keyword") != null
                    && !req.getFilters().get("keyword").toString().isEmpty()) {
                jumpToScanActivity(adapter, 0);
            }
        } else {
            adapter.addData(list);
        }
        if (URLPath.WMS.MENU_WMS_APP_SCAN_TASK.equals(name)) {
            getMBind().rvList.setSwipeItemMenuEnabled(adapter.getData().size() != 0);
        }

    }

    @Override
    public void showEmptyView() {
        if (URLPath.WMS.MENU_WMS_APP_SCAN_TASK.equals(name)) {
            getMBind().rvList.setSwipeItemMenuEnabled(false);
        }
        adapter.getData().clear();
        adapter.notifyDataSetChanged();
        adapter.setEmptyView(emptyView);
    }

    @Override
    public void upDataByTaskCancel(int position) {
        adapter.removeAt(position);
        if (adapter.getData().isEmpty() && URLPath.WMS.MENU_WMS_APP_SCAN_TASK.equals(name)) {
            getMBind().rvList.setSwipeItemMenuEnabled(false);
            adapter.setEmptyView(emptyView);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        String keyword = textView.getText().toString().trim();
        Map<String, Object> filter = req.getFilters();
        filter.put("keyword", keyword);
        req.setIndex(1);
        isActiveSearch = true;
        presenter.requestList(bean.getScene(), name, req);
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (null != req && isVisibleToUser) {
            req.setIndex(1);
            isActiveSearch = false;
            presenter.requestList(bean.getScene(), name, req);
        }

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        int index = req.getPageIndex();
        req.setIndex(index + 1);
        isActiveSearch = false;
        presenter.requestList(bean.getScene(), name, req);
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        adapter.getData().clear();
        req.setIndex(1);
        if (getMBind().layoutSearch.etInProgressSearch.getText().toString().isEmpty()) {
            req.getFilters().remove("keyword");
        }
        isActiveSearch = false;
        presenter.requestList(bean.getScene(), name, req);
        refreshLayout.finishRefresh();
    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }


    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
        if (URLPath.WMS.MENU_WMS_APP_SCAN_TASK.equals(name)) { // 任务列表
            jumpToScanActivity(adapter, position);
        } else {
            if (normalScanConfigBean.getMultiCombineScan() && normalScanConfigBean.getMultiMode().equals("1")) {
                adapter.getData().get(position).setSelect(!adapter.getData().get(position).isSelect());
                adapter.notifyItemChanged(position);
            } else
                jumpToScanActivity(adapter, position);
        }
    }

    private void jumpToScanActivity(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, int position) {
        //点击源单列表创建
        //进行中是进入
        BillBean billBean = (BillBean) adapter.getData().get(position);
        billBean.setPrimaryId(bean.getId());
        billBean.setFlowId(bean.getFlowId());
        billBean.setFormId(name);
        Intent intent = new Intent(getActivity(), NormalScanActivity.class);
        intent.putExtra("bean", billBean);
        intent.putExtra("normalScanConfigBean", normalScanConfigBean);
        intent.putExtra("scene", bean.getScene());
        intent.putExtra("creatorId", billBean.getCreatorId() + "");
        intent.putExtra("fromPage", name);
        if (!URLPath.WMS.MENU_WMS_APP_SCAN_TASK.equals(name)) {
            intent.putExtra("createNewTask", true);
        }
        launcher.launch(intent);

    }

    @Override
    public boolean enableEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshList(RefreshSourceOrTaskListEvent event) {
        //如果当前是显示的是任务列表，在创建任务或任务提交成功后，返回刷新任务列表
        //源单在提交也要刷新
        //目前统一在当前页面，创建和提交都刷新
        if (event != null && name.equals(event.getFromPage())) {
            if (isVisible()) {
                req.setIndex(1);
                isActiveSearch = false;
                presenter.requestList(bean.getScene(), name, req);
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void createTask(NormalScanCreateEvent event) {
        if (!URLPath.WMS.MENU_WMS_APP_SCAN_TASK.equals(name)) {
            //源单列表
            if (normalScanConfigBean.getMultiCombineScan() && normalScanConfigBean.getMultiMode().equals("1")) {
                //作业流程配置启用多单合并扫描，且多单模式为手工选择源单时，单据列表支持多选，且显示【合并扫描】按钮，
                // 点击时按列表界面选择单据调用相关接口创建任务及打开扫描界面。
                //合并扫描
                /*ArrayList<String> primaryIds = new ArrayList<>();
                for (int i = 0; i < adapter.getData().size(); i++) {
                    if(adapter.getData().get(i).isSelect())
                        primaryIds.add(adapter.getData().get(i).getId());
                }

                if (primaryIds.isEmpty()) {
                    ToastUtil.showToastCenter("请选择单据");
                    return;
                }*/
                if (selectBillInfo.isEmpty()) {
                    ToastUtil.showToastCenter("请选择单据");
                    return;
                }
                ArrayList<String> primaryIds = new ArrayList<>();
                selectBillInfo.forEach(billBean -> primaryIds.add(billBean.getId()));

                BillBean billBean = new BillBean();
                billBean.setPrimaryId(bean.getId());
                billBean.setFlowId(bean.getFlowId());
                billBean.setFormId(name);
                Intent intent = new Intent(getActivity(), NormalScanActivity.class);
                intent.putExtra("bean", billBean);
                intent.putExtra("normalScanConfigBean", normalScanConfigBean);
                intent.putStringArrayListExtra("primaryIds", primaryIds);
                intent.putExtra("scene", bean.getScene());
                intent.putExtra("fromPage", name);
                intent.putExtra("createNewTask", true);
                launcher.launch(intent);
            }
        } else {
            //进行中
            if (normalScanConfigBean.getMode().equals("3")) {
                //无源单新任务扫描
                BillBean billBean = new BillBean();
                billBean.setPrimaryId(bean.getId());
                billBean.setFlowId(bean.getFlowId());
                billBean.setFormId(name);
                Intent intent = new Intent(getActivity(), NormalScanActivity.class);
                intent.putExtra("bean", billBean);
                intent.putExtra("normalScanConfigBean", normalScanConfigBean);
                intent.putExtra("scene", bean.getScene());
                intent.putExtra("fromPage", name);
                intent.putExtra("createNewTask", true);
                launcher.launch(intent);
            } else if (normalScanConfigBean.getMultiCombineScan() && normalScanConfigBean.getMultiMode().equals("2")) {
                //只要进行中
                //若作业流程配置启用多单合并扫描，
                // 且多单模式为按条码获取源单信息，则进行中列表显示【新任务扫描】按钮，点击按钮时自动打开空扫描界面。
                //新任务扫描
                BillBean billBean = new BillBean();
                billBean.setPrimaryId(bean.getId());
                billBean.setFlowId(bean.getFlowId());
                billBean.setFormId(name);
                Intent intent = new Intent(getActivity(), NormalScanActivity.class);
                intent.putExtra("bean", billBean);
                intent.putExtra("normalScanConfigBean", normalScanConfigBean);
                intent.putExtra("scene", bean.getScene());
                intent.putExtra("fromPage", name);
                intent.putExtra("createNewTask", true);
                launcher.launch(intent);
            }
        }
    }

    @Override
    public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
        menuBridge.closeMenu();
        new XPopup.Builder(mContext).asConfirm("删除", "确认该任务？", () -> {
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", Integer.parseInt(adapter.getData().get(adapterPosition).getId()));
            presenter.taskCancel(map, adapterPosition);
        }).show();
    }

    @Override
    public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setBackgroundColor(getResources().getColor(unionware.base.R.color.red))
                .setWidth(getResources().getDimensionPixelSize(unionware.base.R.dimen.dp_80))
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        rightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
    }

    @Override
    public void initObserve() {

    }
}
