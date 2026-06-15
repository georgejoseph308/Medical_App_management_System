package com.example.drugreminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineName: String,
    val dosage: String,
    val time: String,
    val notes: String = "",
    val selectedDays: String = "",
    val takenDays: String="",
    val startDate: Long,
    val endDate: Long,
    val lastTakenDate: Long = 0L,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)