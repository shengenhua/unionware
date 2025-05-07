package unionware.base.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author: sheng
 * Date:2024/12/31
 */
@Entity(tableName = "User")
class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "userName")
    var userName: String? = null // 用户名

    @ColumnInfo(name = "password")
    var password: String? = null //密码

    @ColumnInfo(name = "dbName")
    var dbName: String? = null // 账套名称

    @ColumnInfo(name = "dbId")
    var dbId: String? = null // 账套id

    @ColumnInfo(name = "tag")
    var tag: String? = null // 存储标识

    @ColumnInfo(name = "orgName")
    var orgName: String? = null // 组织名称

    @ColumnInfo(name = "orgId")
    var orgId: String? = null // 组织id

    @ColumnInfo(name = "email")
    var email: String? = null

    @ColumnInfo(name = "phone")
    var phone: String? = null

    @ColumnInfo(name = "serverVersion")
    var serverVersion: String? = null // 数据库版本
}