package com.unionware.wms.inter.trans;

import com.unionware.wms.api.PackingApi;
import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.bean.PrintTemplateBean;
import unionware.base.model.req.PrintTemplateReq;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

import java.util.List;

import javax.inject.Inject;

/**
 * @Author : pangming
 * @Time : On 2023/6/7 10:41
 * @Description : PrintTemplatePresenter
 */

public class PrintTemplatePresenter extends BasePresenter<PrintTemplateContract.View> implements PrintTemplateContract.Persenter {
    PackingApi api;

    @Inject
    public PrintTemplatePresenter(PackingApi api) {
        this.api = api;
    }

    @Override
    public void getPrintTemplate(String formId) {
        NetHelper.request(api.getPrintTemplate(new PrintTemplateReq(formId)), mView, new ICallback<List<PrintTemplateBean>>() {
            @Override
            public void onSuccess(List<PrintTemplateBean> data) {
                mView.showPrintTemplate(data);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }
}
