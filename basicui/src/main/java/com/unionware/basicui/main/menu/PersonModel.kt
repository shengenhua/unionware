package com.unionware.basicui.main.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unionware.base.app.event.SingleLiveEvent
import unionware.base.model.req.OrgReq
import unionware.base.model.resp.DBCenterResp
import unionware.base.model.resp.UserInfoResp
import unionware.base.network.request
import unionware.base.room.DatabaseProvider
import unionware.base.room.table.Favourite
import javax.inject.Inject

/**
 * Author: sheng
 * Date:2025/1/3
 */
@HiltViewModel
class PersonModel @Inject constructor() : ScanConfigModel() {

    var userInfoLiveData: MutableLiveData<UserInfoResp> = SingleLiveEvent()
    var favouriteLiveData: SingleLiveEvent<List<Favourite>> = SingleLiveEvent()
    var userOrgLiveData: SingleLiveEvent<List<DBCenterResp>> = SingleLiveEvent()

    fun getOrgList() {
        api?.getUserOrgList()?.request(lifecycle) {
            success {
//                mView.showOrgListView(it)
                userOrgLiveData.postValue(it)
            }
            failure {
                postShowToastViewEvent(it.errorMsg)
            }
        }
    }

    fun setUserOrgInfo(req: OrgReq, name: String?) {
        api?.setUserOrgInfo(req)?.request(lifecycle) {
            success {
                userInfoLiveData.value?.apply {
                    this.name = name
                    this.orgId = req.orgId
                }.also {
                    userInfoLiveData.postValue(it)
                }
            }
            failure {
                postShowToastViewEvent(it.errorMsg)
            }
        }
    }

    fun getPersonInfo() {
        api?.getUserInfo()?.request(lifecycle) {
            success {
                userInfoLiveData.postValue(it)
            }
            failure {
                postShowToastViewEvent(it.errorMsg)
            }
        }
    }

    fun getFavourites() {
        /*val kv = MMKV.mmkvWithID("app")
        val tag =
            "${kv.decodeString("userId")}${kv.decodeString("dbId")}${kv.decodeString("orgId")}"*/
        lifecycle?.lifecycleScope?.launch(Dispatchers.IO) {
            val favouriteList =
                DatabaseProvider.getInstance().getFavouriteDao().queryByTag(mmkvFavouriteTag())
            favouriteList?.also {
                this.launch(Dispatchers.Main) {
                    favouriteLiveData.value = it
                }
            }
//            favouriteLiveData.postValue()
        }
    }

    private fun mmkvFavouriteTag(): String {
        val kv = MMKV.mmkvWithID("app")
        return "${kv.decodeString("userId")}${kv.decodeString("dbId")}${kv.decodeString("orgId")}"
    }

    fun deleteByPath(path: String) {
        lifecycle?.lifecycleScope?.launch(Dispatchers.IO) {
            val tag = mmkvFavouriteTag()
            if (DatabaseProvider.getInstance().getFavouriteDao().countBypathAndTag(path, tag) > 0) {
                DatabaseProvider.getInstance().getFavouriteDao().deleteByPathAndTag(path, tag)
                postShowToastViewEvent("取消收藏")
                getFavourites()
            }
        }
    }

}