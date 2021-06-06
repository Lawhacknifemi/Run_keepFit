package com.example.runandkeepfit.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runandkeepfit.R
import com.example.runandkeepfit.databinding.FragmentStatisticsBinding
import com.example.runandkeepfit.databinding.FragmentTrackingBinding
import com.example.runandkeepfit.others.CustomMarkerView
import com.example.runandkeepfit.others.TrackingUtility
import com.example.runandkeepfit.ui.viewmodels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment :Fragment(R.layout.fragment_statistics) {
    private val viewModel: StatisticsViewModel by viewModels()

    private lateinit var binding: FragmentStatisticsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         binding = FragmentStatisticsBinding.bind(view)

        subScribeToObserver()
        setUpBarChart()

    }

    private fun setUpBarChart(){
        this.binding.run {
            barChart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawLabels(false)
                axisLineColor = Color.YELLOW
                textColor = Color.WHITE
                setDrawGridLines(false)

            }
            barChart.axisLeft.apply {
                axisLineColor = Color.YELLOW
                textColor = Color.RED
                setDrawGridLines(false)

            }
            barChart.axisRight.apply {
                axisLineColor = Color.YELLOW
                textColor = Color.RED
                setDrawGridLines(false)

            }
            barChart.apply {
                description.text = "Avg Speed Over Time"
                legend.isEnabled = false
            }
        }

    }
    private fun subScribeToObserver(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text = totalTimeRun

            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km = it/1000f
                val totalDistanceCovered = round(km *10f)/10f
                val totalDistanceString = "${totalDistanceCovered}Km"
                this.binding.tvTotalDistance.text = totalDistanceString
            }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed = round(it * 10f)/10f
                val avgSpeedString = "${avgSpeed}K/hr"
                this.binding.tvAverageSpeed.text = avgSpeedString
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalCalories = it
                val totalCaloriesString = "$totalCalories"
                this.binding.tvTotalCalories.text = totalCaloriesString
            }
        })

        viewModel.runSortedByDate.observe(viewLifecycleOwner, Observer {
            val allAVGSpeed = it.indices.map {
                i ->BarEntry(i.toFloat(), it[i].avgSpeedInKmH)
            }
            val barDataSet = BarDataSet(allAVGSpeed,"Avg Over Time").apply {
                valueTextColor = Color.RED
                color = ContextCompat.getColor(requireContext(),R.color.colorAccent)
            }
            this.binding.run {
                barChart.data = BarData(barDataSet)
                barChart.marker = CustomMarkerView(it.reversed(),requireContext(),R.layout.marker_view)
                barChart.invalidate()

            }



        })
    }

}