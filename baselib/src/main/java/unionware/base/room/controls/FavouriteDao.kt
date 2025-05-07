package unionware.base.room.controls

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import unionware.base.room.table.Favourite

/**
 * Author: sheng
 * Date:2024/12/12
 */
@Dao
interface FavouriteDao : BaseDao<Favourite> {
    @Transaction
    @Query("SELECT * FROM favourite")
    fun getAll(): List<Favourite>

    @Transaction
    @Query("SELECT * FROM favourite  WHERE tag = :tag")
    fun queryByTag(tag: String): List<Favourite>?

    @Transaction
    @Query("DELETE FROM favourite WHERE path = :path")
    fun deleteByPath(path: String)

    @Transaction
    @Query("SELECT COUNT(*) FROM favourite WHERE path = :path")
    fun countById(path: String): Int

    @Transaction
    @Query("DELETE FROM favourite WHERE path = :path AND tag = :tag")
    fun deleteByPathAndTag(path: String, tag: String)

    @Transaction
    @Query("SELECT COUNT(*) FROM favourite WHERE path = :path AND tag = :tag")
    fun countBypathAndTag(path: String, tag: String): Int
}