package com.unionware.path

/**
 * Author: sheng
 * Date:2024/12/31
 */
class RouterPath {

    class Main {
        companion object {
            const val PATH_MAIN_HOME = "/main/home"
            const val PATH_MENU_HOME = "/main/menu/home"
        }
    }

    /**
     * app
     */
    class APP {
        /**
         * 装配
         */
        class ASSEM {
            companion object {
                /**
                 * 装配 列表
                 */
                const val PATH_ASSEM_LIST = "/virtual/assemble/list"

                /**
                 * 装配 单框 扫描
                 */
                const val PATH_ASSEM_ASSEMBLE_SCAN = "/virtual/assemble/single_scan"

                /**
                 * 装配
                 */
                const val PATH_ASSEM_ASSEMBLE = "/virtual/assemble/home"

                /**
                 * 装配 解绑
                 */
                const val PATH_ASSEM_UNASSEMBLE = "/virtual/assemble/unassemble"

                /**
                 * 装配 替换
                 */
                const val PATH_ASSEM_REASSEMBLE = "/virtual/assemble/reassemble"

            }
        }

        class MES {
            companion object {
                const val PATH_MES_BASIC = "/base/mes/basic"

                /**
                 *  采集数据
                 */
                const val PATH_MES_PROCESS_COLLECT = "/mes_process/collect"

                /**
                 * 巡检
                 */
                const val PATH_MES_INSPECTION = "/emes/xj"

                /**
                 * 老化架转移
                 */
                const val PATH_MES_AGEING_RACK_TRANSFER = "/emes/lhjzy"

                /**
                 * 装箱核对 目前先写mes
                 */
                const val PATH_MES_ZXHD = "/emes/zxhd"

                /**
                 * 工单产品转移
                 */
                const val PATH_MES_GDCPZY = "/emes/gdcpzy"

                /**
                 * 工单进度
                 */
                const val PATH_MES_GDJD = "/emes/gdjd"

                /**
                 * 不良检修判定
                 */
                const val PATH_MES_BLJXPD = "/emes/bljxpd"

                /**
                 *  工单生产故障记录
                 */
                const val PATH_MES_GDSCGZJL = "/mes/gdscgzjl"

                /**
                 *  明胶半成品货位绑定
                 */
                const val PATH_MES_MJBCPHWBD = "/mes/mjbcphwbd"

                /**
                 * 磷钙生产
                 */
                const val PATH_MES_LGSC = "/mes/lgsc"

                }
        }
    }

    class Person {
        companion object {
            const val PATH_PERSON_HOME = "/person/home"
            const val PATH_PERSON_LOGIN = "/person/login"
            const val PATH_PERSON_NET_CONFIG = "/person/net"
            const val PATH_PERSON_PRINT_CONFIG = "/person/print"
            const val PATH_SETTING_PRINTER_CONFIG = "/person/setting/printer"
        }
    }

    class Pack {
        companion object {
            const val PATH_PACK_HOME = "/pack/home"
            const val PATH_PACK_SCAN_MAIN = "/pack/scan/main"
        }
    }

    class Wms {
        companion object {
            const val PATH_WMS_BOX_MAIN = "/wms/box/main"
            const val PATH_WMS_BOX_SPLIT_MAIN = "/wms/box/split/main"
            const val PATH_WMS_SCAN_MAIN = "/wms/scan/main"

        }
    }

    class Srd {
        companion object {
            const val PATH_SRD_LHZY_MAIN = "/Srd/lhzy/main" //老化作业
        }
    }

    class Print {
        companion object {
            const val PATH_PRINT_SET_MAIN = "/Print/Set/main" //打印设置
        }
    }

    class RePrint {
        companion object {
            /**
             * 条码补打
             */
            const val PATH_WMS_BARCODE_REPRINTING = "/wms/reprinting"
        }
    }

    class Query {
        companion object {
            const val PATH_Query_STOCK_MAIN = "/query/Stock/main" //库存查询
        }
    }

    class MEIZhoYu {
        companion object {
            /**
             * 工序 带坯工序
             */
            const val PATH_ZhoYu_PROCESSES_PREFORM = "/ZhoYu/processes/preform"

            /**
             * 中裕 报工单打印
             */
            const val PATH_MES_ZHOYU_ORDER_PRINT = "/meszhoyu/order/print"

            /**
             * 中裕 计件工资查询
             */
            const val PATH_MES_ZHOYU_PIECE_RATE = "/meszhoyu/piece_rate"
        }
    }
}