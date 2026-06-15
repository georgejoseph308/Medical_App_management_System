package com.example.drugreminder.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.drugreminder.databinding.ActivityHistoryBinding
import com.example.drugreminder.ui.adapter.HistoryAdapter
import com.example.drugreminder.ui.viewmodel.HistoryViewModel

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeHistory()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarHistory)
        binding.toolbarHistory.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(
            onDelete = {history ->
                viewModel.deleteHistoryById(history.id)
            }
        )
        binding.recyclerHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerHistory.adapter = adapter
    }

    private fun observeHistory() {
        viewModel.allHistory.observe(this) { historyList ->
            adapter.submitList(historyList)
            binding.layoutHistoryEmpty.visibility =
                if (historyList.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerHistory.visibility =
                if (historyList.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}