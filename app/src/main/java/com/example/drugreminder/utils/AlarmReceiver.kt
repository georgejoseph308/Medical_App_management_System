package com.example.drugreminder.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.drugreminder.data.database.MedicineDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type") ?: "medicine"

        if (type == "appointment") {
            handleAppointmentAlarm(context, intent)
        } else {
            handleMedicineAlarm(context, intent)
        }
    }

    private fun handleMedicineAlarm(context: Context, intent: Intent) {
        val medicineId = intent.getIntExtra("medicine_id", 0)
        val medicineName = intent.getStringExtra("medicine_name") ?: "Medicine"
        val dosage = intent.getStringExtra("medicine_dosage") ?: ""
        val isBefore = intent.getBooleanExtra("is_before", false)
        val day = intent.getStringExtra("day")
        val requestCode = intent.getIntExtra("request_code", 0)

        if (isBefore) {
            NotificationHelper.showNotification(
                context = context,
                medicineId = medicineId,
                title = "⏰ بعد 10 دقايق!",
                message = "استعد لجرعة $medicineName - $dosage"
            )
        } else {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val db = MedicineDataBase.getDatabase(context)
                val medicine = db.medicineDao().getMedicineById(medicineId)
                val takenDays = medicine?.takenDays?.split(",") ?: emptyList()
                val alreadyTaken = takenDays.contains(day)
                if (!alreadyTaken) {
                    NotificationHelper.showNotification(
                        context = context,
                        medicineId = medicineId,
                        title = "💊 وقت الدواء!",
                        message = "$medicineName - $dosage"
                    )
                }
            }
        }
        scheduleNextWeek(context, intent, requestCode)
    }

    private fun handleAppointmentAlarm(context: Context, intent: Intent) {
        val appointmentId = intent.getIntExtra("appointment_id", 0)
        val doctorName = intent.getStringExtra("doctor_name") ?: "Doctor"
        val specialty = intent.getStringExtra("specialty") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val isBefore = intent.getBooleanExtra("is_before", false)
        val requestCode = intent.getIntExtra("request_code", 0)

        val title = if (isBefore) "⏰ موعد بعد 30 دقيقة!" else "🏥 وقت الموعد!"
        val message = if (isBefore) {
            "متبقى 30 دقيقة لموعدك مع د. $doctorName ($specialty)"
        } else {
            "موعدك الآن مع د. $doctorName في $location"
        }

        NotificationHelper.showNotification(
            context = context,
            medicineId = appointmentId + 10000,
            title = title,
            message = message
        )
    }

    private fun scheduleNextWeek(context: Context, intent: Intent, requestCode: Int) {
        val nextWeekIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtras(intent)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            nextWeekIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextWeek = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextWeek,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                nextWeek,
                pendingIntent
            )
        }
    }
}