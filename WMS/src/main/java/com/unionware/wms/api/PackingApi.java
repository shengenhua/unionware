package com.unionware.wms.api;

import com.unionware.base.lib_common.model.resp.ListDataViewResp;
import com.unionware.wms.model.bean.BarcodeDetailsBean;
import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;
import com.unionware.wms.model.bean.NormalScanConfigBean;
import com.unionware.wms.model.bean.ProgressInfoBean;
import com.unionware.wms.model.bean.RePackBean;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.AnalysisReq;
import com.unionware.wms.model.req.DeleteReq;
import com.unionware.wms.model.req.PackingsReq;
import com.unionware.wms.model.req.PageIdReq;
import com.unionware.wms.model.req.RePackReq;
import com.unionware.wms.model.resp.BasePropertyResp;
import com.unionware.wms.model.req.IdReq;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import unionware.base.model.bean.BaseInfoBean;
import unionware.base.model.bean.BinCodeInfoBean;
import unionware.base.model.bean.PrintTemplateBean;
import unionware.base.model.bean.PropertyBean;
import unionware.base.model.bean.SerialNumberInfoBean;
import unionware.base.model.bean.SimpleViewAndModelBean;
import unionware.base.model.bean.TaskIdBean;
import unionware.base.model.req.BarcodePrintExportReq;
import unionware.base.model.req.ClientCustomParametersReq;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.PrintExportReq;
import unionware.base.model.req.PrintTemplateReq;
import unionware.base.model.req.TaskIdReq;
import unionware.base.model.req.TaskReq;
import unionware.base.model.req.ViewReq;
import unionware.base.model.resp.AnalysisInfoResp;
import unionware.base.model.resp.AnalysisResp;
import unionware.base.model.resp.CommonDataResp;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.model.resp.MenuResp;
import unionware.base.network.response.BaseResponse;

/**
 * @GET 表明这是get请求
 * @POST 表明这是post请求
 * @PUT 表明这是put请求
 * @DELETE 表明这是delete请求
 * @PATCH 表明这是一个patch请求，该请求是对put请求的补充，用于更新局部资源
 * @HEAD 表明这是一个head请求
 * @OPTIONS 表明这是一个option请求
 * @HTTP 通用注解, 可以替换以上所有的注解，其拥有三个属性：method，path，hasBody
 * @Headers 用于添加固定请求头，可以同时添加多个。通过该注解添加的请求头不会相互覆盖，而是共同存在
 * @Header 作为方法的参数传入，用于添加不固定值的Header，该注解会更新已有的请求头
 * @Body 多用于post请求发送非表单数据, 比如想要以post方式传递json格式数据
 * @Filed 多用于post请求中表单字段, Filed和FieldMap需要FormUrlEncoded结合使用
 * @FiledMap 和@Filed作用一致，用于不确定表单参数
 * @Part 用于表单字段, Part和PartMap与Multipart注解结合使用, 适合文件上传的情况
 * @PartMap 用于表单字段, 默认接受的类型是Map<String,REquestBody>，可用于实现多文件上传
 * <p>
 * Part标志上文的内容可以是富媒体形势，比如上传一张图片，上传一段音乐，即它多用于字节流传输。
 * 而Filed则相对简单些，通常是字符串键值对。
 * </p>
 * Part标志上文的内容可以是富媒体形势，比如上传一张图片，上传一段音乐，即它多用于字节流传输。
 * 而Filed则相对简单些，通常是字符串键值对。
 * @Path 用于url中的占位符,{占位符}和PATH只用在URL的path部分，url中的参数使用Query和QueryMap代替，保证接口定义的简洁
 * @Query 用于Get中指定参数
 * @QueryMap 和Query使用类似
 * @Url 指定请求路径
 * ==============================================================================================================================
 * 命名规则：
 * get_ 表示获取查询数据  如：获取验证码接口 getCode()
 * create_表示创建 如：创建直播接口createLive();
 * delete_ 删除 如：删除订单接口  deleteOrder()
 * change_ 更改 如：更改用户绑定接口 changeUserBind()
 * submit_ 提交 如：提交主播审核信息接口 submitAnchorVerifyInfo()
 * _details 详情 如 获取用户详情接口 getUserDetails()
 * _info 信息 如：获取机器人用户信息接口 getRobotInfo()
 * _list 列表 如：获取主播商品列表接口 getAnchorGoodsList();
 * _state 状态 如：查询审核状态接口 getVerifyState()
 * =====================================================================================================================================
 * 特别注意：不允许有数字拼音、命名
 */
public interface PackingApi {


    /**
     * 获取配置列表（带参数）
     *
     * @param name @see 获取配置列表一级菜单返回的code
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    Observable<BaseResponse<MenuResp>> getScanConfigList(@Query("name") String name, @Query("scene") String scene, @Body FiltersReq filters);


    /**
     * 获取配置列表（带参数-场景码）
     *
     * @param name @see 获取配置列表一级菜单返回的code
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    Observable<BaseResponse<MenuResp>> getScanConfigList(@Query("scene") String scene, @Query("name") String name);


    /**
     * 获取通用列表
     *
     * @param scene   场景吗
     * @param name
     * @param filters
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    Observable<BaseResponse<CommonListDataResp<Map<String, Object>>>> getCommonList(@Query("scene") String scene, @Query("name") String name, @Body FiltersReq filters);

    /**
     * 获取装箱列表（进行中）
     *
     * @param scene
     * @param name
     * @param filters
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    Observable<BaseResponse<CommonListDataResp<ProgressInfoBean>>> getInProgressList(@Query("scene") String scene, @Query("name") String name, @Body FiltersReq filters);


    /**
     * 获取基础资料
     *
     * @param scene
     * @param name
     * @param filters
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    Observable<BaseResponse<CommonListDataResp<BaseInfoBean>>> getBaseInfoList(@Query("scene") String scene, @Query("name") String name, @Body FiltersReq filters);

    /**
     * 仓库扫描回车专用：库位条码
     * @param scene
     * @param filters
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    Observable<BaseResponse<CommonListDataResp<BinCodeInfoBean>>> getBinCodeInfoList(@Query("scene") String scene, @Query("name") String name, @Body FiltersReq filters);

    /**
     * 获取配置详情信息
     *
     * @param scene 场景吗 （Packing）装箱
     * @param name  63FDBB04545196 固定值
     * @return
     */

    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    Observable<BaseResponse<CommonListDataResp<ScanConfigBean>>> getScanConfigDetails(@Query("scene") String scene, @Query("name") String name, @Body FiltersReq filters);


    /**
     * 获取扫描记录详情信息
     *
     * @param scene
     * @param name    63FD80D1477ED0 明细（固定） 63FD7EEB477EC2 包装（固定）
     * @param filters
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    Observable<BaseResponse<CommonListDataResp<BarcodeDetailsBean>>> getRecordDetalisInfo(@Query("scene") String scene, @Query("name") String name, @Body FiltersReq filters);


    /**
     * 创建临时装箱单
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.CreateTempPackingList")
    Observable<BaseResponse<BasePropertyResp>> createTempPackingList(@Body IdReq id);

    /**
     * 删除临时装箱单
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.DeleteTemp")
    Observable<BaseResponse<Object>> deleteTempPackingList(@Body IdReq id);


    /**
     * 装箱删除条码
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.DeleteCodeTemp")
    Observable<BaseResponse<Object>> deletePackingByBarcode(@Body Map<String, Object> map);

    /**
     * 装箱删除条码
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.DeleteCodeTemp")
    Observable<BaseResponse<Object>> deletePackingByBarcode(@Body DeleteReq req);


    /**
     * 条码解析（临时装箱）
     *
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.TScanCode")
    Observable<BaseResponse<List<BarcodeDetailsInfoBean>>> analysisPackBarcode(@Body AnalysisReq req);


    /**
     * 更新条码信息
     *
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.Update")
    Observable<BaseResponse<Object>> updateBarcodeInfo(@Body AnalysisReq req);


    /**
     * 确认条码（箱码、提交）
     *
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.ComfirmCode")
    Observable<BaseResponse<Object>> comfirmBarcodeInfo(@Body AnalysisReq req);


    /**
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.Submit")
    Observable<BaseResponse<Object>> submitBarcodeInfo(@Body Map<String, String> map);

    /**
     * 装拆箱扫描条码
     *
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.ScanCode")
    Observable<BaseResponse<List<BarcodeDetailsInfoBean>>> analysisPackBarcodeByBPUnpacking(@Body AnalysisReq req);

    /**
     * 拆箱
     *
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.UnPacking")
    Observable<BaseResponse<String>> unPacking(@Body PackingsReq req);

    /**
     * 转入转出箱
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.ScanCode")
    Observable<BaseResponse<List<BarcodeDetailsInfoBean>>> analysisPackBarcodeByTransfer(@Body AnalysisReq req);

    /**
     * 整箱转
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Packing.RePacking")
    Observable<BaseResponse<RePackBean>> rePacking(@Body RePackReq req);

    /**
     * 获取套打模板
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.GetPrintTemplate")
    Observable<BaseResponse<List<PrintTemplateBean>>> getPrintTemplate(@Body PrintTemplateReq printTemplateReq);

    /**
     * 套打导出
     *
     * @param printExportReq
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.PrintExport")
    Observable<BaseResponse<String>> getPrintExport(@Query("scene") String scene,@Body PrintExportReq printExportReq);


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
     * 更新虚拟视图
     *  要根据具体的场景，会有交互信息确认的情况，所以存在2种数据类型，要使用Object,
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.UpdateValue")
    Observable<BaseResponse<Object>> updateView(@Body ViewReq req);

    /**
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<Object>> commandView(@Body ViewReq req);

    /**
     * 关闭虚拟视图
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.CloseView")
    Observable<BaseResponse<String>> closeView(@Body PageIdReq req);

    /**
     * 获取虚拟视图
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.GetSimpleModel")
    Observable<BaseResponse<AnalysisResp>> getViewData(@Body ViewReq req);

    /**
     *  获取源单序列号
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<SerialNumberInfoBean>> getSerialNumber(@Body ViewReq req);

    /**
     * 获取虚拟视图 (提交条码)
     *
     * @param
     * @return 正常返回和90000-99999返回是2种结构
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<Object>> confirmViewData(@Body ViewReq req);

    /**
     *  无条码制单下，点击分录调用，效果类似扫条码返回可编辑项和字段值
     *  {
     * 	"pageId" : "908ae4d6-d87c-47b1-a171-63bf73252df6",
     * 	"command" : "INVOKE_CLIENTROWDOUBLECLICK",
     * 	"params" : {
     * 		"ByRow":"653241"
     *        }
     * }
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<Object>> clientRowDoubleClick(@Body ViewReq req);

    /**
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<Object>> commandViewDataToView(@Body ViewReq req);

    /**
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Stock.CreateScanTask")
    Observable<BaseResponse<TaskIdBean>> createScanTask(@Body TaskReq req);

    /**
     * 提交
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Stock.SubmitScanTask")
    Observable<BaseResponse<String>> submitTask(@Body TaskIdReq req);


    /**
     * 获取汇总列表
     *
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<CommonDataResp>> getSummaryList(@Body ViewReq req);


    /**
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<CommonDataResp>> deleteBarcodeDetails(@Body ViewReq req);


    /**
     * 关闭容器
     *
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<AnalysisInfoResp>> closeContainer(@Body ViewReq req);

    /**
     * 查询未完成的条码装箱作业（拿到配置id）
     *
     * @param scene
     * @param name
     * @param filters
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    Observable<BaseResponse<CommonListDataResp<ScanConfigBean>>> getBoxStateId(@Query("scene") String scene, @Query("name") String name, @Body FiltersReq filters);


    /**
     * 获取装拆箱扫描配置
     *
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.GetSimpleView")
    Observable<BaseResponse<List<PropertyBean>>> getScanConfigData(@Body PageIdReq req);

    /**
     * 获取装拆箱扫描配置
     *
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.GetSimpleModel")
    Observable<BaseResponse<AnalysisResp>> getGetSimpleModel(@Body PageIdReq req);

    /**
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<AnalysisInfoResp>> commandViewData(@Body ViewReq req);

    /**
     * 拆分数量
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<AnalysisInfoResp>> splitQuantity(@Body ViewReq req);

    /**
     *批量设置客户端自定义参数
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.SetClientDefines")
    Observable<BaseResponse<String>> setClientDefines(@Body ClientCustomParametersReq req);

    /**
     * 批量获取客户端自定义参数
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.GetClientDefines")
    Observable<BaseResponse<List<ClientCustomParametersReq.Param>>> getClientDefines(@Body ClientCustomParametersReq req);
    /** 普通扫描区域**/
    /**
     * 1.普通扫描应用配置列表
     * 2.查询作业流程配置详情
     * @param scene
     * @param name
     * @param filters
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    Observable<BaseResponse<CommonListDataResp<NormalScanConfigBean>>> getScanConfigurationList(@Query("scene") String scene, @Query("name") String name, @Body FiltersReq filters);

    /**
     * 创建扫描任务
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.FlowTask.Create")
    Observable<BaseResponse<TaskIdBean>> createNormalScanTask(@Body TaskReq req);

    /**
     * 创建虚拟视图(返回view page data)
     *
     * @param
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.CreateView")
    Observable<BaseResponse<SimpleViewAndModelBean>> createViewGetMore(@Body ViewReq req);

    /**
     *  提交作业
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Simulate.Command")
    Observable<BaseResponse<Object>> flowTaskSubmit(@Body TaskIdReq req);

    /**
     *
     * @param server server=remote 强制使用远端打印服务  server=local 强制使用本地打印服务，接口返回打印数据
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.BarCodePrintExport")
    Observable<BaseResponse<String>> barCodePrintExport(@Query("scene") String scene,@Query("server") String server, @Body BarcodePrintExportReq req);

    /**
     *  条码套打导出
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.BarCodePrintExport")
    Observable<BaseResponse<String>> barcodePrintExportReq(@Query("scene") String scene,@Body BarcodePrintExportReq req);

    /**
     * <p>
     * data = !PrinterRemoteService ，则表示该打印任务已转移至远端打印服务去打印,本地无需打印
     * 强制使用远端打印服务，接口返回 !PrinterRemoteService
     * action=UnionWare.Basic.PrintExport&server=remote
     * 强制使用本地打印服务，接口返回打印数据
     * action=UnionWare.Basic.PrintExport&server=local
     * </p>
     *  拆装箱打印
     *
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.PrintExport")
    Observable<BaseResponse<String>> boxPrintExportReq(@Query("scene") String scene,@Body BarcodePrintExportReq req);

    /**
     * 单据打印
     * @param scene
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.PrintExport")
    Observable<BaseResponse<String>> printExportReq(@Query("scene") String scene,@Body BarcodePrintExportReq req);
    /**
     * 条码补打 条码查询
     * @param scene   场景吗
     * @param name
     * @param filters
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    Observable<BaseResponse<CommonListDataResp<Map<String, Object>>>> getScanBarcode(@Query("scene") String scene, @Query("name") String name, @Body FiltersReq filters);

    /**
     *  条码补打
     */
    /**
     *  进行中任务删除
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.FlowTask.Cancel")
    Observable<BaseResponse<String>> taskCancel(@Body Map map);
}
