package com.example.drugreminder.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.drugreminder.data.database.MedicineDataBase
import com.example.drugreminder.data.model.DoseHistory
import com.example.drugreminder.data.model.Medicine
import com.example.drugreminder.data.model.MedicineDay
import com.example.drugreminder.data.model.MedicineListItem
import com.example.drugreminder.data.repository.DoseHistoryRepository
import com.example.drugreminder.data.repository.MedicineRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.collections.mutableMapOf

class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedicineRepository
    private val historyRepository: DoseHistoryRepository
    val allMedicine: LiveData<List<Medicine>>
    val allMedicineDays: LiveData<List<MedicineListItem>>

    init {
        val dao = MedicineDataBase.getDatabase(application).medicineDao()
        val historyDao= MedicineDataBase.getDatabase(application).doseHistoryDao()
        repository = MedicineRepository(dao)
        historyRepository= DoseHistoryRepository(historyDao)
        allMedicine = repository.getAllMedicine()
        allMedicineDays = allMedicine.map { medicines ->
            val dayOrder=listOf("SAT","SUN","MON","TUE","WED","THU","FRI")
            val grouped=mutableMapOf<String, MutableList<MedicineDay>>()
            medicines.forEach { medicine ->
                val days = medicine.selectedDays.split(",")
                val takenDays=medicine.takenDays.split(",")
                days.forEach { day ->
                    if (day.isNotEmpty()) {
                        grouped.getOrPut(day){mutableListOf()}.add(
                            MedicineDay(
                                medicine = medicine,
                                day = day,
                                dayArabic = dayToArabic(day),
                                isTaken = takenDays.contains(day)
                            )
                        )
                    }
                }
            }
            val result=mutableListOf<MedicineListItem>()
            dayOrder.forEach { day ->
                val items=grouped[day]
                if(!items.isNullOrEmpty()){
                    result.add(MedicineListItem.Header(dayToArabic(day)))
                    items.forEach { result.add(MedicineListItem.Item(it))}
                }
            }
            result
        }
    }

    fun insertMedicine(medicine: Medicine) {
        viewModelScope.launch { repository.insertMedicines(medicine) }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch { repository.deleteMedicines(medicine) }
    }

    fun updateMedicine(medicine: Medicine) {
        viewModelScope.launch { repository.updateMedicines(medicine) }
    }

    fun markAsTaken(medicine: Medicine,day: String) {
        viewModelScope.launch {
            val currentTakenDays=medicine.takenDays
                .split(",")
                .filter { it.isNotEmpty() }
                .toMutableList()
            if (!currentTakenDays.contains(day)){
                currentTakenDays.add(day)
                historyRepository.insertHistory(
                    DoseHistory(
                        medicineId = medicine.id,
                        medicineName = medicine.medicineName,
                        dosage = medicine.dosage,
                        day = day,
                        dayArabic = dayToArabic(day)
                    )
                )
            }
            repository.updateTakenDays(medicine.id,currentTakenDays.joinToString(","))
        }
    }

    suspend fun getMedicineById(id: Int): Medicine? {
        return repository.getMedicineById(id)
    }

    private fun dayToArabic(day: String): String {
        return when (day) {
            "SAT" -> "السبت"
            "SUN" -> "الأحد"
            "MON" -> "الاثنين"
            "TUE" -> "الثلاثاء"
            "WED" -> "الأربعاء"
            "THU" -> "الخميس"
            "FRI" -> "الجمعة"
            else -> ""
        }
    }

    private fun isTakenToday(medicine: Medicine): Boolean {
        if (medicine.lastTakenDate == 0L) return false
        val lastTaken = Calendar.getInstance().apply { timeInMillis = medicine.lastTakenDate }
        val today = Calendar.getInstance()
        return lastTaken.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                lastTaken.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    }
    fun markAsUnTaken(medicine: Medicine,day: String){
        viewModelScope.launch {
            val currentTakenDays=medicine.takenDays
                .split(",")
                .filter { it.isNotEmpty()&&it!=day }
            repository.updateTakenDays(medicine.id,currentTakenDays.joinToString( "," ))
        }
    }
}