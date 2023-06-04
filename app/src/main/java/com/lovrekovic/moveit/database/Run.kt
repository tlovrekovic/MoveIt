package com.lovrekovic.moveit.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="run_table")
data class Run (
    var img: Bitmap?=null,
    //datum u milisekundama
    var timestamp:Long=0L,
    var avgSpeedKMH: Float=0f,
    var distanceInMeters: Int = 0,
    //duljina trajanja
    var timeInMillis: Long = 0L,
    var caloriesBurned: Int = 0
)

{
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}