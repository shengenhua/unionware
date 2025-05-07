package unionware.base.room.controls

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import unionware.base.room.table.User

/**
 * Author: sheng
 * Date:2024/12/12
 */
@Dao
interface UserDao : BaseDao<User> {
    @Transaction
    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Transaction
    @Query("SELECT * FROM User WHERE id IN(:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>?

    @Transaction
    @Query("SELECT * FROM User  WHERE userName LIKE:name")
    fun findByName(name: String): User?

    @Transaction
    @Query("SELECT * FROM User  WHERE tag = :tag")
    fun queryByTag(tag: String): List<User>?

    @Transaction
    @Query("SELECT * FROM User  WHERE tag = :tag and userName = :name")
    fun query(tag: String, name: String): User?

    @Transaction
    @Query("DELETE FROM User WHERE tag = :tag and userName = :name")
    fun deleteList(tag: String, name: String)
}