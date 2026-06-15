package com.example.drugreminder.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.NumberPicker
import com.drugreminder.R
import com.google.android.material.button.MaterialButton

class TimePickerDialog(
    context: Context,
    private val initialHour: Int,
    private val initialMinute: Int,
    private val onTimeSet:(hour:Int,minute: Int)-> Unit
): Dialog(context){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_time_picker)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val hourPicker=findViewById<NumberPicker>(R.id.hourPicker)
        val minutePicker=findViewById<NumberPicker>(R.id.minutePicker)
        val btnAM=findViewById< MaterialButton>(R.id.btnAM)
        val btnPM=findViewById<MaterialButton>(R.id.btnPM)
        val btnOk=findViewById<MaterialButton>(R.id.btnOk)
        val btnCancel=findViewById<MaterialButton>(R.id.btnCancel)
        var isAm=initialHour<12
        hourPicker.minValue=1
        hourPicker.maxValue=12
        hourPicker.value=when{
            initialHour==0->12
            initialHour >12 ->initialHour -12
            else->initialHour
        }
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = initialMinute
        minutePicker.setFormatter { String.format("%02d", it) }
        fun updateAMPM(){
            if (isAm){
                btnAM.setBackgroundColor(context.getColor(R.color.amber_600))
                btnAM.setTextColor(context.getColor(R.color.white))
                btnPM.setBackgroundColor(context.getColor(R.color.surface_light))
                btnPM.setTextColor(context.getColor(R.color.black))
            }else{
                btnPM.setBackgroundColor(context.getColor(R.color.purple_600))
                btnPM.setTextColor(context.getColor(R.color.white))
                btnAM.setBackgroundColor(context.getColor(R.color.surface_light))
                btnAM.setTextColor(context.getColor(R.color.black))
            }
        }
        updateAMPM()
        btnAM.setOnClickListener {
            isAm=true
            updateAMPM()
        }
        btnPM.setOnClickListener {
            isAm=false
            updateAMPM()
        }
        btnOk.setOnClickListener {
            var hour=hourPicker.value
            if (!isAm && hour!=12)hour += 12
            if (isAm &&hour==12)hour = 0
            onTimeSet(hour,minutePicker.value)
            dismiss()
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }
}
