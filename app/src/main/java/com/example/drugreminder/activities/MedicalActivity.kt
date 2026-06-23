package com.example.drugreminder.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.drugreminder.databinding.ActivityMedicalBinding
import com.example.drugreminder.ui.fragment.AppointmentFragment
import com.example.drugreminder.ui.fragment.LabResultFragment
import com.example.drugreminder.ui.viewmodel.MedicalViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MedicalActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMedicalBinding
    private val viewModel: MedicalViewModel by viewModels()

    override fun onCreate(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityMedicalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupTabs()
        setupFab()
    }
    private fun setupToolbar(){
        setSupportActionBar(binding.toolbarMedical)
        binding.toolbarMedical.setNavigationOnClickListener { finish() }
    }
    private fun setupTabs(){
        val adapter = object : FragmentStateAdapter (this){
            override fun getItemCount(): Int = 2
            override fun createFragment(position: Int): Fragment {
                return when (position){
                    0 -> LabResultFragment()
                    else -> AppointmentFragment()
                }
            }
        }
        binding.viewPager.adapter=adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab, position ->
            tab.text = when (position){
                0 -> "التحاليل"
                else -> "المواعيد"
            }
        }.attach()
    }
    private fun setupFab(){
        binding.fabAddMedical.setOnClickListener {
            when(binding.viewPager.currentItem){
                0 -> startActivity(Intent(this, AddLabResultActivity::class.java))
                1 -> startActivity(Intent(this, AddAppointmentActivity::class.java))
            }
        }
    }
}