package com.example.drugreminder.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drugreminder.databinding.ItemAppointmentBinding
import com.example.drugreminder.data.model.MedicalAppointment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppointmentAdapter(
    private val onEdit:(MedicalAppointment)-> Unit,
    private val onDelete:(MedicalAppointment) -> Unit
): ListAdapter<MedicalAppointment, AppointmentAdapter.AppointmentViewHolder>(DiffCallback()){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentViewHolder {
        val binding= ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AppointmentViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
    inner class AppointmentViewHolder(
        private val binding: ItemAppointmentBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(appointment: MedicalAppointment){
            binding.tvDoctorName.text=appointment.doctorName
            binding.tvSpecialty.text=appointment.specialty
            binding.tvAppointmentLocation.text=appointment.location
            binding.tvAppointmentDate.text= SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault()
            ).format(Date(appointment.date))
            binding.tvAppointmentTime.text=appointment.time
            binding.btnEditAppointment.setOnClickListener { onEdit(appointment) }
            binding.btnDeleteAppointment.setOnClickListener {
                androidx.appcompat.app.AlertDialog.Builder(binding.root.context)
                    .setTitle("حذف الموعد")
                    .setMessage("هل انت متاكد؟")
                    .setPositiveButton("حذف"){_,_->onDelete(appointment)}
                    .setNegativeButton("الغاء",null)
                    .show()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MedicalAppointment>() {
        override fun areItemsTheSame(oldItem: MedicalAppointment, newItem: MedicalAppointment) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MedicalAppointment, newItem: MedicalAppointment) =
            oldItem == newItem
    }
}
