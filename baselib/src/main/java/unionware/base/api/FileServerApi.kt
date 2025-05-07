package unionware.base.api

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import unionware.base.model.bean.UploadReq
import unionware.base.model.req.BarcodePrintExportReq
import unionware.base.model.req.FileReq
import unionware.base.model.resp.FileResp
import unionware.base.network.response.BaseResponse

/**
 * 文件服务
 * Author: sheng
 * Date:2024/9/6
 */
interface FileServerApi {
    /**
     * 从文件服务器获取文件内容（BASE64）
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.FileServer.GetFileData")
    fun getFile(@Body req: FileReq?): BaseResponse<FileResp?>?

    /**
     *
     * data = PrinterRemoteService ，则表示该打印任务已转移至远端打印服务去打印,本地无需打印
     * 强制使用远端打印服务，接口返回 <PrinterRemoteService>
     * action=UnionWare.Basic.PrintExport&server=remote
     * 强制使用本地打印服务，接口返回打印数据
     * action=UnionWare.Basic.PrintExport&server=local
     *
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.PrintExport")
    fun printExport(@Body req: BarcodePrintExportReq?): Observable<BaseResponse<String>>?

    /**
     *
     * data = PrinterRemoteService ，则表示该打印任务已转移至远端打印服务去打印,本地无需打印
     * 强制使用远端打印服务，接口返回 <PrinterRemoteService>
     * action=UnionWare.Basic.PrintExport&server=remote
     * 强制使用本地打印服务，接口返回打印数据
     * action=UnionWare.Basic.PrintExport&server=local
     *
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.PrintExport")
    fun printExport(
        @Query("server") server: String,
        @Body req: BarcodePrintExportReq?,
    ): Observable<BaseResponse<String>>?

    /**
     *
     * data = PrinterRemoteService ，则表示该打印任务已转移至远端打印服务去打印,本地无需打印
     * 强制使用远端打印服务，接口返回 <PrinterRemoteService>
     * action=UnionWare.Basic.PrintExport&server=remote
     * 强制使用本地打印服务，接口返回打印数据
     * action=UnionWare.Basic.PrintExport&server=local
     *
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.BarCodePrintExport")
    fun barCodePrintExport(@Body req: BarcodePrintExportReq?): Observable<BaseResponse<String>>?

    /**
     *
     * data = PrinterRemoteService ，则表示该打印任务已转移至远端打印服务去打印,本地无需打印
     * 强制使用远端打印服务，接口返回 <PrinterRemoteService>
     * action=UnionWare.Basic.PrintExport&server=remote
     * 强制使用本地打印服务，接口返回打印数据
     * action=UnionWare.Basic.PrintExport&server=local
     *
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.BarCodePrintExport")
    fun barCodePrintExport(
        @Query("server") server: String,
        @Body req: BarcodePrintExportReq?,
    ): Observable<BaseResponse<String>>?

    /**
     * 上传图片
     *
     * @param req
     * @return
     */
    @POST("Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.UploadFile.common.kdsvc")
    fun uploadFile(@Body req: UploadReq?): Observable<BaseResponse<FileResp>>?


    /**
     * 获取套打模板
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.GetPrintTemplate")
    fun getPrintTemplate(@Body printTemplateReq: Map<String, String>?): Observable<BaseResponse<List<MutableMap<String, String>>?>>?
}