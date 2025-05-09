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
        class MES {
            companion object {
                const val PATH_MES_BASIC = "/base/mes/basic"
            }
        }
    }

    class Person {
        companion object {
            const val PATH_PERSON_HOME = "/person/home"
            const val PATH_PERSON_LOGIN = "/person/login"
            const val PATH_PERSON_NET_CONFIG = "/person/net"
        }
    }

    class Print {
        companion object {
            const val PATH_PRINT_SET_MAIN = "/Print/Set/main" //打印设置
        }
    }
}