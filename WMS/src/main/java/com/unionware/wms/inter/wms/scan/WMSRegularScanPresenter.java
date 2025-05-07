package com.unionware.wms.inter.wms.scan;

import com.unionware.wms.URLPath;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.bean.NormalScanConfigBean;

import java.util.HashMap;

import javax.inject.Inject;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

/**
 * @Author : pangming
 * @Time : On 2024/7/9 15:31
 * @Description : WMSRegularScanPresenter
 */

public class WMSRegularScanPresenter extends BasePresenter<WMSRegularScanContract.View> implements WMSRegularScanContract.Presenter {
    PackingApi api;

    @Inject
    public WMSRegularScanPresenter(PackingApi api) {
        this.api = api;
    }

    @Override
    public void requestConfigurationList(String scene, String name, FiltersReq filters) {
        NetHelper.request(api.getScanConfigurationList(scene, name, filters), mView, new ICallback<CommonListDataResp<NormalScanConfigBean>>() {
            @Override
            public void onSuccess(CommonListDataResp<NormalScanConfigBean> data) {
                if (null == data.getData() || data.getData().isEmpty()) {
                    mView.showFailedView("查询不到数据");
                } else {
                    if (data.getData() != null && data.getData().size() > 0) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("primaryId", data.getData().get(0).getJobFlowId() + "");
                        requestConfigurationDetails(scene, URLPath.WMS.MENU_WMS_APP_COMFIGURATION_DETAILS, new FiltersReq(map));
                    }
                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void requestConfigurationDetails(String scene, String name, FiltersReq filters) {
        NetHelper.request(api.getScanConfigurationList(scene, name, filters), mView, new ICallback<CommonListDataResp<NormalScanConfigBean>>() {
            @Override
            public void onSuccess(CommonListDataResp<NormalScanConfigBean> data) {
                if (null == data.getData() || data.getData().isEmpty()) {
                    mView.showFailedView("查询不到数据");
                } else {
                    if (data.getData() != null && data.getData().size() > 0) {
                        mView.setSrcFormId(data.getData().get(0));
                    }
                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }
}
