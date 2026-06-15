package com.example.drugreminder.activities

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.drugreminder.databinding.ActivityMainBinding
import com.example.drugreminder.ui.adapter.MedicineAdapter
import com.example.drugreminder.ui.viewmodel.MedicineViewModel
import com.example.drugreminder.utils.NotificationHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MedicineAdapter
    private val viewModel: MedicineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        NotificationHelper.createNotificationChannel(this)
        checkNotificationPermission()
        checkExactAlarmPermission()
        setupRecyclerView()
        observeMedicines()
        setupFab()
    }

    private fun setupRecyclerView() {
        adapter = MedicineAdapter(
            onTaken = { medicineDay ->
                viewModel.markAsTaken(
                    medicineDay.medicine,
                    medicineDay.day
                )
            },
            onUntaken = { medicineDay ->
                viewModel.markAsUnTaken(
                    medicineDay.medicine,
                    medicineDay.day
                )
            },
            onEdit = { medicineDay ->
                val intent = Intent(this, AddMedicineActivity::class.java).apply {
                    putExtra("medicine_id", medicineDay.medicine.id)
                }
                startActivity(intent)
            },
            onDelete = { medicineDay -> viewModel.deleteMedicine(medicineDay.medicine) }
        )
        binding.recyclerMedicines.layoutManager = LinearLayoutManager(this)
        binding.recyclerMedicines.adapter = adapter
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }
    private fun observeMedicines() {
        viewModel.allMedicineDays.observe(this) { medicineDays ->
            adapter.submitList(medicineDays)
        }
    }

    private fun setupFab() {
        binding.btnAddMedicine.setOnClickListener {
            startActivity(Intent(this, AddMedicineActivity::class.java))
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }
    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also {
                    startActivity(it)
                }
            }
        }

    }
}