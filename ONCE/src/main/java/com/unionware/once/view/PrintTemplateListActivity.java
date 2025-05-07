package com.unionware.once.view;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.unionware.basicui.setting.SettingViewModel;
import com.unionware.once.R;
import com.unionware.once.adapter.TListAdapter;
import com.unionware.once.databinding.PrintTemplateActivityBinding;
import com.unionware.once.viewmodel.PrintTemplateViewModel;

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
public class PrintTemplateListActivity extends BaseBindActivity<PrintTemplateActivityBinding> {

    PrintTemplateViewModel viewModel;
    private TListAdapter<PrintTemplateBean> adapter;
    private String formId;

    @Override
    public int onBindLayout() {
        return R.layout.print_template_activity;
    }

    @Override
    public void initView() {
//        presenter.attach(this);
        viewModel = new ViewModelProvider(this).get(PrintTemplateViewModel.class);
        this.getLifecycle().addObserver(viewModel);

        getMBind().toolbar.setNavigationOnClickListener(v -> finish());
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TListAdapter<>() {
            @NonNull
            @Override
            public String onString(@Nullable PrintTemplateBean item) {
                return item != null ? item.getTempName() : "";
            }
        };
        getMBind().rvList.setAdapter(adapter);
        adapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
            PrintTemplateBean item = adapter.getItem(position);
            Intent intent = new Intent();
            if (item != null) {
                intent.putExtra("tempId", item.getTempId());
                intent.putExtra("tempName", item.getTempName());
            }
            setResult(100, intent);
            finish();
        });
    }

    @Override
    public void initListener() {
        super.initListener();
        viewModel.getPrintTemplateLiveData().observe(this, printTemplateBeans -> {
            adapter.submitList(printTemplateBeans);
        });
        viewModel.getFailureLiveData().observe(this, s -> {
            ToastUtil.showToastCenter(s);
        });
    }

    @Override
    public void initData() {
        formId = getIntent().getStringExtra("formId");
        viewModel.getPrintTemplate(formId);
    }
}
