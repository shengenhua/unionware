package com.unionware.mes.app

/**
 * Author: sheng
 * Date:2025/3/5
 */
class RouterMESPath {
    public class MES {
        companion object {
            /**
             * 生产订单 列表
             */
            const val PATH_MES_PRDLIST = "/mes/bill/prdlist"

            /**
             * 列表
             */
            const val PATH_MES_LIST = "/mes/bill/list"

            /**
             * 明细
             */
            const val PATH_MES_BILL_DETAILS = "/mes/bill/details"

            /**
             *  动态汇报
             */
            const val PATH_MES_DYNAMIC = "/mes/dynamic"
        }
    }
}