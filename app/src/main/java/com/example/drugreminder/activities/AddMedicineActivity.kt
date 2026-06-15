package com.example.drugreminder.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.drugreminder.databinding.AddMedicineBinding
import com.example.drugreminder.data.model.Medicine
import com.example.drugreminder.ui.viewmodel.MedicineViewModel
import com.example.drugreminder.utils.AlarmScheduler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.drugreminder.utils.TimePickerDialog

class AddMedicineActivity : AppCompatActivity() {

    private lateinit var binding: AddMedicineBinding
    private val viewModel: MedicineViewModel by viewModels()

    private var startDateMillis: Long = 0L
    private var endDateMillis: Long = 0L
    private val selectedDays = mutableSetOf<String>()
    private var editMedicineId: Int = -1
    private var existingMedicine: Medicine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddMedicineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editMedicineId = intent.getIntExtra("medicine_id", -1)
        if (editMedicineId != -1) {
            loadMedicineData(editMedicineId)
        }

        setupTimePicker()
        setupDatePickers()
        setupDayToggles()
        saveMedicine()
    }

    private fun loadMedicineData(id: Int) {
        lifecycleScope.launch {
            val medicine = viewModel.getMedicineById(id) ?: return@launch
            existingMedicine = medicine
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            binding.etMedicineName.setText(medicine.medicineName)
            binding.etDosage.setText(medicine.dosage)
            binding.etTime.setText(medicine.time)
            binding.etNotes.setText(medicine.notes)

            startDateMillis = medicine.startDate
            endDateMillis = medicine.endDate
            binding.etStartDate.setText(formatter.format(medicine.startDate))
            binding.etEndDate.setText(formatter.format(medicine.endDate))

            selectedDays.clear()
            val days = medicine.selectedDays.split(",")
            days.forEach { day ->
                if (day.isNotEmpty()) {
                    selectedDays.add(day)
                    when (day) {
                        "SAT" -> binding.btnSat.isChecked = true
                        "SUN" -> binding.btnSun.isChecked = true
                        "MON" -> binding.btnMon.isChecked = true
                        "TUE" -> binding.btnTue.isChecked = true
                        "WED" -> binding.btnWed.isChecked = true
                        "THU" -> binding.btnThu.isChecked = true
                        "FRI" -> binding.btnFri.isChecked = true
                    }
                }
            }

            binding.btnSaveMedicine.text = "Update Medicine"
        }
    }

    private fun setupTimePicker() {
        binding.etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                context=this,
                initialHour=calendar.get(Calendar.HOUR_OF_DAY),
                initialMinute=calendar.get(Calendar.MINUTE),
                onTimeSet={hour,minute ->
                    binding.etTime.setText(String.format("%02d:%02d",hour,minute))
                }
            ).show()
        }
    }

    private fun setupDatePickers() {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        binding.etStartDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                startDateMillis = cal.timeInMillis
                binding.etStartDate.setText(formatter.format(cal.time))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.etEndDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                endDateMillis = cal.timeInMillis
                binding.etEndDate.setText(formatter.format(cal.time))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupDayToggles() {
        val dayButtons = mapOf(
            binding.btnSat to "SAT",
            binding.btnSun to "SUN",
            binding.btnMon to "MON",
            binding.btnTue to "TUE",
            binding.btnWed to "WED",
            binding.btnThu to "THU",
            binding.btnFri to "FRI"
        )

        dayButtons.forEach { (button, day) ->
            button.setOnClickListener {
                if (selectedDays.contains(day)) {
                    selectedDays.remove(day)
                } else {
                    selectedDays.add(day)
                }
            }
        }
    }

    private fun saveMedicine() {
        binding.btnSaveMedicine.setOnClickListener {

            val name = binding.etMedicineName.text.toString().trim()
            val dosage = binding.etDosage.text.toString().trim()
            val time = binding.etTime.text.toString().trim()
            val notes = binding.etNotes.text.toString().trim()

            if (name.isEmpty() || dosage.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedDays.isEmpty()) {
                Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (startDateMillis == 0L || endDateMillis == 0L) {
                Toast.makeText(this, "Please select start and end date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (endDateMillis < startDateMillis) {
                Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val medicine = Medicine(
                id = if (editMedicineId != -1) editMedicineId else 0,
                medicineName = name,
                dosage = dosage,
                time = time,
                notes = notes,
                selectedDays = selectedDays.joinToString(","),
                takenDays = existingMedicine?.takenDays ?: "",
                startDate = startDateMillis,
                endDate = endDateMillis
            )

            if (editMedicineId != -1) {
                AlarmScheduler.cancelAlarm(this, medicine)
                viewModel.updateMedicine(medicine)
            } else {
                viewModel.insertMedicine(medicine)
            }

            AlarmScheduler.scheduleAlarm(this, medicine)
            Toast.makeText(this, "Medicine Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}