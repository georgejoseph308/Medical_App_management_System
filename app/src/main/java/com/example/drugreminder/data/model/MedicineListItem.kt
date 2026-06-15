package com.example.drugreminder.data.model

sealed class MedicineListItem {
    data class Header(val dayArabic: String) : MedicineListItem()
    data class Item(val medicineDay: MedicineDay) : MedicineListItem()
}