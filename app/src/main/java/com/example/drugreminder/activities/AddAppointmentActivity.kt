package com.example.drugreminder.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.drugreminder.R
import androidx.lifecycle.lifecycleScope
import com.drugreminder.databinding.AddAppointmentBinding
import com.example.drugreminder.data.model.MedicalAppointment
import com.example.drugreminder.ui.viewmodel.MedicalViewModel
import com.example.drugreminder.utils.AlarmScheduler
import com.example.drugreminder.utils.TimePickerDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddAppointmentActivity : AppCompatActivity() {

    private lateinit var binding: AddAppointmentBinding
    private val viewModel: MedicalViewModel by viewModels()

    private var dateMillis: Long = 0L
    private var editAppointmentId: Int = -1
    private var existingAppointment: MedicalAppointment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editAppointmentId = intent.getIntExtra("appointment_id", -1)

        setupToolbar()
        setupDatePicker()
        setupTimePicker()
        saveAppointment()

        if (editAppointmentId != -1) {
            loadAppointmentData(editAppointmentId)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAddAppointment)
        binding.toolbarAddAppointment.setNavigationOnClickListener { finish() }
        if (editAppointmentId != -1) {
            binding.toolbarAddAppointment.title = getString(R.string.update_appointment)
        }
    }

    private fun loadAppointmentData(id: Int) {
        lifecycleScope.launch {
            val appointment = viewModel.getAppointmentById(id) ?: return@launch
            existingAppointment = appointment
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            binding.etDoctorName.setText(appointment.doctorName)
            binding.etSpecialty.setText(appointment.specialty)
            binding.etLocation.setText(appointment.location)

            dateMillis = appointment.date
            binding.etDate.setText(dateFormatter.format(appointment.date))
            binding.etTime.setText(appointment.time)
            binding.etNotes.setText(appointment.notes)

            binding.btnSaveAppointment.text = getString(R.string.update_appointment)
        }
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            if (dateMillis != 0L) cal.timeInMillis = dateMillis
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                dateMillis = cal.timeInMillis
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etDate.setText(formatter.format(cal.time))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupTimePicker() {
        binding.etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                context = this,
                initialHour = calendar.get(Calendar.HOUR_OF_DAY),
                initialMinute = calendar.get(Calendar.MINUTE),
                onTimeSet = { hour, minute ->
                    binding.etTime.setText(String.format("%02d:%02d", hour, minute))
                }
            ).show()
        }
    }

    private fun saveAppointment() {
        binding.btnSaveAppointment.setOnClickListener {
            val doctorName = binding.etDoctorName.text.toString().trim()
            val specialty = binding.etSpecialty.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val time = binding.etTime.text.toString().trim()
            val notes = binding.etNotes.text.toString().trim()

            if (doctorName.isEmpty() || specialty.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_required_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dateMillis == 0L) {
                Toast.makeText(this, getString(R.string.select_date), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (time.isEmpty()) {
                Toast.makeText(this, getString(R.string.select_appointment_time), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val appointment = MedicalAppointment(
                id = if (editAppointmentId != -1) editAppointmentId else 0,
                doctorName = doctorName,
                specialty = specialty,
                location = location,
                date = dateMillis,
                time = time,
                notes = notes
            )

            if (editAppointmentId != -1) {
                AlarmScheduler.cancelAppointmentAlarm(this, existingAppointment!!)
                viewModel.updateAppointment(appointment)
            } else {
                viewModel.insertAppointment(appointment)
            }

            AlarmScheduler.scheduleAppointmentAlarm(this, appointment)
            Toast.makeText(this, getString(R.string.appointment_saved), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}