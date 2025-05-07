package com.unionware.mes

import com.unionware.mes.app.RouterMESPath
import com.unionware.path.RouterPath


/**
 * Author: sheng
 * Date:2024/12/3
 */
class MESPath {
    enum class PathTag(tag: String) {
        LIST("列表"), DETAILS("详情"), ASSEMBLE("装配"), COLLECT("采集"), DYNAMIC("动态"), OTHER("其他")
    }

    companion object {
        private var pathTagListener: MutableMap<PathTag, () -> String> = mutableMapOf()
        private var pathTagControl: MutableMap<PathTag, String> = mutableMapOf()

        @JvmStatic
        fun renewalPathTag(pathTag: PathTag) {
            pathTagControl.remove(pathTag)
            pathTagListener.remove(pathTag)
        }

        @JvmStatic
        fun replacePathTag(pathTag: PathTag, unit: () -> String) {
            pathTagListener[pathTag] = unit
        }

        @JvmStatic
        fun replacePathTag(pathTag: PathTag, path: String) {
            pathTagControl[pathTag] = path
        }

        @JvmStatic
        fun openPath(pathTag: PathTag): String? {
            if (pathTagListener.containsKey(pathTag)) {
                return pathTagListener[pathTag]?.invoke()
            }
            if (pathTagControl.containsKey(pathTag) && pathTagControl[pathTag] != null) {
                return pathTagControl[pathTag].toString()
            }
            return when (pathTag) {
                PathTag.LIST -> return RouterMESPath.MES.PATH_MES_LIST
                PathTag.DETAILS -> return RouterMESPath.MES.PATH_MES_BILL_DETAILS
                PathTag.ASSEMBLE -> return RouterPath.APP.ASSEM.PATH_ASSEM_ASSEMBLE_SCAN
                PathTag.COLLECT -> return RouterPath.APP.MES.PATH_MES_PROCESS_COLLECT
                PathTag.DYNAMIC -> return RouterMESPath.MES.PATH_MES_DYNAMIC
                else -> null
            }
        }
    }
}