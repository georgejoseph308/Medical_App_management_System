package com.example.drugreminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dose_history")
data class DoseHistory(
    @PrimaryKey (autoGenerate = true)
    val id:Int=0,
    val medicineId: Int,
    val medicineName: String,
    val dosage:String,
    val day: String,
    val dayArabic: String,
    val takenAt: Long = System.currentTimeMillis()
)
