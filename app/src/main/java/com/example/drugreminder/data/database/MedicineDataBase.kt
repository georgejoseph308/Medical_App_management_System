package com.example.drugreminder.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.drugreminder.data.dao.MedicineDao
import com.example.drugreminder.data.dao.DoseHistoryDao
import com.example.drugreminder.data.dao.LabResultDao
import com.example.drugreminder.data.dao.MedicalAppointmentDao
import com.example.drugreminder.data.model.Medicine
import com.example.drugreminder.data.model.DoseHistory
import com.example.drugreminder.data.model.LabResult
import com.example.drugreminder.data.model.MedicalAppointment

@Database(
    entities = [
        Medicine::class,
        DoseHistory::class,
        LabResult::class,
        MedicalAppointment::class
               ],
    version = 5,
    exportSchema = false
)
abstract class MedicineDataBase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun doseHistoryDao(): DoseHistoryDao
    abstract fun labResultDao(): LabResultDao
    abstract fun MedicalAppointmentDao(): MedicalAppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: MedicineDataBase? = null

        fun getDatabase(context: Context): MedicineDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedicineDataBase::class.java,
                    "drug_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}