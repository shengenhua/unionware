package com.unionware.wms.inter.trans;

import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;
import unionware.base.model.bean.PrintTemplateBean;

import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2023/6/7 10:32
 * @Description : PrintTemplateContract
 */

public interface PrintTemplateContract {
    interface View extends IView {
        void showPrintTemplate(List<PrintTemplateBean> list);

        void showFailedView(String msg);
    }
    interface Persenter extends IPresenter<View> {
        void getPrintTemplate(String formId);//获取套打模板
    }
}
