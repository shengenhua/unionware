package com.unionware.emes.api

import com.unionware.emes.bean.SpecialReportReq
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import unionware.base.api.basic.BasicApi
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq
import unionware.base.model.resp.ChecketReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.model.resp.ReportResp
import unionware.base.network.response.BaseResponse

/**
 * 电子制造
 * Author: sheng
 * Date:2025/3/28
 */
interface EMESApi : BasicApi {

    /**
     * 获取采集项目
     *
     * @param scene 场景码
     * @param name 接口
     * @param filters 参数
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun queryCollect(
        @Query("scene") scene: String?,
        @Query("name") name: String?,
        @Body filters: FiltersReq?,
    ): Observable<BaseResponse<CommonListDataResp<Map<String, Any>>>>?

    /**
     * 扫描条码
     *
     * @param scene 场景码
     * @param name 接口
     * @param filters 参数
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun scanBarcodeAny(
        @Query("scene") scene: String?,
        @Query("name") name: String?,
        @Body filters: FiltersReq?,
    ): Observable<BaseResponse<Any>>?


    /**
     * UnionWare.EMES.Check
     * 气体标定检测提交接口
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.Check")
    fun barcodeCheck(@Body checketReq: ChecketReq?): Observable<BaseResponse<List<ReportResp>>>?


    /**
     * UnionWare.EMES.RatifyChecked
     * 特批校验（验证特批人及特批密码）
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.RatifyChecked")
    fun ratifyChecked(@Body filters: Map<String, String>?): Observable<BaseResponse<String>>?

    /**
     * UnionWare.XMES.Report
     * 工序汇报
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.XMES.Report")
    fun submitReport(@Body req: ReportReq?): Observable<BaseResponse<List<ReportResp>>>?

    /**
     * UnionWare.EMES.Repaired
     * 生成不良品处理单
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.Repaired")
    fun repaired(@Body reportReq: SpecialReportReq?): Observable<BaseResponse<Any>>?


    /**
     * UnionWare.EMES.FrameTransfer
     * 生成老化架转移记
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.FrameTransfer")
    fun frameTransfer(@Body reportReq: SpecialReportReq?): Observable<BaseResponse<ReportResp>>?


    /**
     * 不良检修判定
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.RepairedJudge")
    fun repairedJudge(@Body filters: Map<String, Any>?): Observable<BaseResponse<Any>>?


    /**
     * UnionWare.EMES.TimeUpdated
     * 时长上报（浇封固化）
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.TimeUpdated")
    fun timeUpdated(@Body reportReq: ReportReq?): Observable<BaseResponse<String>>?


    /**
     * 预装箱核对
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.CheckPackRecord")
    fun checkPackRecord(@Body filters: Map<String, String>?): Observable<BaseResponse<ReportResp>>?


    /**
     * 更新排产单工序进度
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.ReportProgress")
    fun reportProgress(@Body filters: Map<String, Any>?): Observable<BaseResponse<Any>>?


    /**
     * 工单产品转移单
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.OrderTransfer")
    fun orderTransfer(@Body checketReq: ChecketReq?): Observable<BaseResponse<List<ReportResp>>>?


    /**
     * 更新排产单工序进度
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.UpdateJobSchedule")
    fun updateJobSchedule(@Body filters: Map<String, Any>?): Observable<BaseResponse<Any>>?
}