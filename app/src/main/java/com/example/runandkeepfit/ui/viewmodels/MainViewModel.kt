package com.example.runandkeepfit.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runandkeepfit.db.Run
import com.example.runandkeepfit.others.SortType
import com.example.runandkeepfit.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
        val mainRepository: MainRepository
) :ViewModel(){

        fun insertRun(run:Run) =viewModelScope.launch {
                mainRepository.insertRun(run)
        }
        private val runSortedByDate = mainRepository.getAllRunSortedByDate()
      private  val runsortedByDistance = mainRepository.getAllRunSortedByDistance()
        private val runSortedByCalorieBurned = mainRepository.getAllRunSortedByCaloriesBurned()
        private val runSortedByTimeInMillis = mainRepository.getAllRunSortedByTime()
        private val runSortedByAvgSpeed = mainRepository.getAllRunSortedByAvgSpeed()

        val runs = MediatorLiveData<List<Run>>()
        var sortType = SortType.DATE

        init {
            runs.addSource(runSortedByDate){result ->
                    if (sortType == SortType.DATE){
                            result?.let { runs.value = it }
                    }
            }
                runs.addSource(runSortedByAvgSpeed){result ->
                        if (sortType == SortType.AVG_SPEED){
                                result?.let { runs.value = it }
                        }
                }
                runs.addSource(runsortedByDistance){result ->
                        if (sortType == SortType.DISTANCE){
                                result?.let { runs.value = it }
                        }
                }
                runs.addSource(runSortedByTimeInMillis){result ->
                        if (sortType == SortType.RUNNING_TIME){
                                result?.let { runs.value = it }
                        }
                }
                runs.addSource(runSortedByCalorieBurned){result ->
                        if (sortType == SortType.CALORIES_BURNED){
                                result?.let { runs.value = it }
                        }
                }
        }

        fun sortRuns(sortType: SortType) = when(sortType){
                SortType.DATE -> runSortedByDate.value?.let { runs.value = it }
                SortType.RUNNING_TIME -> runSortedByTimeInMillis.value?.let { runs.value = it }
                SortType.CALORIES_BURNED -> runSortedByCalorieBurned.value?.let { runs.value = it }
                SortType.DISTANCE -> runsortedByDistance.value?.let { runs.value = it }
                SortType.AVG_SPEED -> runSortedByAvgSpeed.value?.let { runs.value = it }
        }.also {
                this.sortType = sortType
        }

}
