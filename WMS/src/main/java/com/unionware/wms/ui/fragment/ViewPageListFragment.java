package com.unionware.wms.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.unionware.wms.R;
import com.unionware.wms.databinding.ViewpageListFragmentBinding;

import unionware.base.app.view.base.viewbinding.BaseBindFragment;
import unionware.base.model.bean.BillBean;
import com.unionware.wms.ui.adapter.SummaryContentAdapter;
import com.unionware.wms.ui.adapter.SummaryInfoAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ViewPageListFragment extends BaseBindFragment<ViewpageListFragmentBinding> {
    @Override
    public void initData() {
        List<BillBean> list = new ArrayList<>();
        list.add((BillBean) getArguments().getSerializable("bean"));
        SummaryInfoAdapter adapter = new SummaryInfoAdapter(getActivity(), list);
        getMBind().rvList.setLayoutManager(new LinearLayoutManager(mContext));
        getMBind().rvList.setAdapter(adapter);
        if (list.get(0).getList().size() > 2) {
            View footerView = LayoutInflater.from(mContext).inflate(R.layout.item_foot_view, null);
            TextView tv_tips = footerView.findViewById(R.id.tv_foot_tips);
            ImageView iv_arrow = footerView.findViewById(R.id.iv_foot_arrow);
            adapter.addFooterView(footerView);
            footerView.setOnClickListener(view -> {
                SummaryContentAdapter contentAdapter = adapter.getAdapter();
                contentAdapter.setExpand(!contentAdapter.isExpand());
                contentAdapter.notifyDataSetChanged();
                tv_tips.setText(contentAdapter.isExpand() ? "收起" : "点击查看更多");
                iv_arrow.setImageResource(contentAdapter.isExpand() ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
            });
        }

    }


    public static ViewPageListFragment newInstance(BillBean bean) {
        Bundle args = new Bundle();
        ViewPageListFragment fragment = new ViewPageListFragment();
        args.putSerializable("bean", (Serializable) bean);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void initObserve() {

    }
}
