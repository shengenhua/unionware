package com.unionware.wms.inter.wms.scan;

import com.unionware.wms.api.PackingApi;
import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.bean.BillBean;
import unionware.base.model.bean.CommonListBean;
import unionware.base.model.bean.ViewBean;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @Author : pangming
 * @Time : On 2024/6/26 11:10
 * @Description : MMSRegularScanCommonListPresenter
 */

public class WMSRegularScanCommonListPresenter extends BasePresenter<WMSRegularScanCommonListContract.View> implements WMSRegularScanCommonListContract.Presenter {
    PackingApi api;

    @Inject
    public WMSRegularScanCommonListPresenter(PackingApi api) {
        this.api = api;
    }


    @Override
    public void requestList(String scene, String name, FiltersReq filters) {
        NetHelper.request(api.getCommonList(scene, name, filters), mView, new ICallback<CommonListDataResp<Map<String, Object>>>() {
            @Override
            public void onSuccess(CommonListDataResp<Map<String, Object>> data) {
                if (filters.getPageIndex() == 1 && (null == data.getData() || data.getData().isEmpty())) {
                    mView.showEmptyView();
                } else {
                    mView.showList(convertViewToList(data.getView(), data.getData()));
                }
            }

            @Override
            public void onFailure(ApiException e) {
                if(mView!=null) {
                    mView.showFailedView(e.getErrorMsg());
                }
            }
        });
    }

    @Override
    public void taskCancel(Map map,int position) {
        NetHelper.request(api.taskCancel(map), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String data) {
                mView.upDataByTaskCancel(position);
                mView.showFailedView("任务删除成功");
            }

            @Override
            public void onFailure(ApiException e) {
                if(mView!=null) {
                    mView.showFailedView(e.getErrorMsg());
                }
            }
        });
    }

    /**
     *  根据 ConvertUtils类的 convertViewToList公共方法，特殊修改
     * @param views
     * @param data
     * @return
     */
    public  List<BillBean> convertViewToList(List<ViewBean> views, List<Map<String, Object>> data) {
        // options 获取需要显示的View
        Map<String, String> options = views.stream()
                .filter(ViewBean::isVisible)
                .collect(Collectors.toMap(ViewBean::getKey, ViewBean::getName));
        List<BillBean> billBeans = new ArrayList<>();
        for (Map<String, Object> map : data) {
            List<CommonListBean> list = new ArrayList<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (options.containsKey(key)) {
                    String val = "";
                    if (entry.getValue() != null) {
                        Object value = entry.getValue();
                        if (value == null) {
                            val = "";
                        } else if (value instanceof Number) {
                            val = new BigDecimal(String.valueOf(value)).stripTrailingZeros().toPlainString();
                        } else {
                            val = value.toString();
                        }
                    }
                    list.add(new CommonListBean(options.get(key), val));
                }
            }
            BillBean bean = null;
            if (map.containsKey("code")) {
                bean = new BillBean(new BigDecimal(objToString(map.get("id")))
                        .stripTrailingZeros().toPlainString(),
                        objToString(map.get("code")), list);
                if (options.containsKey("code")) {
                    bean.setCodeName(objToString(options.get("code")));
                }
                billBeans.add(bean);
            } else if (map.containsKey("id")) {
                bean = new BillBean(new BigDecimal(objToString(map.get("id"))).stripTrailingZeros().toPlainString(), list);
                if (options.containsKey("id")) {
                    bean.setCodeName(objToString(options.get("id")));
                } else {
                    bean.setCodeName("编号");
                }
                billBeans.add(bean);
            } else {
                bean = new BillBean(list);
                billBeans.add(bean);
            }
            if (map.containsKey("barCode")) {
                bean.setBarcode(objToString(map.get("barCode")));
            }
            if (map.containsKey("materialId")) {
                bean.setMaterialId(new BigDecimal(objToString(map.get("materialId"))).stripTrailingZeros().toPlainString());
            }
            if (map.containsKey("planId")) {
                bean.setPlanId(new BigDecimal(objToString(map.get("planId"))).stripTrailingZeros().toPlainString());
            }
            if (map.containsKey("FPlanId")) {
                bean.setPlanId(objToString(map.get("FPlanId")));
            }

            if (map.containsKey("planNo")) {
                bean.setPlanNo(objToString(map.get("planNo")));
            }
            if (map.containsKey("FPlanNo")) {
                bean.setPlanNo(objToString(map.get("FPlanNo")));
            }

            if (map.containsKey("taskStatus")) {
                bean.setTaskStatus(objToString(map.get("taskStatus")));
            }
            if (map.containsKey("jobId")) {
                bean.setJobId(objToString(map.get("jobId")));
            }
            if (map.containsKey("formId")) {
                bean.setFormId(objToString(map.get("formId")));
            }
            if (map.containsKey("LinkId")) {
                bean.setLinkId(objToString(map.get("LinkId")));
            }
            if (map.containsKey("FBoxCode")) {
                bean.setFBoxCode(objToString(map.get("FBoxCode")));
            }
            if (map.containsKey("creatorId")) {
                bean.setCreatorId(new BigDecimal(map.get("creatorId") == null ? "0" : map.get("creatorId").toString()).stripTrailingZeros().longValue());
            }
            bean.setDataMap(map);
        }
        return billBeans;
    }
    public  String objToString(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }
}
