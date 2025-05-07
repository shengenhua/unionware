package unionware.base.room.controls

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import unionware.base.room.table.TransScanInfo
import unionware.base.room.table.UnPackScanInfo
import unionware.base.room.table.User

/**
 * Author: sheng
 * Date:2024/12/12
 */
@Dao
interface UnPackScanInfoDao : BaseDao<UnPackScanInfo> {
    @Transaction
    @Query("SELECT * FROM UnPackScanInfo")
    fun getAll(): List<UnPackScanInfo>

    @Transaction
    @Query("SELECT * FROM UnPackScanInfo  WHERE InternalCodeId = :codeId")
    fun queryByCode(codeId: String): List<UnPackScanInfo>?

    @Transaction
    @Query("SELECT * FROM UnPackScanInfo  WHERE DetailCode =:detailCode")
    fun queryByDetailCode(detailCode: String): List<UnPackScanInfo>?

    @Transaction
    @Query("SELECT * FROM UnPackScanInfo  WHERE InternalCodeId = :codeId and PackCode != :packCode and DetailCode =:detailCode  ")
    fun queryByCodeAPcADc(
        codeId: String,
        packCode: String,
        detailCode: String,
    ): List<UnPackScanInfo>?

    @Transaction
    @Query("SELECT * FROM UnPackScanInfo  WHERE InternalCodeId = :codeId and PackCode != :packCode  ")
    fun queryByCodeAPc(codeId: String, packCode: String): List<UnPackScanInfo>?

    @Transaction
    @Query("SELECT * FROM UnPackScanInfo  WHERE InternalCodeId = :codeId and DetailCode != :detailCode  ")
    fun queryByCodeADc(codeId: String, detailCode: String): List<UnPackScanInfo>?

    @Transaction
    @Query("DELETE FROM UnPackScanInfo  WHERE InternalCodeId = :codeId")
    fun deleteList(codeId: String)

    @Transaction
    @Query("DELETE FROM UnPackScanInfo  WHERE InternalCodeId = :codeId and DetailCode != :detailCode")
    fun deleteListByDetailNot(codeId: String, detailCode: String)
}