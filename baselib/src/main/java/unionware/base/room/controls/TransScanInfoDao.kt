package unionware.base.room.controls

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import unionware.base.room.table.TransScanInfo

/**
 * Author: sheng
 * Date:2024/12/12
 */
@Dao
interface TransScanInfoDao : BaseDao<TransScanInfo> {
    @Transaction
    @Query("SELECT * FROM TransScanInfo")
    fun getAll(): List<TransScanInfo>

    @Transaction
    @Query("SELECT * FROM TransScanInfo  WHERE InternalCodeId = :codeId")
    fun queryByCode(codeId: String): List<TransScanInfo>?

    @Transaction
    @Query("SELECT * FROM TransScanInfo  WHERE InternalCodeId = :codeId and detailCode = :detailCode")
    fun queryByCodeAndDetailCode(codeId: String, detailCode: String): List<TransScanInfo>?

    @Transaction
    @Query("SELECT * FROM TransScanInfo  WHERE InternalCodeId = :codeId and TransType = :type")
    fun queryByCodeAndType(codeId: String, type: String): List<TransScanInfo>?

    @Transaction
    @Query("SELECT * FROM TransScanInfo  WHERE InternalCodeId = :codeId and OutBarcode = :barcode")
    fun queryByCodeAndOutBar(codeId: String, barcode: String): List<TransScanInfo>?

    @Transaction
    @Query("SELECT * FROM TransScanInfo  WHERE InternalCodeId = :codeId and TransType IS NULL ")
    fun queryByCodeAndTypeIsNull(codeId: String): List<TransScanInfo>?


    @Transaction
    @Query("SELECT * FROM TransScanInfo  WHERE InternalCodeId = :codeId and TransType IS NOT NULL ")
    fun queryByCodeAndTypeIsNotNull(codeId: String): List<TransScanInfo>?

    @Transaction
    @Query("DELETE FROM TransScanInfo  WHERE InternalCodeId = :codeId")
    fun deleteList(codeId: String)

    @Transaction
    @Query("DELETE FROM TransScanInfo  WHERE InternalCodeId = :codeId and TransType IS NULL ")
    fun deleteListTypeIsNull(codeId: String)
}