package com.example.drugreminder.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.drugreminder.databinding.FragmentLabResultBinding
import com.example.drugreminder.activities.AddLabResultActivity
import com.example.drugreminder.ui.adapter.LabResultAdapter
import com.example.drugreminder.ui.viewmodel.MedicalViewModel

class LabResultFragment : Fragment() {

    private var _binding: FragmentLabResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MedicalViewModel by activityViewModels()
    private lateinit var adapter: LabResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLabResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeLabResults()
        setupFab()
    }

    private fun setupRecyclerView() {
        adapter = LabResultAdapter(
            onEdit = { labResult ->
                val intent = Intent(requireContext(), AddLabResultActivity::class.java).apply {
                    putExtra("lab_id", labResult.id)
                }
                startActivity(intent)
            },
            onDelete = { labResult ->
                viewModel.deleteLabResult(labResult)
            }
        )
        binding.recyclerLabResults.adapter = adapter
    }

    private fun observeLabResults() {
        viewModel.allLabResults.observe(viewLifecycleOwner) { results ->
            adapter.submitList(results)
            binding.layoutLabEmpty.visibility =
                if (results.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerLabResults.visibility =
                if (results.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun setupFab() {
        binding.fabAddLabResult.setOnClickListener {
            startActivity(Intent(requireContext(), AddLabResultActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}