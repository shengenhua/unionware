package unionware.base.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author: sheng
 * Date:2025/1/3
 */
@Entity(tableName = "favourite")
class Favourite {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "color")
    var color: String? = null

    @ColumnInfo(name = "path")
    var path: String? = null

    @ColumnInfo(name = "icon")
    var icon: String? = null

    @ColumnInfo(name = "tag")
    var tag: String? = null


    constructor(name: String?, color: String?, path: String?, icon: String?, tag: String?) {
        this.name = name
        this.color = color
        this.path = path
        this.icon = icon
        this.tag = tag
    }
}