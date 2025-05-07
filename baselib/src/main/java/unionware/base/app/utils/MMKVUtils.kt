package unionware.base.app.utils

import com.tencent.mmkv.MMKV

/**
 * Author: sheng
 * Date:2024/11/6
 */
class MMKVUtils {

    companion object {

        val appMMKV = MMKV.mmkvWithID("app")

        fun withID(mmapID: String): MMKV {
            return MMKV.mmkvWithID(mmapID)
        }

        @JvmStatic
        fun decodeString() {
//            appMMKV.decodeString()
        }
    }
}