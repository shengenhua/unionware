package unionware.base.api.basic

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.bean.PrintTemplateBean
import unionware.base.model.bean.UploadReq
import unionware.base.model.req.BarcodePrintExportReq
import unionware.base.model.req.ClientCustomParametersReq
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.PrintExportReq
import unionware.base.model.req.PrintTemplateReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.model.resp.FileResp
import unionware.base.network.response.BaseResponse

/**
 * 基础（Basic
 * Author: sheng
 * Date:2024/9/6
 */
@JvmSuppressWildcards
interface BasicApi {

    /**
     * @param req
     * @return
     */
    @POST
    fun anyApi(
        @Url url: String,
        @Query("action") action: String?,
        @Query("scene") scene: String?,
        @Query("name") name: String?,
        @Body req: Any?,
    ): Observable<Any>?

    /**
     * 获取客户端自定义参数
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.GetClientDefine")
    fun getClientDefine(@Body req: ClientCustomParametersReq?): Observable<BaseResponse<List<ClientCustomParametersReq.Param>>>?

    /**
     * 批量获取客户端自定义参数
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.GetClientDefines")
    fun getClientDefines(@Body req: ClientCustomParametersReq?): Observable<BaseResponse<List<ClientCustomParametersReq.Param>>>?

    /**
     * 设置客户端自定义参数
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.SetClientDefine")
    fun setClientDefine(@Body req: ClientCustomParametersReq?): Observable<BaseResponse<String>>?

    /**
     * 批量设置客户端自定义参数
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.SetClientDefines")
    fun setClientDefines(@Body req: ClientCustomParametersReq?): Observable<BaseResponse<String>>?

    /**
     * 获取基础资料
     *
     * @param scene 场景码
     * @param name 接口
     * @param filters 参数
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun getBaseInfoList(
        @Query("scene") scene: String?,
        @Query("name") name: String?,
        @Body filters: FiltersReq?,
    ): Observable<BaseResponse<CommonListDataResp<BaseInfoBean>>>?

    /**
     * 获取基础资料
     *
     * @param scene 场景码
     * @param name 接口
     * @param filters 参数
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun query(
        @Query("scene") scene: String?,
        @Query("name") name: String?,
        @Body filters: MutableMap<String, Any>?,
    ): Observable<Any>?

    /**
     * 获取基础资料
     *
     * @param scene 场景码
     * @param name 接口
     * @param filters 参数
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun queryToAny(
        @Query("scene") scene: String?,
        @Query("name") name: String?,
        @Body filters: FiltersReq?,
    ): Observable<BaseResponse<Any>>?


    /**
     * 通用查询操作
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun query(
        @Query("scene") scene: String?,
        @Query("name") name: String?,
        @Body filters: FiltersReq?,
    ): Observable<BaseResponse<CommonListDataResp<Map<String, Any>>?>>?

    /**
     * 通用查询操作
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun queryToMMap(
        @Query("scene") scene: String?,
        @Query("name") name: String?,
        @Body filters: FiltersReq?,
    ): Observable<BaseResponse<CommonListDataResp<MutableMap<String, Any>>?>>?


    /**
     * 通用查询操作
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun queryToMapString(
        @Query("scene") scene: String?,
        @Query("name") name: String?,
        @Body filters: FiltersReq?,
    ): Observable<BaseResponse<CommonListDataResp<Map<String, String>>>>?

    /**
     * 通用查询操作
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun queryToMMapStr(
        @Query("scene") scene: String?,
        @Query("name") name: String?,
        @Body filters: FiltersReq?,
    ): Observable<BaseResponse<CommonListDataResp<MutableMap<String, String>>?>>?

    /**
     * 通用查询操作
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun <T> queryToT(
        @Query("scene") scene: String?,
        @Query("name") name: String?,
        @Body filters: FiltersReq?,
    ): Observable<BaseResponse<T>>?

    /**
     * 获取配置列表（带UI 新版）
     *
     * @return
     */
//    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.GetUiDefine")
//    fun getMenuList(): Observable<BaseResponse<MenuCustomResp?>?>?

    /**
     * 套打导出
     *
     * @param printExportReq
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.PrintExport")
    fun getPrintExport(@Body printExportReq: PrintExportReq?): Observable<BaseResponse<String>>?

    /**
     * 套打导出
     *
     * @param printExportReq
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.PrintExport")
    fun getPrintExport(
        @Query("scene") scene: String?,
        @Body printExportReq: BarcodePrintExportReq?,
    ): Observable<BaseResponse<String>>?

    /**
     * 条码套打导出
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.BarCodePrintExport")
    fun barcodePrintExportReq(@Body req: BarcodePrintExportReq?): Observable<BaseResponse<String>>?

    /**
     * 条码套打导出
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.BarCodePrintExport")
    fun barcodePrintExportReq(
        @Query("scene") scene: String?,
        @Body req: BarcodePrintExportReq?,
    ): Observable<BaseResponse<String>>?

    /**
     * 获取套打模板
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.GetPrintTemplate")
    fun getPrintTemplate(@Body printTemplateReq: PrintTemplateReq?): Observable<BaseResponse<List<PrintTemplateBean>>>?

    /**
     * 获取套打模板
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.GetPrintTemplate")
    fun getPrintTemplate(@Body printTemplateReq: Map<String, String>?): Observable<BaseResponse<List<MutableMap<String, String>>>>?

    /**
     * 上传图片
     *
     * @param req
     * @return
     */
    @POST("Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.UploadFile.common.kdsvc")
    fun uploadFile(@Body req: UploadReq?): Observable<BaseResponse<FileResp>>?


    /**
     * 设备接入
     *
     * @return
     */
    @POST("UNW.MOM.WebApi.ServicesStub.OpenService.Invoke.common.kdsvc?action=\$Connected")
    fun deviceConnect(@Body map: MutableMap<String, String>): Observable<BaseResponse<Map<String, String>>>

    /**
     * 设备心跳
     *
     * @return
     */
    @POST("UNW.MOM.WebApi.ServicesStub.OpenService.Invoke.common.kdsvc?action=\$Heartbeat")
    fun deviceHeartbeat(): Observable<BaseResponse<Any>>

//    /**
//     * UnionWare.XMES.Report
//     * 工序汇报
//     */
//    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.XMES.Report")
//    fun submitReport(@Body req: ReportReq?): Observable<BaseResponse<List<ReportResp>>>?
//
//    /**
//     * UnionWare.EMES.Check
//     * 气体标定检测提交接口
//     */
//    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.Check")
//    fun barcodeCheck(@Body checketReq: ChecketReq?): Observable<BaseResponse<List<ReportResp>>>?
//
//    /**
//     * UnionWare.EMES.RatifyChecked
//     * 特批校验（验证特批人及特批密码）
//     */
//    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.RatifyChecked")
//    fun ratifyChecked(@Body filters: Map<String, String>?): Observable<BaseResponse<String>>?
}