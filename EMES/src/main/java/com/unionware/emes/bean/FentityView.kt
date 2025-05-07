package com.unionware.emes.bean

class FentityView {
    var map: Map<String, String>? = null
    var titleView: ShowView? = null
    var view: MutableList<ShowView>? = null

    class ShowView {
        var name: String? = null
        var key: String? = null
        var value: String? = null
    }
}