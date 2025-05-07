package unionware.base.room

import android.content.Context
import androidx.room.Room
import unionware.base.room.controls.DefaultKeyDao
import unionware.base.room.controls.DefaultValueInfoDao
import unionware.base.room.controls.FavouriteDao
import unionware.base.room.controls.TransScanInfoDao
import unionware.base.room.controls.UnPackScanInfoDao
import unionware.base.room.controls.UserDao

class DatabaseProvider private constructor(context: Context) {
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "new_user_database.db")
            .build()
    }

    fun getAppDatabase(): AppDatabase {
        return database
    }

    fun getUserDao(): UserDao {
        return database.userDao()
    }

    fun getFavouriteDao(): FavouriteDao {
        return database.favouriteDao()
    }

    fun getTransScanInfoDao(): TransScanInfoDao {
        return database.transScanInfoDao()
    }

    fun getUnPackScanInfoDao(): UnPackScanInfoDao {
        return database.unPackScanInfoDao()
    }

    fun getDefaultKeyDao(): DefaultKeyDao {
        return database.defaultKeyDao()
    }

    fun getDefaultValueInfoDao(): DefaultValueInfoDao {
        return database.defaultValueInfoDao()
    }

    companion object {
        private var instance: DatabaseProvider? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = DatabaseProvider(context)
            }
        }

        @JvmStatic
        fun getInstance(): DatabaseProvider {
            return instance
                ?: throw IllegalStateException("DatabaseProvider must be initialized first.")
        }
    }
}