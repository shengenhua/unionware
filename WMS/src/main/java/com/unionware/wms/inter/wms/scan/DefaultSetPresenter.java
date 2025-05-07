package com.unionware.wms.inter.wms.scan;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.bean.DefaultInfoBean;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.bean.PropertyBean;
import unionware.base.model.req.ClientCustomParametersReq;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @Author : pangming
 * @Time : On 2024/8/26 11:30
 * @Description : DefaultSetPresenter
 * 位置0是默认值字段
 * 位置1是控制字段
 *
 */

public class DefaultSetPresenter extends BasePresenter<DefaultSetContract.View> implements DefaultSetContract.Presenter{
    PackingApi api;

    @Inject
    public DefaultSetPresenter(PackingApi api) {
        this.api = api;
    }
    private ClientCustomParametersReq getClientCustomParametersReq(List<PropertyBean> editList, String control, Integer orgId, Integer userId, String primaryId){
        ClientCustomParametersReq clientCustomParametersReq = new ClientCustomParametersReq();
        clientCustomParametersReq.setOrgId(orgId);
        clientCustomParametersReq.setUserId(userId);
        clientCustomParametersReq.setClient("PDA");
        clientCustomParametersReq.setSection(primaryId);
        List<ClientCustomParametersReq.Param> paramList = new ArrayList<>();

        //保存字段

        ClientCustomParametersReq.Param edit = new ClientCustomParametersReq.Param();
        edit.setKey("editList");
        if(editList == null || editList.size()==0){
            edit.setValue("");
        }else {
            String json = new Gson().toJson(editList);
            edit.setValue(json);
        }
        paramList.add(edit);

        //保存控制参数
        ClientCustomParametersReq.Param param = new ClientCustomParametersReq.Param();
        param.setKey("control");
        if(control == null){
            param.setValue("");
        }else {
            param.setValue(control);
        }

        paramList.add(param);

        clientCustomParametersReq.setParams(paramList);
        return clientCustomParametersReq;
    }
    @Override
    public void setClientDefinesDefaultSetByNet(List<PropertyBean> editList, String control,Integer orgId,Integer userId,String primaryId) {
//        mView.showDialog("默认值保存中...");
//        NetHelper.request(api.setClientDefines(getClientCustomParametersReq(editList,control,orgId,userId,primaryId)), mView, new ICallback<String>() {
//            @Override
//            public void onSuccess(String bean) {
//                mView.showFailedView("保存成功");
//                mView.getClientDefines(req.getParams(),false);
//                mView.dismissDialog();
//            }
//
//            @Override
//            public void onFailure(ApiException e) {
//                mView.dismissDialog();
//                mView.showFailedView(e.getErrorMsg());
//            }
//        });
    }

    @Override
    public void setClientDefinesDefaultSetByLocal(MMKV kv,List<PropertyBean> editList, String control, Integer orgId, Integer userId, String primaryId) {
        String json = new Gson().toJson(getClientCustomParametersReq(editList,control,orgId,userId,primaryId));
        String key = orgId+"_"+userId+"_"+primaryId;
        kv.putString(key,json);
        getClientDefinesDefaultSetByLocal(kv,orgId,userId,primaryId,true);
    }

    @Override
    public void getClientDefinesDefaultSetByNet(Integer orgId,Integer userId,String primaryId) {
        ClientCustomParametersReq clientCustomParametersReq = new ClientCustomParametersReq();
        clientCustomParametersReq.setOrgId(orgId);
        clientCustomParametersReq.setUserId(userId);
        clientCustomParametersReq.setClient("PDA");
        clientCustomParametersReq.setSection(primaryId);
        String[] keys = new String[2];
        keys[0] = "editList";
        keys[1] = "control";
        clientCustomParametersReq.setKeys(keys);
//        mView.showDialog("初始化配置中...");
//        NetHelper.request(api.getClientDefines(req), mView, new ICallback<List<ClientCustomParametersReq.Param>>() {
//            @Override
//            public void onSuccess(List<ClientCustomParametersReq.Param> bean) {
//                mView.getClientDefines(bean,true);
//                mView.dismissDialog();
//            }
//
//            @Override
//            public void onFailure(ApiException e) {
//                mView.dismissDialog();
//                mView.showFailedView(e.getErrorMsg());
//            }
//        });
    }

    @Override
    public void getClientDefinesDefaultSetByLocal(MMKV kv,Integer orgId,Integer userId,String primaryId,boolean isSave) {
        String key = orgId+"_"+userId+"_"+primaryId;
        String json = kv.getString(key,"");
        if(!json.isEmpty()){
            ClientCustomParametersReq clientCustomParametersReq = new Gson().fromJson(json,ClientCustomParametersReq.class);
            List<ClientCustomParametersReq.Param> paramList = clientCustomParametersReq.getParams();
            if(isSave){
                mView.getClientDefinesDefaultSet(paramList);
            }else {
//                String editJson = paramList.get(0).getValue();
//                if (!editJson.isEmpty()) {
//                    List<PropertyBean> editList = new Gson().fromJson(editJson, new TypeToken<List<PropertyBean>>() {
//                    }.getType());
//                }
                String controlJson = paramList.get(1).getValue();
                boolean isDefault = false;
                if (!controlJson.isEmpty()) {
                    DefaultInfoBean defaultInfoBean = new Gson().fromJson(controlJson, DefaultInfoBean.class);
                    isDefault = defaultInfoBean.isDefault();
                }
                if (!isDefault) {
                    paramList.get(0).setValue(null);
                }
                mView.getClientDefinesDefaultSet(paramList);
            }
        }
    }
}
