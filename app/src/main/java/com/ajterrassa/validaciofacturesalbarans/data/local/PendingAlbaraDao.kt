package com.ajterrassa.validaciofacturesalbarans.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingAlbaraDao {

    @Insert
    suspend fun insert(entity: PendingAlbaraEntity): Long

    @Update
    suspend fun update(entity: PendingAlbaraEntity)

    @Query("DELETE FROM pending_albara WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM pending_albara WHERE id = :id")
    suspend fun get(id: Long): PendingAlbaraEntity?

    // En viu (detall)
    @Query("SELECT * FROM pending_albara WHERE id = :id")
    fun observeById(id: Long): Flow<PendingAlbaraEntity?>

    // LiveData (si et cal compatibilitat anterior)
    @Query("SELECT * FROM pending_albara WHERE id = :id")
    fun getLive(id: Long): LiveData<PendingAlbaraEntity?>

    // Llistes en viu
    @Query("SELECT * FROM pending_albara ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<PendingAlbaraEntity>>

    @Query("SELECT * FROM pending_albara WHERE status = :status ORDER BY createdAt DESC")
    fun observeByStatus(status: String): Flow<List<PendingAlbaraEntity>>
}
