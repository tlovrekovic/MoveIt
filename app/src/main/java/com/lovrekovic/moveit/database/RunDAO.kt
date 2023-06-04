package com.lovrekovic.moveit.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RunDAO {
    //ako postoji novi run zamjeniti ce stari
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //izvrsavati ce se unutar korutine
    suspend fun insertTun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)
    @Query("SELECT * FROM run_table ORDER BY timestamp DESC ")
    fun getAllRunsSortedByDate() : LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY timeInMillis DESC ")
    fun getAllRunsSortedByTimeInMillis() : LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY caloriesBurned DESC ")
    fun getAllRunsSortedByCaloriesBurned() : LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY avgSpeedKMH DESC ")
    fun getAllRunsSortedByAverageSpeed() : LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY distanceInMeters DESC ")
    fun getallRunsSortedByDistance() : LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillis) FROM run_table")
    fun getTotalTimeInMillis():LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM run_table")
    fun getTotalCaloriesBurned():LiveData<Long>

    @Query("SELECT SUM(distanceInMeters) FROM run_table")
    fun getTotalDistance():LiveData<Int>

    @Query("SELECT AVG(avgSpeedKMH) FROM run_table")
    fun getTotalAverageSpeed():LiveData<Float>
}