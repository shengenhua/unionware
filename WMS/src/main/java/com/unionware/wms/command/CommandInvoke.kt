package com.unionware.wms.command

class CommandInvoke {
    class Pack {
        companion object {
            /**
             * 提交任务
             */
            const val WMS_SUBMIT_TASK = "INVOKE_SUBMITTASK"

            /**
             * 作废任务
             */
            const val WMS_CANCEL_TASK = "INVOKE_CANCELTASK"

            /**
             * 确认录入
             */
            const val WMS_CONFIRM_BARCODE = "INVOKE_CONFIRMBARCODE"

            /**
             * 关箱
             */
            const val WMS_CLOSE_BOX_CODE = "INVOKE_CLOSEBOXCODE"

            /**
             * 生成包装码
             */
            const val WMS_CREATE_BOX_CODE = "INVOKE_CREATEBOXCODE"

            /**
             * 生成转入包装码
             */
            const val WMS_CREATE_NEW_CODE = "INVOKE_CREATENEWCODE"

            /**
             * 设置拆分数量
             */
            const val WMS_SET_SPLIT_QTY = "INVOKE_SETSPLITQTY"
        }
    }
}