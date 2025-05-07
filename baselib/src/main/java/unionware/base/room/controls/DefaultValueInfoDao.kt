package unionware.base.room.controls

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import unionware.base.room.table.DefaultValueInfo

/**
 * Author: sheng
 * Date:2024/12/12
 */
@Dao
interface DefaultValueInfoDao : BaseDao<DefaultValueInfo> {
    @Transaction
    @Query("SELECT * FROM DefaultValueInfo")
    fun getAll(): List<DefaultValueInfo>

    @Transaction
    @Query("SELECT * FROM DefaultValueInfo WHERE id = :id")
    fun queryByKey(id: String): List<DefaultValueInfo>?

    @Transaction
    @Query("DELETE FROM DefaultValueInfo WHERE id = :id")
    fun deleteList(id: String)
}