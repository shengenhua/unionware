package com.unionware.wms.ui.activity;


import android.content.Intent;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.unionware.wms.R;
import com.unionware.wms.databinding.CommonSwipeListBinding;
import com.unionware.wms.inter.scan.BarcodeEditContract;
import com.unionware.wms.inter.scan.BarcodeEditPresenter;
import com.unionware.wms.model.event.EditIndexEvent;
import com.unionware.wms.ui.adapter.BarcodeListAdapter;
import com.unionware.wms.utlis.CommonUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.app.utils.DateFormatUtils;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;
import unionware.base.model.bean.BaseInfoBean;
import unionware.base.model.bean.EntityBean;
import unionware.base.model.req.ItemBean;
import unionware.base.model.req.ViewReq;
import unionware.base.ui.datepicker.CustomDatePicker;

/**
 * 逐条编辑
 */
@AndroidEntryPoint
public class BarcodeEditActivity extends BaseBindActivity<CommonSwipeListBinding> implements BarcodeEditContract.View {
    private BarcodeListAdapter adapter;
    private ViewReq req;

    @Inject
    BarcodeEditPresenter presenter;

    @Override
    public int onBindLayout() {
        return R.layout.common_swipe_list;
    }

    @Override
    public void initView() {
        presenter.attach(this);
    }

    @Override
    public void initData() {
        boolean isContainer = getIntent().getBooleanExtra("isContainer", false);
        req = new ViewReq(getIntent().getStringExtra("pageId"));
        getMBind().toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
        getMBind().tvTabTitle.setText(isContainer ? "删除明细" : "逐条编辑");
        List<List<EntityBean>> list = (List<List<EntityBean>>) getIntent().getSerializableExtra("list");
        adapter = new BarcodeListAdapter(this, list);
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(this));
        getMBind().rvList.setAdapter(adapter);

    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void onSuccess(int pos) {
        adapter.removeAt(pos);
        ToastUtil.showToastCenter("删除成功");
    }

    @Override
    public void onUpdateSuccessEvent() {

    }


    @Override
    public boolean enableEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateItems(EntityBean bean) {
        updateItem(bean.getKey(), bean.getValue(), bean.getIndex());
    }

    /**
     * 执行更新
     *
     * @param key key 标识
     * @param val 用户书写的val值
     * @param pos 选择更新的单据体的pos （index）
     */
    private void updateItem(String key, String val, int pos) {
        ItemBean item = new ItemBean(key, val, pos);
        List<ItemBean> list = new ArrayList<>();
        list.add(item);
        if (req != null) {
            req.setItems(list);
            req.setParams(new HashMap<>());
            presenter.updateScanView(req);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upDateBaseInfo(BaseInfoBean infoBean) {
        EntityBean bean = adapter.getData().get(infoBean.getIndex()).get(infoBean.getPos());
        bean.setValue(infoBean.getCode());
        bean.setId(CommonUtils.isInteger(infoBean.getId()) ? Integer.valueOf(infoBean.getId()) : 0); // 辅助属性id不是数字
//        adapter.notifyItem(infoBean.getIndex());
        updateItem(bean.getKey(), infoBean.getCode(), infoBean.getIndex());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void jumToBaseInfo(EditIndexEvent event) {
        EntityBean bean = (EntityBean) adapter.getData().get(event.getIndex()).get(event.getPos());
        if ("BASEDATA".equals(bean.getType()) || "ASSISTANT".equals(bean.getType())) {
            Intent intent = new Intent(this, BaseInfoListActivity.class);
            intent.putExtra("key", bean.getTag());
            intent.putExtra("parentId", bean.getProperty().getParentId());
            intent.putExtra("position", event.getPos());
            intent.putExtra("isEdit", true);
            intent.putExtra("index", bean.getIndex());
            startActivity(intent);
        } else if ("DATETIME".equals(bean.getType())) {
            initTimePick(event.getEditText(), bean);
        }
    }


    private void initTimePick(EditText editText, EntityBean bean) {
        //时间选择器
        long beginTimestamp = DateFormatUtils.str2Long("1980-01-01", false);
        long endTimestamp = DateFormatUtils.str2Long("2100-01-01", false);
        CustomDatePicker picker = new CustomDatePicker(this, timestamp -> {
            String time = DateFormatUtils.long2Str(timestamp, false);
            bean.setValue(time);
            editText.setText(time);
            ItemBean item = new ItemBean(bean.getProperty().getKey(), editText.getText().toString(), bean.getIndex());
            List<ItemBean> list = new ArrayList<>();
            list.add(item);
            if (req != null) {
                req.setItems(list);
                req.setParams(new HashMap<>());
                presenter.updateScanView(req);
            }
        }, beginTimestamp, endTimestamp);

        picker.setCancelable(false);
        // 不显示时和分
        picker.setCanShowPreciseTime(false);
        // 不允许循环滚动
        picker.setScrollLoop(false);
        // 不允许滚动动画
        picker.setCanShowAnim(false);
        picker.show(System.currentTimeMillis());
    }

}
