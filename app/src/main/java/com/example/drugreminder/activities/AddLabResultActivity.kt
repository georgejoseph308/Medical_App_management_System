package com.example.drugreminder.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.drugreminder.R
import com.drugreminder.databinding.ActivityAddLabResultBinding
import com.example.drugreminder.data.model.LabResult
import com.example.drugreminder.ui.viewmodel.MedicalViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddLabResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddLabResultBinding
    private val viewModel: MedicalViewModel by viewModels()
    private var selectedDate: Long = System.currentTimeMillis()
    private var labId: Int = -1
    private var existingLabResult: LabResult? = null
    private var selectedPdfUri: Uri? = null

    private val pdfPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val data = result.data!!.data
            if (data != null) {
                selectedPdfUri = data
                binding.tvPdfFileName.text = getFileName(selectedPdfUri!!)
                binding.tvPdfFileName.visibility = View.VISIBLE
                binding.btnAttachPdf.text = getString(R.string.pdf_attached, getFileName(selectedPdfUri!!))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLabResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        labId = intent.getIntExtra("lab_id", -1)

        setupToolbar()
        setupDatePicker()
        updateDateText()
        setupPdfPicker()

        if (labId != -1) {
            loadExistingLabResult()
        }
        binding.btnSaveLab.setOnClickListener { saveLabResult() }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAddLab)
        binding.toolbarAddLab.setNavigationOnClickListener { finish() }
        if (labId != -1) {
            binding.toolbarAddLab.title = getString(R.string.update_appointment)
            binding.btnSaveLab.text = getString(R.string.update_appointment)
        }
    }

    private fun loadExistingLabResult() {
        lifecycleScope.launch {
            existingLabResult = viewModel.getLabResultById(labId)
            existingLabResult?.let {
                binding.etTestName.setText(it.testName)
                binding.etTestValue.setText(it.value)
                binding.etTestUnit.setText(it.unit)
                binding.etTestNotes.setText(it.notes)
                selectedDate = it.date
                updateDateText()
                if (it.pdfUri.isNotEmpty()) {
                    selectedPdfUri = Uri.parse(it.pdfUri)
                    binding.tvPdfFileName.text = getFileName(selectedPdfUri!!)
                    binding.tvPdfFileName.visibility = View.VISIBLE
                    binding.btnAttachPdf.text = getString(R.string.pdf_attached, getFileName(selectedPdfUri!!))
                }
            }
        }
    }

    private fun setupDatePicker() {
        binding.etTestDate.setOnClickListener {
            val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedDate = calendar.timeInMillis
                    updateDateText()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateText() {
        binding.etTestDate.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
        )
    }

    private fun setupPdfPicker() {
        binding.btnAttachPdf.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            pdfPicker.launch(intent)
        }
    }

    private fun getFileName(uri: Uri): String {
        return try {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                    if (nameIndex >= 0) it.getString(nameIndex) else "PDF File"
                } else "PDF File"
            } ?: "PDF File"
        } catch (e: Exception) {
            "PDF File"
        }
    }

    private fun saveLabResult() {
        val testName = binding.etTestName.text.toString().trim()
        val value = binding.etTestValue.text.toString().trim()
        val unit = binding.etTestUnit.text.toString().trim()
        val notes = binding.etTestNotes.text.toString().trim()

        if (testName.isEmpty() || value.isEmpty()) {
            binding.etTestName.error = if (testName.isEmpty()) "مطلوب" else null
            binding.etTestValue.error = if (value.isEmpty()) "مطلوب" else null
            return
        }

        val pdfUriString = selectedPdfUri?.toString() ?: ""

        if (labId != -1 && existingLabResult != null) {
            val updated = existingLabResult!!.copy(
                testName = testName,
                value = value,
                unit = unit,
                date = selectedDate,
                notes = notes,
                pdfUri = pdfUriString
            )
            viewModel.updateLabResult(updated)
        } else {
            val labResult = LabResult(
                testName = testName,
                value = value,
                unit = unit,
                date = selectedDate,
                notes = notes,
                pdfUri = pdfUriString
            )
            viewModel.insertLabResult(labResult)
        }
        finish()
    }
}
