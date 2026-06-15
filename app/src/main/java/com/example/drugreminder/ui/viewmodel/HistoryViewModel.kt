package com.example.drugreminder.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drugreminder.data.database.MedicineDataBase
import com.example.drugreminder.data.model.DoseHistory
import com.example.drugreminder.data.repository.DoseHistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DoseHistoryRepository
    val allHistory: LiveData<List<DoseHistory>>

    init {
        val dao = MedicineDataBase.getDatabase(application).doseHistoryDao()
        repository = DoseHistoryRepository(dao)
        allHistory = repository.getAllHistory()
    }

    fun getHistoryByMedicine(medicineId: Int): LiveData<List<DoseHistory>> =
        repository.getHistoryByMedicine(medicineId)

    fun deleteHistoryByMedicine(medicineId: Int) {
        viewModelScope.launch {
            repository.deleteHistoryByMedicine(medicineId)
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            repository.deleteAllHistory()
        }
    }
    fun deleteHistoryById(id: Int) {
        viewModelScope.launch {
            repository.deleteHistoryById(id)
        }
    }
}