package com.unionware.once.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unionware.once.model.FentityView
import unionware.base.model.bean.CommonListBean
import unionware.base.model.bean.ViewBean
import unionware.base.model.resp.CommonListDataResp
import java.util.stream.Collectors

class DataToViewUtil {

    fun convertMapToList(
        views: List<ViewBean>,
        data: List<Map<String?, Any?>>,
    ): List<CommonListBean> {
        val list: MutableList<CommonListBean> = ArrayList()
        val options = views.stream()
            .filter { obj: ViewBean -> obj.isVisible }
            .collect(
                Collectors.toMap({ obj: ViewBean -> obj.key }, { obj: ViewBean -> obj.name })
            )

        for (map in data) {
            for ((key1, value) in map) {
                val key = key1 as String
                val `val` = value?.toString() ?: ""
                if (options.containsKey(key)) {
                    list.add(CommonListBean(options[key], `val`))
                }
            }
        }
        return list
    }

    companion object {

        fun fentityMapToList(
            views: List<ViewBean>,
            data: List<Map<String, Any>>,
            listKey: String,
            titleKey: String,
            vararg append: String,
        ): List<FentityView> {
            val options = views.stream()
                .filter { obj: ViewBean -> obj.isVisible }
                .collect(
                    Collectors.toMap(
                        { obj: ViewBean -> obj.key },
                        { obj: ViewBean -> obj.name })
                )

            val fentity: MutableList<MutableMap<String, String>> = mutableListOf()
            data.forEach {
                fentity.addAll(it.let {
                    if (!it.containsKey(listKey)) {
                        return mutableListOf()
                    }
                    // as MutableList<MutableMap<String, String>>
                    val fentityList = anyToList(it[listKey])
                    fentityList.forEach { map ->
                        append.forEach { addName ->
                            map[addName] = it[addName].toString()
                        }
                    }
                    return@let fentityList
                })
            }
            val list: MutableList<FentityView> = ArrayList()
            for (map in fentity) {
                list.add(FentityView().apply {
                    this.map = map
                    this.view = ArrayList<FentityView.ShowView>().apply {
                        for ((key, value) in map) {
                            options[key]?.also {
                                FentityView.ShowView().apply {
                                    this.key = key
                                    this.value = value
                                    this.name = options[key]
                                }.also {
                                    if (key == titleKey) {
                                        titleView = it
                                    } else {
                                        this.add(it)
                                    }
                                }
                            }
                        }
                    }
                })
            }

            return list
        }

        fun anyToList(value: Any?): MutableList<MutableMap<String, String>> {
            if (value == null) {
                return mutableListOf()
            }
            if (value is MutableList<*>) {
                @Suppress("UNCHECKED_CAST")
                return value as MutableList<MutableMap<String, String>>
            } else {
                try {
                    return Gson().let {
                        val json = it.toJson(value)
                        Gson().fromJson(
                            json,
                            (object :
                                TypeToken<CommonListDataResp<MutableList<MutableMap<String, String>>>?>() {}).type
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return mutableListOf()
            }
        }
    }
}