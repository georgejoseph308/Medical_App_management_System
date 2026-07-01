package com.example.drugreminder.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.drugreminder.data.model.Medicine
import com.example.drugreminder.data.model.MedicalAppointment
import java.util.Calendar

object AlarmScheduler {

    fun scheduleAlarm(context: Context, medicine: Medicine) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val timeParts = medicine.time.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        val selectedDays = medicine.selectedDays.split(",")

        selectedDays.forEachIndexed { index, day ->
            if (day.isEmpty()) return@forEachIndexed

            scheduleForDay(
                context = context,
                alarmManager = alarmManager,
                medicine = medicine,
                day = day,
                hour = hour,
                minute = minute,
                offsetMinutes = -10,
                requestCode = medicine.id * 100 + index,
                isBefore = true
            )

            // وقت الدواء بالظبط
            scheduleForDay(
                context = context,
                alarmManager = alarmManager,
                medicine = medicine,
                day = day,
                hour = hour,
                minute = minute,
                offsetMinutes = 0,
                requestCode = medicine.id * 100 + index + 50,
                isBefore = false
            )
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleForDay(
        context: Context,
        alarmManager: AlarmManager,
        medicine: Medicine,
        day: String,
        hour: Int,
        minute: Int,
        offsetMinutes: Int,
        requestCode: Int,
        isBefore: Boolean
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("medicine_id", medicine.id)
            putExtra("medicine_name", medicine.medicineName)
            putExtra("medicine_dosage", medicine.dosage)
            putExtra("is_before", isBefore)
            putExtra("day",day)
            putExtra("request_code", requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_WEEK, dayNameToCalendar(day))
            add(Calendar.MINUTE, offsetMinutes)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(context: Context, medicine: Medicine) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val selectedDays = medicine.selectedDays.split(",")

        selectedDays.forEachIndexed { index, day ->
            if (day.isEmpty()) return@forEachIndexed

            listOf(
                medicine.id * 100 + index,
                medicine.id * 100 + index + 50
            ).forEach { requestCode ->
                val intent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    private fun dayNameToCalendar(day: String): Int {
        return when (day) {
            "SAT" -> Calendar.SATURDAY
            "SUN" -> Calendar.SUNDAY
            "MON" -> Calendar.MONDAY
            "TUE" -> Calendar.TUESDAY
            "WED" -> Calendar.WEDNESDAY
            "THU" -> Calendar.THURSDAY
            "FRI" -> Calendar.FRIDAY
            else -> Calendar.SUNDAY
        }
    }

    fun scheduleAppointmentAlarm(context: Context, appointment: MedicalAppointment) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val timeParts = appointment.time.split(":")
        val hour = timeParts[0].toIntOrNull() ?: return
        val minute = timeParts[1].toIntOrNull() ?: return

        // 30 minutes before
        scheduleAppointment(
            context = context,
            alarmManager = alarmManager,
            appointment = appointment,
            hour = hour,
            minute = minute,
            offsetMinutes = -60,
            requestCode = appointment.id * 100,
            isBefore = true
        )

        // At appointment time
        scheduleAppointment(
            context = context,
            alarmManager = alarmManager,
            appointment = appointment,
            hour = hour,
            minute = minute,
            offsetMinutes = 0,
            requestCode = appointment.id * 100 + 50,
            isBefore = false
        )
    }

    fun cancelAppointmentAlarm(context: Context, appointment: MedicalAppointment) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        listOf(appointment.id * 100, appointment.id * 100 + 50).forEach { requestCode ->
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAppointment(
        context: Context,
        alarmManager: AlarmManager,
        appointment: MedicalAppointment,
        hour: Int,
        minute: Int,
        offsetMinutes: Int,
        requestCode: Int,
        isBefore: Boolean
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("type", "appointment")
            putExtra("appointment_id", appointment.id)
            putExtra("doctor_name", appointment.doctorName)
            putExtra("specialty", appointment.specialty)
            putExtra("location", appointment.location)
            putExtra("is_before", isBefore)
            putExtra("request_code", requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = appointment.date
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, offsetMinutes)

            if (timeInMillis <= System.currentTimeMillis()) {
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}