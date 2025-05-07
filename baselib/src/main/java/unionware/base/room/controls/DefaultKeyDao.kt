package unionware.base.room.controls

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import unionware.base.room.table.DefaultKey
import unionware.base.room.table.User

/**
 * Author: sheng
 * Date:2024/12/12
 */
@Dao
interface DefaultKeyDao : BaseDao<DefaultKey> {
    @Transaction
    @Query("SELECT * FROM DefaultKey")
    fun getAll(): List<DefaultKey>

    @Transaction
    @Query("SELECT * FROM DefaultKey  WHERE userID = :userId and dbId = :dbId and app = :app")
    fun queryKey(userId: String?, dbId: String?, app: String): List<DefaultKey>?
}