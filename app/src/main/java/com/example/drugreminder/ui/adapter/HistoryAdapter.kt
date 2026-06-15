package com.example.drugreminder.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drugreminder.databinding.ItemHistoryBinding
import com.example.drugreminder.data.model.DoseHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val onDelete: (DoseHistory) -> Unit
) : ListAdapter<DoseHistory, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(history: DoseHistory) {
            binding.tvHistoryMedicineName.text = history.medicineName
            binding.tvHistoryDosage.text = history.dosage
            binding.tvHistoryDay.text = history.dayArabic

            val date = Date(history.takenAt)
            binding.tvHistoryDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
            binding.tvHistoryTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)

            binding.btnDeleteHistory.setOnClickListener {
                androidx.appcompat.app.AlertDialog.Builder(binding.root.context)
                    .setTitle("حذف السجل")
                    .setMessage("هل انت متأكد؟")
                    .setPositiveButton("حذف") { _, _ -> onDelete(history) }
                    .setNegativeButton("إلغاء", null)
                    .show()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DoseHistory>() {
        override fun areItemsTheSame(oldItem: DoseHistory, newItem: DoseHistory) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DoseHistory, newItem: DoseHistory) =
            oldItem == newItem
    }
}