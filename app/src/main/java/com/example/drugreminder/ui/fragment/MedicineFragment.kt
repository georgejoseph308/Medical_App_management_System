package com.example.drugreminder.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.drugreminder.databinding.FragmentMedicineBinding
import com.example.drugreminder.activities.AddMedicineActivity
import com.example.drugreminder.ui.adapter.MedicineAdapter
import com.example.drugreminder.ui.viewmodel.MedicineViewModel

class MedicineFragment : Fragment() {

    private var _binding: FragmentMedicineBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicineViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeMedicines()
        setupFab()
    }

    private fun setupRecyclerView() {
        val adapter = MedicineAdapter(
            onTaken = { medicineDay ->
                viewModel.markAsTaken(medicineDay.medicine, medicineDay.day)
            },
            onUntaken = { medicineDay ->
                viewModel.markAsUnTaken(medicineDay.medicine, medicineDay.day)
            },
            onEdit = { medicineDay ->
                val intent = Intent(requireContext(), AddMedicineActivity::class.java).apply {
                    putExtra("medicine_id", medicineDay.medicine.id)
                }
                startActivity(intent)
            },
            onDelete = { medicineDay ->
                viewModel.deleteMedicine(medicineDay.medicine)
            }
        )
        binding.recyclerMedicines.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMedicines.adapter = adapter
    }

    private fun observeMedicines() {
        viewModel.allMedicineDays.observe(viewLifecycleOwner) { medicineDays ->
            (binding.recyclerMedicines.adapter as? MedicineAdapter)?.submitList(medicineDays)
        }
    }

    private fun setupFab() {
        binding.fabAddMedicine.setOnClickListener {
            startActivity(Intent(requireContext(), AddMedicineActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}