package unionware.base.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Author: sheng
 * Date:2025/3/6
 */
/*@Entity(
    tableName = "DefaultValueInfo",
    foreignKeys = [ForeignKey(
        entity = DefaultKey::class,
        parentColumns = ["defaultKey"],
        childColumns = ["id"],
        onDelete = ForeignKey.CASCADE
    )]
)*/
@Entity( tableName = "DefaultValueInfo")
class DefaultValueInfo {
    /**
     * 自增 id
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primaryId")
    var primaryId: Long? = null

    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "key")
    var key: String? = null

    @ColumnInfo(name = "value")
    var value: String? = null

}