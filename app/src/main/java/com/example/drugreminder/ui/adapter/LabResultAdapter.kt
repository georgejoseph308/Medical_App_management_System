package com.example.drugreminder.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drugreminder.databinding.ItemLabResultBinding
import com.example.drugreminder.data.model.LabResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LabResultAdapter(
    private val onEdit: (LabResult) -> Unit,
    private val onDelete : (LabResult)-> Unit
): ListAdapter<LabResult, LabResultAdapter.LabResultViewHolder>(DiffCallback()){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LabResultViewHolder {
        val binding= ItemLabResultBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return LabResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class LabResultViewHolder(
        private val binding: ItemLabResultBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(labResult: LabResult){
            binding.tvTestName.text=labResult.testName
            binding.tvTestValue.text=labResult.value
            binding.tvTestUnit.text=labResult.unit
            binding.tvLabDate.text= SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(Date(labResult.date))

            if (labResult.notes.isNotEmpty()){
                binding.tvLabNotes.visibility=android.view.View.VISIBLE
                binding.tvLabNotes.text=labResult.notes
            } else {
                binding.tvLabNotes.visibility=android.view.View.GONE
            }
            binding.btnEditLab.setOnClickListener { onEdit(labResult) }
            binding.btnDeleteLab.setOnClickListener {
                androidx.appcompat.app.AlertDialog.Builder(binding.root.context)
                    .setTitle("حذف التحليل")
                    .setMessage("هل انت متاكد")
                    .setPositiveButton("حذف"){_,_->onDelete(labResult)}
                    .setNegativeButton("لا",null)
                    .show()
            }
        }
    }
    class DiffCallback : DiffUtil.ItemCallback<LabResult>() {
        override fun areItemsTheSame(oldItem: LabResult, newItem: LabResult) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: LabResult, newItem: LabResult) =
            oldItem == newItem
    }
}