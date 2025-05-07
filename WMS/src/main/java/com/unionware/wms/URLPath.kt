package com.unionware.wms

class URLPath {
    class Pack {
        companion object {
            const val PATH_PACK_SCENE = "Packing" // 场景码
            const val PATH_PACK_SCAN_CONFIG_CODE = "63FDBB04545196" //获取装拆箱应用明细配置项
            const val PATH_PACK_SCAN_TASK_LIST = "63FD6D86477EB4" //获取临时装箱任务列表
            const val PATH_PACK_SCAN_PACKING_CODE = "63FD7EEB477EC2" // 获取临时装箱任务，包装条码信息
            const val PATH_PACK_SCAN_DETAILS_CODE = "63FD80D1477ED0" // 获取临时装箱任务，明细条码信息
            const val PATH_PACK_SCAN_CONFIG_NAME = "UNW_WMS_APPSETPACKING" //扫描配置项-装箱参数
        }
    }

    class Stock {
        companion object {
            const val PATH_STOCK_SCENE = "WMS.Stock" // 场景码
            const val PATH_STOCK_IN_PROGRESS_TASK_CODE = "64980EF6D54752" // 查询正在进行中的扫描任务
            const val PATH_STOCK_SCAN_TASK_CODE = "649E9DF4A864AD" // 扫描任务
            const val PATH_STOCK_SCAN_TASK_RECORD_CODE = "649EAE01A864C8" // 扫描记录
            const val PATH_STOCK_LPN_INFO = "649EAE01A864C8" // LPN
            const val PATH_STOCK_CONFIG_DETAILS = "649812D6D54755" // 普通扫描配置详情
            const val PATH_MENU_CONFIG_DETAILS = "649803CED54732" // 菜单配置详情(原WMS LPN菜单详情接口)

            const val PATH_SUBMIT_FINISH = 1001
        }
    }

    class WMS {
        companion object {
            const val PATH_WMS_PACK_SCENE = "WMS.Packing" // 场景码
            const val PATH_WMS_CONFIG_DETAILS = "666BEC0EF8F3A2" //

            const val MENU_WMS_PACKING_ID = "66456ad018dda1" // 条码装箱（WMS）
            const val MENU_WMS_UNPACKING_ID = "66456ae818dda4" // 条码拆箱（WMS）
            const val MENU_WMS_SPLIT_ID = "66456b4118dda7" // 条码拆分（WMS）

            /**
             * 条码装箱（WMS）
             */
            const val MENU_WMS_APP_PACKING = "UNW_WMS_APPSETPACKING"
            const val MENU_WMS_PACKING_FROM_ID = "UNW_WMS_JOB_INPACKING"

            /**
             * 条码拆箱（WMS）
             */
            const val MENU_WMS_APP_UNPACKING = "UNW_WMS_APPSETUNPACK"
            const val MENU_WMS_UNPACKING_FROM_ID = "UNW_WMS_JOB_UNPACKING"

            /**
             *  条码拆分（WMS）
             */
            const val MENU_WMS_APP_SPLIT = "UNW_WMS_APPSETDEPACK"
            const val MENU_WMS_SPLIT_FROM_ID = "UNW_WMS_JOB_SPLITCODE"

            /**
             * 普通扫描
             */
            const val MENU_WMS_APP_NORMALSCAN = "UNW_WMS_APPSETNORMALSCAN"
            const val MENU_WMS_APP_NORMALSCAN_CONFIGURATION_LIST = "663DCE002B90C2"//普通扫描应用配置列表
            const val MENU_WMS_APP_COMFIGURATION_DETAILS = "663DE22E2B90D8"//查询作业流程配置详情
            const val MENU_WMS_APP_SCAN_TASK = "663DE43F2B90E9"//查询正在进行中的扫描任务
            const val MENU_WMS_APP_SCAN_RECORD_TASK = "663DE5F52B90F6"//查询扫描任务条码扫描记录
        }

        class Name {
            companion object {
                /**
                 * 条码装箱
                 */
                const val WMS_PACKING_CONFIG_CODE = "664732EF7031D9";

                /**
                 * 条码装箱 查询未完成的条码装箱作业
                 */
                const val WMS_PACKING_NAME_CODE = "660115AE951CC7";

                /**
                 *条码装箱 明细列表 name code
                 */
                const val PATH_WMS_PACKING_QUERY_LIST_CODE = "660115F8951CC9";

                /**
                 * 条码拆箱
                 */
                const val WMS_UNPACKING_CONFIG_CODE = "6645719118DDA9";

                /**
                 *条码拆箱 name code
                 */
                const val WMS_UNPACKING_NAME_CODE = "6601624F951CCA"

                /**
                 *条码拆箱 明细列表 name code
                 */
                const val PATH_WMS_UNPACKING_LIST_CODE = "66016646951CCB"

                /**
                 * 条码拆分
                 */
                const val WMS_SPLIT_CONFIG_CODE = "664573F818DDBA";

                /**
                 *条码拆分 查询未完成的条码拆分作业
                 */
                const val WMS_SPLIT_NAME_CODE = "662B1527526C89"
            }
        }

    }

    class MESJIMEI {
        companion object {
            const val PATH_MES_SCENE = "MES.Normal" // 场景码
            const val PATH_MES_CONFIG_DETAILS = "65A7B6C3866971" // 查询正在进行中的扫描任务 65A7B6C3866971
            const val PATH_MES_INGREDIENT_REPORT_LIST_CODE = "65928D59133ACC"; // 配料
            const val PATH_MES_UPPER_MOLD_LIST_CODE = "6592ACC30F63E2"; // 上模
            const val PATH_MES_UPPER_MOLD_DETAILS_CODE = "6592ACD00F63E3"; // 上模
            const val PATH_MES_UPPER_MATERIAL_LIST_CODE = "6592ACC30F63E4"; // 上料
            const val PATH_MES_UPPER_MATERIAL_DETAILS_CODE = "6592ACD00F63E5"
            const val PATH_MES_DEBUG_MACHINE_LIST_CODE = "6592ACC30F63E6"; // 调机
            const val PATH_MES_FIRST_CHECK_LIST_CODE = "6592ACC30F63E8"; // 首检
            const val PATH_MES_FIRST_CHECK_DETAILS_CODE = "6592C0EA0F63E4"; //首检详情
            const val PATH_MES_CUSTOM_COMPLAINTS_LIST_CODE = "65E994049BD655" //客诉记录
            const val PATH_MES_ON_SITE_INSPECTION_LIST_CODE = "6593AA418E14D1"; // 巡检
            const val PATH_MES_TRANSFER_MANAGE_LIST_CODE = "65F2CD6635E226"; // 转单管理
            const val PATH_MES_ORDINARY_PROCESSES_LIST_CODE = "661779469DFB27" // 普通工序列表
            const val PATH_MES_PACKING_PROCESSES_LIST_CODE = "661793859DFD95" // 包装工序列表
            const val PATH_MES_PACKING_DISPATCH_LIST_QUERY_ID_CODE =
                "6684E366144DBC" // 派工单进度查询 派工单信息

            const val MENU_MES_BATCHING_REPORT_ID = "65a7889e866939" // 配料汇报
            const val MENU_MES_UPPER_MOLD_REPORT_ID = "65a788b786693b" // 上模汇报
            const val MENU_MES_UPPER_MATERIAL_REPORT_ID = "65a788c186693d" // 上料汇报
            const val MENU_MES_DEBUG_MACHINE_REPORT_ID = "65a788d486693f" // 调机汇报
            const val MENU_MES_EXECUTION_ORDER_CHANGE_REPORT_ID = "6660086bc34e9f" // 工序执行单变更
            const val MENU_MES_FIRST_CHECK_REPORT_ID = "65a788df866941" // 首检
            const val MENU_MES_ON_SITE_INSPECTION_REPORT_ID = "65a7891d866949" // 巡检
            const val MENU_MES_BAGGING_MACHINE_REPORT_ID = "65e705d1aef026" // 看机装袋
            const val MENU_MES_BAGGING_MACHINE_INSPECT_ID = "65a788ec866943" // 装袋检验
            const val MENU_MES_QUALIFIED_WEIGHT_REPORT_ID = "65a78904866945" // 良品称重
            const val MENU_MES_UNQUALIFIED_WEIGHT_REPORT_ID = "65a78913866947" // 不良品称重
            const val MENU_MES_FAULT_RECORD_REPORT_ID = "65a7892b86694b" // 机台故障记录
            const val MENU_MES_SPECIALLY_APPROVED_REPORT_ID = "65a7893a86694d" // 特批生产申请
            const val MENU_MES_SPECIALLY_CONFIRM_REPORT_ID = "65a7894786694f" // 特批生产确认
            const val MENU_MES_UNLOAD_MOLD_REPORT_ID = "65a78953866951" // 下模汇报
            const val MENU_MES_UNLOAD_MOLD_EXCEPTION_REPORT_ID = "6630916b6d6633" // 下模异常汇报
            const val MENU_MES_TRANSFER_ORDER_REPORT_ID = "65a7895c866953" // 转单管理
            const val MENU_MES_EXCEPTION_TRANSFER_ORDER_REPORT_ID = "667392808bb0d1" // 异常转单管理
            const val MENU_MES_PRODUCT_INSPECT_ID = "65a789ae866957" // 产品检验
            const val MENU_MES_OUTSOURCED_PRODUCT_INSPECT_ID = "662a2b3173d2f7" //自由派工检验
            const val MENU_MES_OUTSOURCED_PRODUCT_INSPECT_ID_2 = "6716f815859d70" //委外产品检验
            const val MENU_MES_ORDINARY_PROCESSES_ID = "65a789a0866955" // 普通工序
            const val MENU_MES_PACKING_PROCESSES_ID = "65a7985886695f" // 包装工序
            const val MENU_MES_OUTSOURCED_RECEPTION_PROCESSES_ID = "66051b308b58e1" // 委外接收称重
            const val MENU_MES_OUTSOURCED_RECEPTION_PROCESSES_ID_2 = "66051b308b58e1-2" // 委外接收称重 列表二 只做列表显示
            const val MENU_MES_TEMPERATURE_CONTROL_BOX_ID = "66515eb7b4d1f1" // 温控箱列表
            const val MENU_MES_REWORK_INSPECTION_ID = "65a789bb866959" // 返工汇报
            const val MENU_MES_REWORK_ADVERSE_ID = "65a789c386695b" // 不良汇报
            const val MENU_MES_DISPATCH_LIST_QUERY_ID = "666005c7c34e9b" // 派工单进度查询
            const val MENU_MES_RESET_MACHINE_SERIAL_ID = "667a9ee1143653" // 清空机台流水码
            const val MENU_MES_TEMPORARILY_PROCESSES_ID = "668ce882dd1490" // 临时工序汇报
            const val MENU_MES_QUERY_INSPECT_INFO_ID = "6694b5ef88e4ee" // 检验信息查询
            const val MENU_MES_MATERIAL_QUERY_ID = "668bce71dd105a" // 配料查询

            /**
             * 注塑产品检验
             */
            const val MENU_MES_INJECTION_PRODUCT_INSPECT_ID = "669f8712070f55"

            /**
             *  称重汇报
             */
            const val MENU_MES_WEIGHING_INSPECT_ID = "6715ea78859d68"
            /**
             * 进入称重汇报界面  点击 超产汇报 按钮进入此界面
             *  超产汇报界面
             */
            const val MENU_MES_WEIGHING_EXCEED_ID = "6715ea78859d68_2"

            /**
             *  包装上料核对
             */
            const val MENU_MES_PACKMATERIAL_CHECK = "672c6cdf252811"
            /**
             *  半成品组装需求 Semi-finished product
             */
            const val MENU_MES_SEMI_FINISHED_PRODUCT= "673d8822504b21"
            /**
             * 配货汇报  6789bc1759b861 //678726c9588a31
             */
            const val MENU_MES_INVENTORY_REPORT_ID = "678726c9588a31"
            /**
             * 按箱称重
             */
            const val MENU_MES_WEIGHING_BOX_ID = "6789fc306947cd"
            /**
             * 称重拆包 primaryId=678a155ac74763
             */
            const val MENU_MES_UN_PACKING_ID = "678a155ac74763"
            /**
             * 打包
             */
            const val MENU_MES_PACKING_ID = "6789c4f68d44b7"
        }
    }

    class Trans {
        companion object {
            const val PATH_TRANS_FORM_ID = "UN_Packaging" // 获取套打模板，formId 固定传值
        }
    }

    /**
     *  条码补打
     */
    class BarcodeReprinting {
        companion object {
            const val PATH_BARCODEREPRINTING_FORM_ID = "UNW_WMS_BARCODEMAIN" // 获取套打模板，formId 固定传值  或业务对象
            const val PATH_PACK_SCENE = "WMS.CodeReprint" // 场景码
        }
    }
    /**
     *  施罗得二开
     */
    class Srd {
        companion object {
            const val PATH_SRD_ID = "app://UNW_CMES_LHZYJL/65a789a0866967" // 老化作业记录
        }
    }

}