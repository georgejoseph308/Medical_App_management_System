package com.example.drugreminder.data.repository

import androidx.lifecycle.LiveData
import com.example.drugreminder.data.dao.MedicalAppointmentDao
import com.example.drugreminder.data.model.MedicalAppointment

class MedicalAppointmentRepository(private val dao: MedicalAppointmentDao) {

    fun getAllMedicalAppointments(): LiveData<List<MedicalAppointment>> = dao.getAllAppointments()

    suspend fun getMedicalAppointmentById(id:Int) = dao.getAppointmentById(id)

    suspend fun insertMedicalAppointment(appointment: MedicalAppointment) = dao.insertAppointment(appointment)

    suspend fun updateMedicalAppointment(appointment: MedicalAppointment) = dao.updateAppointment(appointment)

    suspend fun deleteMedicalAppointment(appointment: MedicalAppointment) = dao.deleteAppointment(appointment)


}