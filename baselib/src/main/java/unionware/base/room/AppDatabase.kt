package unionware.base.room

import androidx.room.Database
import androidx.room.RoomDatabase
import unionware.base.room.controls.DefaultKeyDao
import unionware.base.room.controls.DefaultValueInfoDao
import unionware.base.room.controls.FavouriteDao
import unionware.base.room.controls.TransScanInfoDao
import unionware.base.room.controls.UnPackScanInfoDao
import unionware.base.room.controls.UserDao
import unionware.base.room.table.DefaultKey
import unionware.base.room.table.DefaultValueInfo
import unionware.base.room.table.Favourite
import unionware.base.room.table.TransScanInfo
import unionware.base.room.table.UnPackScanInfo
import unionware.base.room.table.User


/**
 * Author: sheng
 * Date:2024/9/29
 */
@Database(
    entities = [User::class, Favourite::class, TransScanInfo::class, UnPackScanInfo::class, DefaultKey::class, DefaultValueInfo::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao//
    abstract fun favouriteDao(): FavouriteDao
    abstract fun transScanInfoDao(): TransScanInfoDao
    abstract fun unPackScanInfoDao(): UnPackScanInfoDao

    abstract fun defaultKeyDao(): DefaultKeyDao
    abstract fun defaultValueInfoDao(): DefaultValueInfoDao
}