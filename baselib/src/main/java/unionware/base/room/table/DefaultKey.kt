package unionware.base.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author: sheng
 * Date:2025/3/6
 */
@Entity(tableName = "DefaultKey")
class DefaultKey {
    /**
     * 自增 id
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "defaultKey")
    var defaultKey: Long? = null

    /**
     * 登陆的账号标识
     */
    @ColumnInfo(name = "userID")
    var userID: String? = null

    /**
     * 服务器iD
     */
    @ColumnInfo(name = "dbId")
    var dbId: String? = null

    /**
     * 选择的功能表示 app
     */
    @ColumnInfo(name = "app")
    var app: String? = null

    /**
     * 是否启用
     */
    @ColumnInfo(name = "isDefault")
    var isDefault: Boolean = false
}