package unionware.base.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author: sheng
 * Date:2025/3/5
 */
@Entity(tableName = "TransScanInfo")
class TransScanInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "internalCodeId")
    var internalCodeId: String? = null //配置内码

    @ColumnInfo(name = "inBarcode")
    var inBarcode: String? = null //转入箱码

    @ColumnInfo(name = "outBarcode")
    var outBarcode: String? = null //转出箱码

    @ColumnInfo(name = "detailCode")
    var detailCode: String? = null //子项条码

    @ColumnInfo(name = "detailCodeType")
    var detailCodeType: String? = null //子项条码类型

    @ColumnInfo(name = "detailQty")
    var detailQty: String? = null //子箱数量

    @ColumnInfo(name = "transType")
    var transType: String? = null //转箱类型  1.整箱转，2.按箱指定子项转箱，3.按子项转箱

}