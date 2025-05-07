package unionware.base.api

import unionware.base.model.req.TaskIdReq
import unionware.base.model.req.TaskReq
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import unionware.base.model.bean.TaskIdBean
import unionware.base.network.response.BaseResponse

/**
 * 流程作业
 * Author: sheng
 * Date:2024/9/6
 */
interface FlowTaskApi {

    /**
     * 创建作业流程
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.FlowTask.Create")
    fun createFlowTask(@Body req: TaskReq?): Observable<BaseResponse<TaskIdBean?>?>?

    /**
     * 提交作业
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.FlowTask.Submit")
    fun submitFlowTask(@Body req: TaskIdReq?): Observable<unionware.base.network.response.BaseResponse<Any?>?>?


    /**
     * 提交作业
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.FlowTask.Cancel")
    fun cancelFlowTask(@Body req: TaskIdReq?): Observable<unionware.base.network.response.BaseResponse<Any?>?>?
}