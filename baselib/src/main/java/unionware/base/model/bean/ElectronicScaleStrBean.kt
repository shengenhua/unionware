package com.unionware.base.lib_common.model.bean

import com.google.gson.annotations.SerializedName


data class ElectronicScaleStrBean(
    @SerializedName("List")
    val list: MutableList<String>
)