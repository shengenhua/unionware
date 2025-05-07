package com.unionware.emes

import android.content.Context
import androidx.startup.Initializer
import com.unionware.mes.MESPath

/**
 * Author: sheng
 * Date:2024/12/4
 */
class EMesInitializer : Initializer<Unit> {
    companion object {
        /**
         * 诺安 电子车间 明细
         */
        const val PATH_MES_CORPUSCLE_DETAILS = "/emes/corpuscle/details"
    }

    override fun create(context: Context) {
        //替换 详情 改成 电子车间详情
        MESPath.replacePathTag(MESPath.PathTag.DETAILS, PATH_MES_CORPUSCLE_DETAILS)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}



