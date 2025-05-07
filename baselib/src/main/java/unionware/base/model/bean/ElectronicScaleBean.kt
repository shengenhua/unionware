package com.unionware.base.lib_common.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ElectronicScaleBean(
    @SerializedName("List")
    val list: MutableList<Material>
) : Serializable {
    data class Material(
        @SerializedName("MaterialCode")
        val materialCode: String, // 0
        @SerializedName("MaterialName")
        val materialName: String, // Apple
        @SerializedName("NetWeight")
        val netWeight: String, // 0.2034
        @SerializedName("ScaleID")
        val scaleID: String, // X4849da3a282
        @SerializedName("WeightUnit")
        val weightUnit: String // kg
    ) : Serializable
}