package com.unionware.basicui.main.menu

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.tencent.mmkv.MMKV
import com.unionware.lib_base.utils.ext.formatterYMD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import unionware.base.api.UserApi
import unionware.base.app.event.SingleLiveEvent
import unionware.base.app.model.AppLastest
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.model.bean.MenuBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.LoginReq
import unionware.base.model.req.MenuCustomResp
import unionware.base.model.resp.UserInfoResp
import unionware.base.network.request
import unionware.base.route.URouter
import unionware.base.util.AppUpdateUtil
import java.io.File
import javax.inject.Inject

/**
 * Author: sheng
 * Date:2024/12/31
 */
@HiltViewModel
open class ScanConfigModel @Inject constructor() : BaseViewModel() {

    @JvmField
    @Inject
    var api: UserApi? = null//ApiHelper.getInstance().userApi

    private val startConList: MutableList<String> = mutableListOf()

    var menuConfigLiveData: SingleLiveEvent<MenuBean> = SingleLiveEvent()
    var menuLiveData: SingleLiveEvent<MenuCustomResp> = SingleLiveEvent()
    /**
     * 检测app版本
     */
    val appVersionLiveData: SingleLiveEvent<AppLastest> = SingleLiveEvent()

    /**
     * app文件 下载
     */
    val appFileLiveData: SingleLiveEvent<File?> = SingleLiveEvent()

    /**
     * query  查询特色处理  query://场景吗/配置id
     * 666BEC0EF8F3A2  通用查询 -> 判断是否查询详细配置信息 -> 获取配置信息 -> 打开具体界面
     *
     *
     */
    fun getScanConfigList(scene: String? = "MES.Normal", link: String) {
        if (link.contains("query")) {
            getQueryConfig(link)
            return
        }
        val primaryId: String = link.substring(link.lastIndexOf("/") + 1)
        //通一的 接口获取场景码
        api?.getScanConfigList(
            "666BEC0EF8F3A2", scene,
            FiltersReq(
                mutableMapOf(
                    Pair(
                        "primaryId",
                        primaryId as Any
                    )
                )
            )
        )?.request(lifecycle) {
            success {
                if (startConList.contains(link)) {
                    return@success
                }
                if (it == null || it.data == null || it.data.isEmpty()) {
                    postShowToastViewEvent("未查询到相关配置信息")
                    return@success
                }
                it.data?.get(0)?.apply {
                    startConList.add(link)
                    //判断是否查询详细配置信息
                    URouter.build().getAppDetails(link).also { name ->
                        if (name.isNullOrEmpty()) {
                            //获取配置信息
                            startConList.remove(link)
                            menuConfigLiveData.postValue(this.clone().apply {
                                url = link
                            })
                            return@apply
                        } else {
                            getAppConfig(name, link, this)
                        }
                    }
                }
            }
            failure {
                postShowToastViewEvent(it.errorMsg)
            }
        }
    }

    private fun getQueryConfig(link: String) {
        URouter.build().builder(link).also { router ->
            api?.getScanConfigList(
                "UNW_WMS_QUERYSCHEMA",
                router.module,
                FiltersReq(mutableMapOf<String?, Any?>().apply {
                    this["primaryId"] = router.address
                })
            )?.request(lifecycle) {
                success {
                    it?.data?.get(0)?.clone()?.apply {
                        scene = scene.ifEmpty { router.module }
                        url = link
                    }.also {
                        menuConfigLiveData.postValue(it)
                    }
                }
                failure {
                    postShowToastViewEvent(it.errorMsg)
                }
            }
        }
    }

    /**
     * 获取配置信息
     */
    private fun getAppConfig(
        name: String,
        link: String,
        config: MenuBean,
    ) {
        api?.getScanConfigList(
            name,
            config.scene,
            FiltersReq(
                mutableMapOf(
                    Pair(
                        "primaryId",
                        config.id as Any
                    )
                )
            )
        )?.request(lifecycle) {
            success {
                lifecycle?.lifecycleScope?.launch {
                    //打开具体界面
                    /*mView.initMenuDetails(it?.data?.get(0)?.apply {
                        id = config.id
                        scene = config.scene
                    }, link, "1")*/
                    it?.data?.get(0)?.clone()?.apply {
                        id = config.id
                        scene = config.scene
                        url = link
                    }.also {
                        menuConfigLiveData.postValue(it)
                    }

                    withContext(Dispatchers.IO) {
                        delay(1000)
                        startConList.remove(link)
                    }
                }
            }
            failure {
                postShowToastViewEvent(it.errorMsg)
            }
        }
    }

    /**
     * 获取菜单列表
     */
    fun getMenuList() {
        api?.getMenuList()?.request(lifecycle) {
            success {
                menuLiveData.postValue(it)
            }
            failure {
                postShowToastViewEvent(it.errorMsg)
            }
        }
    }

    var loginUserLiveData: MutableLiveData<UserInfoResp?> = MutableLiveData()

    fun getLoginInfo() {
        val userInfoResp = UserInfoResp()
        MMKV.mmkvWithID("app").apply {
            userInfoResp.also {
                it.name = this.decodeString("userName")
                it.userId = this.decodeString("userId")
                it.dbId = this.decodeString("dbId")
                it.orgId = this.decodeString("orgId")
            }
        }
        loginUserLiveData.postValue(userInfoResp)
    }

    fun login(model: LoginReq?) {
        api?.login(model)?.request(lifecycle) {
            success {
                loginUserLiveData.postValue(it)
                getAppLastest()
//                getMenuList()
            }
            failure {
                postShowToastViewEvent(it.errorMsg)
            }
        }
    }

    fun getAppLastest() {
        MMKV.mmkvWithID("app").apply {
            val checkUpdateAppTime = this.decodeString("CheckUpdateAppTime")
            if (checkUpdateAppTime == System.currentTimeMillis().formatterYMD()) {
                return
            }
        }
        api?.getAppLastest()?.request(lifecycle) {
            success {
                if(it == null){
                    return@success
                }
                appVersionLiveData.value = it
            }
            onFailureLogin = { code, data ->
                false
            }
        }
    }


    fun getAppFile(fileId: String) {
        api?.getFileData(mutableMapOf<String, String>().apply {
            put("fileId", fileId)
        })?.request(lifecycle) {
            success {
                var fileName: String = it?.get("fileName").toString()
                if (TextUtils.isEmpty(fileName)) {
                    fileName = "newApp.apk"
                } else if (!fileName.contains(".apk")) {
                    fileName = "$fileName.apk"
                }
                val file = AppUpdateUtil.base64ToFile(it?.get("data"), fileName)
                //设置apk下载地址：本机存储的download文件夹下
                //找到该路径下的对应名称的apk文件，有可能已经下载过了
                appFileLiveData.value = file
            }
        }
    }
}