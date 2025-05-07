package unionware.base.model.req

class ReportReq {
    /**
     * 交互码
     */
    var sponsors: MutableList<String>? = null

    var data: MutableList<DataReq>? = null

    var params: MutableMap<String, Any>? = null


    class DataReq {
        /**
         * 工序id
         */
        var jobId: String? = null

        /**
         * 执行单 id
         */
        var taskId: String? = null

        var params: MutableMap<String, Any>? = null
    }
}