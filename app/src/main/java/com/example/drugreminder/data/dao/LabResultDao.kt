package com.example.drugreminder.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.drugreminder.data.model.LabResult

@Dao
interface LabResultDao {
    @Insert
    suspend fun insertLabResult(labResult: LabResult)

    @Update
    suspend fun updateLabResult(labResult: LabResult)

    @Delete
    suspend fun deleteLabResult(labResult: LabResult)

    @Query("SELECT * FROM lab_results ORDER BY DATE DESC")
    fun getAllLabResults(): LiveData<List<LabResult>>

    @Query("SELECT * FROM LAB_RESULTS WHERE id =:id")
    suspend fun getLabResultById(id:Int): LabResult?
}