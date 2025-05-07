package com.unionware.emes.bean

import com.google.gson.annotations.SerializedName
import unionware.base.ext.tryBigDecimalToZeros
import java.io.Serializable

/**
 * Author: sheng
 * Date:2024/9/20
 */
class SubJobsBean : Serializable {
    @SerializedName("code")
    val code: String? = null

    @SerializedName("date")
    val date: String? = null

    @SerializedName("id")
    val taskId: Int? = null

    @SerializedName("subJobId")
    val id: Int? = null

    @SerializedName("subJobNumber")
    val number: String? = null
        get() {
            return field.tryBigDecimalToZeros()
        }

    @SerializedName("subJobName")
    val name: String? = null

    /**
     *  勾选状态
     */
    @SerializedName("subIsExecute")
    val isExecute: String? = null

    @SerializedName("subSeq")
    val subSeq: String? = null
        get() {
            return field.tryBigDecimalToZeros()
        }
}