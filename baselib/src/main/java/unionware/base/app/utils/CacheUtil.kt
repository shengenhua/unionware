package unionware.base.app.utils

import android.text.TextUtils
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import unionware.base.model.resp.UserInfoResp

object CacheUtil {
    /**
     * 获取保存的账户信息
     */

    fun getUser(key: String): UserInfoResp? {
        val kv = MMKV.mmkvWithID("app")
        val userStr = kv.decodeString(key)
        return if (TextUtils.isEmpty(userStr)) {
            null
        } else {
            Gson().fromJson(userStr, UserInfoResp::class.java)
        }
    }


    fun setUser(key: String, userInfo: UserInfoResp?) {
        val kv = MMKV.mmkvWithID("app")
        if (userInfo == null) {
            kv.encode(key, "")
            setIsLogin(false)
        } else {
            kv.encode(key, Gson().toJson(userInfo))
            setIsLogin(true)
        }
    }

    fun setBaseUrl(url: String) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("url", url);
    }

    fun getBaseUrl() {
        val kv = MMKV.mmkvWithID("app")
        kv.decodeString("url", "");
    }


    /**
     * 是否已经登录
     */
    fun isLogin(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("login", false)
    }

    /**
     * 设置是否已经登录
     */
    fun setIsLogin(isLogin: Boolean) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("login", isLogin)
    }

    /**
     * 是否是第一次登陆
     */
    fun isFirst(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("first", true)
    }
}