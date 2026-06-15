package com.example.drugreminder.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.drugreminder.data.model.MedicineDay

class DiffCallback : DiffUtil.ItemCallback<MedicineDay>() {
    override fun areItemsTheSame(oldItem: MedicineDay, newItem: MedicineDay) =
        oldItem.medicine.id == newItem.medicine.id && oldItem.day == newItem.day

    override fun areContentsTheSame(oldItem: MedicineDay, newItem: MedicineDay) =
        oldItem == newItem
}