package unionware.base.app.event.common

import unionware.base.app.event.BaseEvent


/**
 * 基于事件
 *
 * @author zhouhuan
 * @Data
 */
open class BaseActivityEvent<T>(code: Int) : unionware.base.app.event.BaseEvent<T>(code)
