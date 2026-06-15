package com.example.drugreminder.data.model

data class MedicineDay(
    val medicine: Medicine,
    val day: String,
    val dayArabic: String,
    val isTaken: Boolean
)
