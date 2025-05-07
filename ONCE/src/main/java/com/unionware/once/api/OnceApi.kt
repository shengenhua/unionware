package com.unionware.once.api

import com.unionware.once.model.BarcodeResponse
import com.unionware.once.model.SpecialReportReq
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import unionware.base.api.basic.BasicApi
import unionware.base.model.req.ReportReq
import unionware.base.model.resp.ChecketReq
import unionware.base.model.resp.ReportResp
import unionware.base.network.response.BaseResponse

/**
 * 基础（Basic
 * Author: sheng
 * Date:2024/9/6
 */

@JvmSuppressWildcards
interface OnceApi : BasicApi {


    /**
     * 生成工单生产故障记录
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.XMES.OPFaultRecord")
    fun OPFaultRecord(@Body filters: Map<String, Any>?): Observable<BaseResponse<Any>>?


    /**
     * 货位绑定
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=DBAO.MES.StockBindAction")
    fun StockBindAction(@Body filters: Map<String, Any>?): Observable<BaseResponse<Any>>?

    /**
     * 磷钙生产记录单汇报
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=DBAO.MES.ElementNoteReportAction")
    fun ElementNoteReportAction(@Body filters: Map<String, Any>?): Observable<BaseResponse<Any>>?


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
     * 预装箱核对
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.CheckPackRecord")
    fun checkPackRecord(@Body filters: Map<String, String>?): Observable<BaseResponse<ReportResp>>?


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
     * UnionWare.EMES.Checket
     * 生成产品检验单
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.EMES.Checket")
    fun checket(@Body reportReq: ChecketReq?): Observable<BaseResponse<List<ReportResp>>>?

    /**
     * 打印调接口
     *
     * @return 条码 模板
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=ZY.MES.ReportBarCodeExport")
    fun barCodeExport(@Body filters: Map<String?, Any?>?): Observable<BaseResponse<BarcodeResponse>>?

    /**
     * 计件工资查询
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=ZY.MES.EmpPieceSearch")
    fun empPieceSearch(@Body filters: Map<String?, Any?>?): Observable<BaseResponse<BarcodeResponse>>?
}