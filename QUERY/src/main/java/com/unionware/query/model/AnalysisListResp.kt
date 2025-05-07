package com.unionware.query.model

import unionware.base.model.resp.ActionResp
import unionware.base.model.resp.CommonListDataResp
import java.io.Serializable

/**
 * Author: sheng
 * Date:2025/3/7
 */
class AnalysisListResp : Serializable {
    var action: List<ActionResp>? = null

    var data: CommonListDataResp<Map<String, Any>>? = null
}