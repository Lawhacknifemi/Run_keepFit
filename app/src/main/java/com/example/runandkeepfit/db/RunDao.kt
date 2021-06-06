package com.example.runandkeepfit.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM run_table ORDER BY timeStamp DESC")
    fun getAllRunSortedByDate():LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY timeInMilli DESC")
    fun getAllRunSortedByTimeInMilliS():LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY avgSpeedInKmH DESC")
    fun getAllRunSortedByAvgSpeed():LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY caloriesBurned DESC")
    fun getAllRunSortedByCaloriesBurned():LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY distanceInMeters DESC")
    fun getAllRunSortedDistance():LiveData<List<Run>>



    @Query("SELECT SUM(timeInMilli) FROM run_table")
    fun getTotalTimeInMillis() :LiveData<Long>

    @Query("SELECT AVG(avgSpeedInKmH) FROM run_table")
    fun getTotalSpeed() :LiveData<Float>

    @Query("SELECT SUM(distanceInMeters) FROM run_table")
    fun getTotalDistance() :LiveData<Int>

    @Query("SELECT SUM(caloriesBurned) FROM run_table")
    fun getTotalCaloriesBurned() :LiveData<Int>




}