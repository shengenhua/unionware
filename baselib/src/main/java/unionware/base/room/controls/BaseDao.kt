package unionware.base.room.controls

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Transaction
import androidx.room.Update


/**
 * Author: sheng
 * Date:2024/12/12
 */
interface BaseDao<T> {

    @Transaction
    @Insert
    fun insert(entity: T): Long

    @Transaction
    @Insert
    fun inserts(entitys: List<T>)

    @Transaction
    @Update
    fun update(entity: T)

    @Transaction
    @Delete
    fun delete(entity: T)

    @Transaction
    @Delete
    fun delete(vararg entities: T?)

    @Transaction
    @Delete
    fun deletes(entities: List<T>)
    // 可以根据需要添加更多的通用方法
}