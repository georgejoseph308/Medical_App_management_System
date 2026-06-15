package com.example.drugreminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_appointments")
data class MedicalAppointment(
    @PrimaryKey (autoGenerate = true)
    val id:Int=0,
    val doctorName: String,
    val specialty: String,
    val location: String,
    val date: Long,
    val time: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
