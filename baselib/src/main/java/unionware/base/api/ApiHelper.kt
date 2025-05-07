package unionware.base.api

import unionware.base.api.basic.BasicApi
import unionware.base.api.SimulateApi
import javax.inject.Inject

/**
 * Author: sheng
 * Date:2024/11/14
 */
class ApiHelper @Inject constructor() {

    companion object {
        private var instance: ApiHelper? = null

        @JvmStatic
        fun getInstance(): ApiHelper {
            if (instance == null) {
                instance = ApiHelper()
            }
            return instance!!
        }
    }

    @JvmField
    @Inject
    var basicApi: BasicApi? = null
    /**
     * 基础接口
     */


    /**
     * 基础接口
     */
    @JvmField
    @Inject
    var userApi: UserApi? = null

    /**
     * 虚拟视图
     */
    @JvmField
    @Inject
    var simulateApi: SimulateApi? = null

    /**
     * 作业流程
     */
    @JvmField
    @Inject
    var flowTaskApi: FlowTaskApi? = null

    /**
     * 文件服务
     */
    @JvmField
    @Inject
    var fileServerApi: FileServerApi? = null
}