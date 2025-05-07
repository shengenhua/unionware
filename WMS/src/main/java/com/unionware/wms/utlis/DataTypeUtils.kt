package com.unionware.wms.utlis

import java.util.*

fun garCodeGroup(): Set<String> {
    return HashSet(Arrays.asList("FBarCodeId_Proxy", "FBoxCodeId_Proxy", "FNewCodeId"))
}
fun baseDataGroup(): Set<String> {
    return HashSet(Arrays.asList("BASEDATA",
        "ASSISTANT",
        "COMBOBOX",
        "COMBOX",
        "CHECKBOX",
        "RADIOBOX",
        "ITEMCLASS"))
}