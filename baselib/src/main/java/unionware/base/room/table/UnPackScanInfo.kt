package unionware.base.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author: sheng
 * Date:2025/3/5
 */
@Entity(tableName = "UnPackScanInfo")
class UnPackScanInfo {
    /**
     * 设置自增长
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    /**
     * 配置内码
     */
    @ColumnInfo(name = "internalCodeId")
    var internalCodeId: String? = null

    /**
     * 在指定的箱码
     */
    @ColumnInfo(name = "packCode")
    var packCode: String? = null

    /**
     * 子箱
     */
    @ColumnInfo(name = "detailCode")
    var detailCode: String? = null

    /**
     * 子箱数量
     */
    @ColumnInfo(name = "detailQty")
    var detailQty: String? = null
}