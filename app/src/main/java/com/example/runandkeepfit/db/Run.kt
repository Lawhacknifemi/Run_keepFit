package com.example.runandkeepfit.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_table")
data class Run (
        var img: Bitmap? = null,
        var timeStamp :Long = 0L,
        var avgSpeedInKmH :Float = 0f,
        var distanceInMeters :Int = 0,
        var timeInMilli :Long = 0L,
        var caloriesBurned : Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id :Int? = null
}