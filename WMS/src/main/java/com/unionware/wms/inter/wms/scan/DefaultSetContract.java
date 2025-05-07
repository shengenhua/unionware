package com.unionware.wms.inter.wms.scan;

import com.tencent.mmkv.MMKV;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;
import unionware.base.model.bean.PropertyBean;
import unionware.base.model.req.ClientCustomParametersReq;

import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2024/8/25 18:11
 * @Description : DefaultSetContract
 */

public interface DefaultSetContract {
    public interface View extends IView {
        void getClientDefinesDefaultSet(List<ClientCustomParametersReq.Param> params);
    }
    interface Presenter extends IPresenter<View> {
        void setClientDefinesDefaultSetByNet(List<PropertyBean> editList, String control, Integer orgId, Integer userId, String primaryId);//设置默认值,editList,control为空时是清空并保存
        void setClientDefinesDefaultSetByLocal(MMKV kv,List<PropertyBean> editList, String control, Integer orgId, Integer userId, String primaryId);
        void getClientDefinesDefaultSetByNet(Integer orgId,Integer userId,String primaryId);//获取默认值
        void getClientDefinesDefaultSetByLocal(MMKV kv,Integer orgId,Integer userId,String primaryId,boolean isSave);
    }
}
