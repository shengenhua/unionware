package com.unionware.basicui.setting

import android.text.TextUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.UserApi
import unionware.base.app.event.SingleLiveEvent
import unionware.base.app.model.AppLastest
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.network.cookie.UnionwareCookieJar
import unionware.base.network.request
import unionware.base.util.AppUpdateUtil
import java.io.File
import javax.inject.Inject


/**
 * Author: sheng
 * Date:2025/1/3
 */
@HiltViewModel
class SettingViewModel @Inject constructor() : BaseViewModel() {
//    private var api: UserApi = ApiHelper().userApi!!

    /**
     * 检测app版本
     */
    val appVersionLiveData: SingleLiveEvent<AppLastest> = SingleLiveEvent()
    /**
     * app文件 下载
     */
    val appFileLiveData: SingleLiveEvent<File?> = SingleLiveEvent()

    @JvmField
    @Inject
    var api: UserApi? = null// ApiHelper.getInstance().userApi

    fun loginOut() {
        api?.logout()?.request(lifecycle) {
            success {
                postShowToastViewEvent("退出成功!")
            }
            failure {
                postShowToastViewEvent(it.errorMsg)
            }
        }
    }


    fun getAppLastest() {
        api?.getAppLastest()?.request(lifecycle) {
            success {
                if(it == null){
                    postShowToastViewEvent("暂无新版本")
                    return@success
                }
                appVersionLiveData.value = it
            }
            failure {
                postShowToastViewEvent("暂无新版本！")
            }
            onFailureLogin = { _, _ ->
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