package com.example.drugreminder.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drugreminder.data.database.MedicineDataBase
import com.example.drugreminder.data.model.LabResult
import com.example.drugreminder.data.model.MedicalAppointment
import com.example.drugreminder.data.repository.LabResultRepository
import com.example.drugreminder.data.repository.MedicalAppointmentRepository
import kotlinx.coroutines.launch

class MedicalViewModel(application: Application): AndroidViewModel(application) {
    private val labResultRepository: LabResultRepository
    private val appointmentRepository: MedicalAppointmentRepository

    val allLabResults : LiveData<List<LabResult>>
    val allAppointments : LiveData<List<MedicalAppointment>>

    init {
        val db= MedicineDataBase.getDatabase(application)
        labResultRepository = LabResultRepository(db.labResultDao())
        appointmentRepository = MedicalAppointmentRepository(db.MedicalAppointmentDao())
        allLabResults = labResultRepository.getALlLabResults()
        allAppointments = appointmentRepository.getAllMedicalAppointments()

    }
    fun insertLabResult(labResult: LabResult){
        viewModelScope.launch { labResultRepository.insertLabResult(labResult) }
    }
    fun updateLabResult(labResult: LabResult){
        viewModelScope.launch { labResultRepository.updateLabResult(labResult) }
    }
    fun deleteLabResult(labResult: LabResult){
        viewModelScope.launch { labResultRepository.deleteLabResult(labResult) }
    }
    suspend fun getLabResultById(id :Int): LabResult?{
        return labResultRepository.getLabResultById(id)
    }

    //Appointments
    fun insertAppointment(appointment: MedicalAppointment){
        viewModelScope.launch { appointmentRepository.insertMedicalAppointment(appointment) }
    }
    fun updateAppointment(appointment: MedicalAppointment){
        viewModelScope.launch { appointmentRepository.updateMedicalAppointment(appointment) }
    }
    fun deleteAppointment(appointment: MedicalAppointment){
        viewModelScope.launch { appointmentRepository.deleteMedicalAppointment(appointment) }
    }
    suspend fun getAppointmentById(id:Int): MedicalAppointment?{
        return appointmentRepository.getMedicalAppointmentById(id)
    }

}