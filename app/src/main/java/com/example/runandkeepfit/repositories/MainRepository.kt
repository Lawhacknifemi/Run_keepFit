package com.example.runandkeepfit.repositories

import com.example.runandkeepfit.db.Run
import com.example.runandkeepfit.db.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(
        val runDao: RunDao
){

    suspend fun insertRun(run:Run) = runDao.insertRun(run)
    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)
    fun getAllRunSortedByDate() = runDao.getAllRunSortedByDate()
    fun getAllRunSortedByDistance()  = runDao.getAllRunSortedDistance()
    fun getAllRunSortedByTime() = runDao.getAllRunSortedByTimeInMilliS()
    fun getAllRunSortedByCaloriesBurned() = runDao.getAllRunSortedByCaloriesBurned()
    fun getAllRunSortedByAvgSpeed() = runDao.getAllRunSortedByAvgSpeed()

    fun getTotalTime() = runDao.getTotalTimeInMillis()
    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
    fun getTotalDistance() = runDao.getTotalDistance()
    fun getTotalSpeed() = runDao.getTotalSpeed()

}