package com.example.drugreminder.data.repository

import androidx.lifecycle.LiveData
import com.example.drugreminder.data.dao.DoseHistoryDao
import com.example.drugreminder.data.model.DoseHistory

class DoseHistoryRepository(private val dao: DoseHistoryDao) {

    fun getAllHistory(): LiveData<List<DoseHistory>> = dao.getAllHistory()

    fun getHistoryByMedicine(medicineId: Int): LiveData<List<DoseHistory>> =
        dao.getHistoryByMedicine(medicineId)

    suspend fun insertHistory(doseHistory: DoseHistory) {
        dao.insertHistory(doseHistory)
    }

    suspend fun deleteHistoryByMedicine(medicineId: Int) {
        dao.deleteHistoryByMedicine(medicineId)
    }

    suspend fun deleteAllHistory() {
        dao.deleteAllHistory()
    }
    suspend fun deleteHistoryById(id: Int) {
        dao.deleteHistoryById(id)
    }
}