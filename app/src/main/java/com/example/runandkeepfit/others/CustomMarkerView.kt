package com.example.runandkeepfit.others

import android.content.Context
import android.widget.TextView
import com.example.runandkeepfit.R
import com.example.runandkeepfit.databinding.ActivityMainBinding
import com.example.runandkeepfit.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
        val runs:List<Run>,
        c:Context,
        layoutId:Int

) :MarkerView(c,layoutId){

    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f,-height.toFloat())

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null){
            return
        }
        val curRunId = e.x.toInt()
        val run = runs[curRunId]
        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val tvDate = findViewById<TextView>(R.id.tvDater)
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        tvDate.text = dateFormat.format(calendar.time)

        val tvAvgSpeed = findViewById<TextView>(R.id.tvAvgSpeedr)
        val avgSpeed = "${run.avgSpeedInKmH}Km/H"
        tvAvgSpeed.text = avgSpeed


        val tvDistance = findViewById<TextView>(R.id.tvDistancer)
        val distanceInKM = "${run.distanceInMeters/ 1000f}km"
        tvDistance.text = distanceInKM

        val tvDuration = findViewById<TextView>(R.id.tvTimer)
        tvDuration.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMilli)

        val tvCaloriesBurned = findViewById<TextView>(R.id.tvCaloriesBurnedr)
        val caloriesBurned = "${run.caloriesBurned}kcal"
        tvCaloriesBurned.text = caloriesBurned


    }
}