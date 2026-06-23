package com.example.drugreminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lab_results")
data class LabResult(
    @PrimaryKey (autoGenerate = true)
    val id:Int=0,
    val testName:String,
    val value:String,
    val unit: String,
    val date:Long,
    val notes:String="",
    val pdfUri:String="",
    val createdAt: Long= System.currentTimeMillis()

)
