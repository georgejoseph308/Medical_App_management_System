package com.example.drugreminder.data.repository

import com.example.drugreminder.data.dao.MedicineDao
import com.example.drugreminder.data.model.Medicine

class MedicineRepository(private val dao: MedicineDao) {
    fun getAllMedicine() = dao.getAllMedicines()

    suspend fun insertMedicines(medicine: Medicine) {
        dao.insertMedicine(medicine)
    }

    suspend fun deleteMedicines(medicine: Medicine) {
        dao.deleteMedicine(medicine)
    }

    suspend fun updateMedicines(medicine: Medicine) {
        dao.updateMedicine(medicine)
    }

    suspend fun updateLastTakenDate(id: Int, date: Long) {
        dao.updateLastTakenDate(id, date)
    }

    suspend fun getMedicineById(id: Int): Medicine? {
        return dao.getMedicineById(id)
    }
    suspend fun updateTakenDays(id: Int, takenDays: String) {
        dao.updateTakenDays(id, takenDays)
    }
}