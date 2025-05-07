package unionware.base.api

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url
import unionware.base.app.model.AppLastest
import unionware.base.model.req.AuthReq
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.LoginReq
import unionware.base.model.req.MenuCustomResp
import unionware.base.model.req.OrgReq
import unionware.base.model.resp.DBCenterResp
import unionware.base.model.resp.MenuResp
import unionware.base.model.resp.UserInfoResp
import unionware.base.network.response.BaseResponse

/**
 * Author: sheng
 * Date:2024/12/30
 */
interface UserApi {


    /**
     * 获取个人信息
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Authorize.GetUserInformation")
    fun getUserInfo(): Observable<BaseResponse<UserInfoResp>>?


    /*获取数据中心列表(金蝶标准)*/
    @POST("Kingdee.BOS.WebApi.ServicesStub.MCService.GetDataCenterList.common.kdsvc")
    fun getKingDeeDataCenterList(): Observable<BaseResponse<List<DBCenterResp>>>?


    /**
     * 获取组织列表
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Authorize.GetUserOrganizations")
    fun getUserOrgList(): Observable<BaseResponse<List<DBCenterResp>>>?

    /**
     * 设置组织
     *
     * @param req
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Authorize.SetUserOrganization")
    fun setUserOrgInfo(@Body req: OrgReq): Observable<BaseResponse<Any>>?


    /**
     * 获取配置列表（带参数）
     *
     * @param name @see 获取配置列表一级菜单返回的code
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun getScanConfigList(
        @Query("name") name: String,
        @Body filters: FiltersReq,
    ): Observable<BaseResponse<MenuResp>>?

    /**
     * 获取配置列表（带参数）
     *
     * @param name @see 获取配置列表一级菜单返回的code
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.Query")
    fun getScanConfigList(
        @Query("name") name: String?,
        @Query("scene") scene: String?,
        @Body filters: FiltersReq?,
    ): Observable<BaseResponse<MenuResp>>?

    /**
     * 获取配置列表（带UI 新版）
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.GetUiDefine")
    fun getMenuList(): Observable<BaseResponse<MenuCustomResp>>?

    /**
     * 登录
     *
     * @param req
     * @return
     */
    @POST("Kingdee.BOS.WebApi.ServicesStub.AuthService.ValidateUser.common.kdsvc")
    fun login(@Body req: LoginReq?): Observable<BaseResponse<UserInfoResp>>

    /**
     * 注销登录
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Authorize.Logout")
    fun logout(): Observable<BaseResponse<Any>>?

    /**
     * 获取最新的app版本
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.Basic.GetAppLastest")
    fun getAppLastest(): Observable<BaseResponse<AppLastest>>?

    /**
     * 获取文件
     *
     * @return
     */
    @POST("UNW.MOM.ServicesStub.OpenService.Invoke.common.kdsvc?action=UnionWare.FileServer.GetFileData")
    fun getFileData(@Body map: Map<String, String>): Observable<BaseResponse<Map<String, String>>>?

    @POST
    fun applyAuthInfo(
        @Url url: String,
        @Body req: AuthReq?,
    ): Observable<BaseResponse<Any>>

    @POST
    fun getAuthInfo(@Url url: String): Observable<BaseResponse<Any>>
}