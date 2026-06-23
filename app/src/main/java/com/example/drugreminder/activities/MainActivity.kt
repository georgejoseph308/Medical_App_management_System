package com.example.drugreminder.activities

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.drugreminder.R
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.drugreminder.databinding.ActivityMainBinding
import com.example.drugreminder.ui.fragment.AppointmentFragment
import com.example.drugreminder.ui.fragment.LabResultFragment
import com.example.drugreminder.ui.fragment.MedicineFragment
import com.example.drugreminder.utils.NotificationHelper
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NotificationHelper.createNotificationChannel(this)
        checkNotificationPermission()
        checkExactAlarmPermission()
        setupViewPager()
        setupBottomNavigation()
        setupFab()
    }

    private fun setupViewPager() {
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 2

        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu[position].isChecked = true
            }
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_medicines -> binding.viewPager.currentItem = 0
                R.id.nav_lab_results -> binding.viewPager.currentItem = 1
                R.id.nav_appointments -> binding.viewPager.currentItem = 2
            }
            true
        }
    }

    private fun setupFab() {
        binding.fabMain.setOnClickListener { showAddDialog() }
    }

    private fun showAddDialog() {
        val options = arrayOf(
            getString(R.string.fab_add_medicine),
            getString(R.string.fab_add_lab_result),
            getString(R.string.fab_add_appointment)
        )

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.fab_add))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(this, AddMedicineActivity::class.java))
                    1 -> startActivity(Intent(this, AddLabResultActivity::class.java))
                    2 -> startActivity(Intent(this, AddAppointmentActivity::class.java))
                }
            }
            .show()
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

    class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MedicineFragment()
                1 -> LabResultFragment()
                else -> AppointmentFragment()
            }
        }
    }
}