package com.example.drugreminder.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.drugreminder.data.model.Medicine

@Dao
interface MedicineDao {
    @Insert
    suspend fun insertMedicine(medicine: Medicine)
    @Update
    suspend fun updateMedicine(medicine: Medicine)
    @Delete
    suspend fun deleteMedicine(medicine: Medicine)
    @Query ("SELECT * FROM medicines")
    fun getAllMedicines(): LiveData<List<Medicine>>
    @Query("UPDATE medicines SET lastTakenDate = :date WHERE id = :id")
    suspend fun updateLastTakenDate(id: Int, date: Long)
    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: Int): Medicine?
    @Query("UPDATE medicines SET takenDays=:takenDays WHERE ID=:id")
    suspend fun updateTakenDays(id:Int,takenDays:String)
    @Query("UPDATE medicines SET takenDays = '' WHERE id= :id")
    suspend fun resetTakenDays(id: Int)
}