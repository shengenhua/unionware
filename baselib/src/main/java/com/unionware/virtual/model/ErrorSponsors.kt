package com.unionware.virtual.model

import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq
import unionware.base.model.req.ViewReq

class ErrorSponsors(
    var errorMag: String
) {
    var reportReq: ReportReq? = null
    var filtersReq: FiltersReq? = null
    var viewReq: ViewReq? = null
}