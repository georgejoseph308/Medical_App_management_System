package com.example.drugreminder.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.drugreminder.databinding.FragmentAppointmentBinding
import com.example.drugreminder.activities.AddAppointmentActivity
import com.example.drugreminder.ui.adapter.AppointmentAdapter
import com.example.drugreminder.ui.viewmodel.MedicalViewModel

class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicalViewModel by viewModels()
    private lateinit var adapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeAppointments()
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter(
            onEdit = { appointment ->
                val intent = Intent(requireContext(), AddAppointmentActivity::class.java).apply {
                    putExtra("appointment_id", appointment.id)
                }
                startActivity(intent)
            },
            onDelete = { appointment ->
                viewModel.deleteAppointment(appointment)
            }
        )
        binding.recyclerAppointments.adapter = adapter
    }

    private fun observeAppointments() {
        viewModel.allAppointments.observe(viewLifecycleOwner) { appointments ->
            adapter.submitList(appointments)
            binding.layoutAppointmentEmpty.visibility =
                if (appointments.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerAppointments.visibility =
                if (appointments.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}