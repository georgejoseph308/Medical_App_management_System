package com.example.drugreminder.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.drugreminder.data.model.MedicalAppointment

@Dao
interface MedicalAppointmentDao {
    @Insert
    suspend fun insertAppointment (appointment: MedicalAppointment)

    @Update
    suspend fun updateAppointment(appointment: MedicalAppointment)

    @Delete
    suspend fun deleteAppointment(appointment: MedicalAppointment)

    @Query("SELECT * FROM MEDICAL_APPOINTMENTS ORDER BY DATE ASC")
    fun getAllAppointments(): LiveData<List<MedicalAppointment>>

    @Query("SELECT * FROM MEDICAL_APPOINTMENTS WHERE id= :id")
    suspend fun getAppointmentById(id : Int): MedicalAppointment?

}