package com.unionware.wms.ui.activity;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.unionware.wms.R;
import com.unionware.wms.databinding.PrintTemplateListActivityBinding;
import com.unionware.wms.inter.trans.PrintTemplateContract;
import com.unionware.wms.inter.trans.PrintTemplatePresenter;
import com.unionware.wms.ui.adapter.PrintTemplateAdapter;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;
import unionware.base.model.bean.PrintTemplateBean;

/**
 * @Author : pangming
 * @Time : On 2023/6/7 10:27
 * @Description : PrintTemplateListActivity
 */
@AndroidEntryPoint
public class PrintTemplateListActivity extends BaseBindActivity<PrintTemplateListActivityBinding> implements PrintTemplateContract.View, OnItemClickListener {

    @Inject
    PrintTemplatePresenter presenter;
    private PrintTemplateAdapter adapter;
    private String formId;

    @Override
    public int onBindLayout() {
        return R.layout.print_template_list_activity;
    }

    @Override
    public void initView() {
        presenter.attach(this);
        getMBind().toolbar.setNavigationOnClickListener(v -> finish());
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PrintTemplateAdapter();
        getMBind().rvList.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        formId = getIntent().getStringExtra("formId");
        presenter.getPrintTemplate(formId);
    }

    @Override
    public void showPrintTemplate(List<PrintTemplateBean> list) {
        adapter.setList(list);
    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        Intent intent = new Intent();
        intent.putExtra("tempId", ((PrintTemplateBean) adapter.getData().get(position)).getTempId());
        intent.putExtra("tempName", ((PrintTemplateBean) adapter.getData().get(position)).getTempName());
        //intent.putExtra("position",position);
        setResult(100, intent);
        finish();
    }
}
