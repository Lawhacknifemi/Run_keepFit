package com.example.runandkeepfit.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.runandkeepfit.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
        val mainRepository: MainRepository
) :ViewModel(){

    val totalTimeRun = mainRepository.getTotalTime()
    val totalDistance = mainRepository.getTotalDistance()
    val totalCaloriesBurned = mainRepository.getTotalCaloriesBurned()
    val totalAvgSpeed = mainRepository.getTotalSpeed()


    val runSortedByDate = mainRepository.getAllRunSortedByDate()


}
