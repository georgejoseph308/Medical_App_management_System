package com.example.drugreminder.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drugreminder.databinding.ItemDayHeaderBinding
import com.drugreminder.databinding.ItemMedicineBinding
import com.example.drugreminder.data.model.MedicineDay
import com.example.drugreminder.data.model.MedicineListItem

class MedicineAdapter(
    private val onTaken: (MedicineDay) -> Unit,
    private val onUntaken: (MedicineDay) -> Unit,
    private val onEdit: (MedicineDay) -> Unit,
    private val onDelete: (MedicineDay) -> Unit
) : ListAdapter<MedicineListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MedicineListItem.Header -> TYPE_HEADER
            is MedicineListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemDayHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemMedicineBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MedicineViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MedicineListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is MedicineListItem.Item -> (holder as MedicineViewHolder).bind(item.medicineDay)
        }
    }

    class HeaderViewHolder(
        private val binding: ItemDayHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: MedicineListItem.Header) {
            binding.tvDayHeader.text = header.dayArabic
        }
    }

    inner class MedicineViewHolder(
        private val binding: ItemMedicineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(medicineDay: MedicineDay) {
            val medicine = medicineDay.medicine

            binding.tvMedicineName.text = medicine.medicineName
            binding.tvDosage.text = medicine.dosage
            binding.tvTime.text = medicine.time

            if (medicine.notes.isNotEmpty()) {
                binding.tvNotes.visibility = View.VISIBLE
                binding.tvNotes.text = medicine.notes
            } else {
                binding.tvNotes.visibility = View.GONE
            }

            binding.cbTaken.setOnCheckedChangeListener(null)
            binding.cbTaken.isChecked = medicineDay.isTaken
            binding.cbTaken.isEnabled = true

            if (medicineDay.isTaken) {
                binding.tvMedicineName.paintFlags =
                    binding.tvMedicineName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.tvMedicineName.paintFlags =
                    binding.tvMedicineName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            binding.cbTaken.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onTaken(medicineDay)
                } else {
                    androidx.appcompat.app.AlertDialog.Builder(binding.root.context)
                        .setTitle("إلغاء الجرعه ")
                        .setMessage("هل انت متاكد انك عايز تشيل الجرعه")
                        .setPositiveButton("اه") { _, _ ->
                            onUntaken(medicineDay)
                        }
                        .setNegativeButton("لا") { _, _ ->
                            binding.cbTaken.setOnCheckedChangeListener(null)
                            binding.cbTaken.isChecked = true
                            binding.cbTaken.setOnCheckedChangeListener { _, isChecked2 ->
                                if (isChecked2) onTaken(medicineDay) else onUntaken(medicineDay)
                            }
                        }.show()
                }
            }
            binding.btnEdit.setOnClickListener { onEdit(medicineDay) }
            binding.btnDelete.setOnClickListener { onDelete(medicineDay) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MedicineListItem>() {
        override fun areItemsTheSame(oldItem: MedicineListItem, newItem: MedicineListItem): Boolean {
            return when {
                oldItem is MedicineListItem.Header && newItem is MedicineListItem.Header ->
                    oldItem.dayArabic == newItem.dayArabic
                oldItem is MedicineListItem.Item && newItem is MedicineListItem.Item ->
                    oldItem.medicineDay.medicine.id == newItem.medicineDay.medicine.id &&
                            oldItem.medicineDay.day == newItem.medicineDay.day
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: MedicineListItem, newItem: MedicineListItem) =
            oldItem == newItem
    }
}