package com.example.drugreminder.data.repository

import androidx.lifecycle.LiveData
import com.example.drugreminder.data.dao.LabResultDao
import com.example.drugreminder.data.model.LabResult

class LabResultRepository(private val dao: LabResultDao){

    fun getALlLabResults(): LiveData<List<LabResult>> = dao.getAllLabResults()

    suspend fun getLabResultById(id:Int): LabResult? = dao.getLabResultById(id)

    suspend fun insertLabResult(labResult: LabResult) = dao.insertLabResult(labResult)

    suspend fun updateLabResult(labResult: LabResult) = dao.updateLabResult(labResult)

    suspend fun deleteLabResult(labResult: LabResult) = dao.deleteLabResult(labResult)

}