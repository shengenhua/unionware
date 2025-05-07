package unionware.base.api;



import com.unionware.base.lib_common.model.resp.ListDataViewResp;
import unionware.base.model.req.ViewReq;
import unionware.base.model.bean.PropertyBean;
import unionware.base.model.resp.AnalysisInfoResp;
import unionware.base.model.resp.AnalysisResp;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import unionware.base.network.response.BaseResponse;

/**
 * 虚拟视图
 */
public interface SimulateApi {

    /**
     * 创建虚拟视图
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.CreateView")
    Observable<BaseResponse<String>> createView(@Body ViewReq req);

    /**
     * 创建虚拟视图
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.CreateView")
    Observable<BaseResponse<ListDataViewResp<AnalysisResp, PropertyBean>>> createViewGetData(@Body ViewReq req);


    /**
     * 获取虚拟视图 view
     *
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.GetSimpleView")
    Observable<BaseResponse<List<PropertyBean>>> getSimpleView(@Body Map<String, String> req);

    /**
     * 获取虚拟视图 数据
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.GetSimpleModel")
    Observable<BaseResponse<AnalysisResp>> getSimpleModel(@Body ViewReq req);

    /**
     * 获取虚拟视图 确认操作
     *
     * @param
     * @return 正常返回和90000-99999返回是2种结构
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<Object>> commandViewData(@Body ViewReq req);

    /**
     * 更新虚拟视图
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.UpdateValue")
    Observable<BaseResponse<Object>> updateView(@Body ViewReq req);

    /**
     * 关闭虚拟视图
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.CloseView")
    Observable<BaseResponse<String>> closeView(@Body Map<String, String> req);
}
