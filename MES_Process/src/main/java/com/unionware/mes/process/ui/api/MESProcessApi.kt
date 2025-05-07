package com.unionware.mes.process.ui.api

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import unionware.base.api.basic.BasicApi
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.model.resp.ReportResp
import unionware.base.network.response.BaseResponse

/**
 * mes数据采集
 * Author: sheng
 * Date:2025/3/28
 */
interface MESProcessApi : BasicApi {

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
     * UnionWare.XMES.Report
     * 工序汇报
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.XMES.Report")
    fun submitReport(@Body req: ReportReq?): Observable<BaseResponse<List<ReportResp>>>?
}