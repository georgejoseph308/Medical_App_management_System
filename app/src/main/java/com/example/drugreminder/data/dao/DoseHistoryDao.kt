package com.example.drugreminder.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.drugreminder.data.model.DoseHistory

@Dao
interface DoseHistoryDao {

    @Insert
    suspend fun insertHistory(doseHistory: DoseHistory)

    @Query("SELECT * FROM dose_history ORDER BY takenAt DESC")
    fun getAllHistory(): LiveData<List<DoseHistory>>

    @Query("SELECT * FROM dose_history WHERE medicineId = :medicineId ORDER BY takenAt DESC")
    fun getHistoryByMedicine(medicineId: Int): LiveData<List<DoseHistory>>

    @Query("DELETE FROM dose_history WHERE medicineId = :medicineId")
    suspend fun deleteHistoryByMedicine(medicineId: Int)

    @Query("DELETE FROM dose_history")
    suspend fun deleteAllHistory()
    @Query("SELECT COUNT (*) FROM DOSE_HISTORY WHERE medicineId=:medicineId")
    fun getDoseCount(medicineId: Int): LiveData<Int>
    @Query("DELETE FROM dose_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Int)

}