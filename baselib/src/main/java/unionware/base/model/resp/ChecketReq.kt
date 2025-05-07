package unionware.base.model.resp

class ChecketReq {
    /**
     * 交互码
     */
    var sponsors: MutableList<String>? = null

    //    var data: DataReq? = null
    var data: MutableList<Map<String, Any?>>? = null
}