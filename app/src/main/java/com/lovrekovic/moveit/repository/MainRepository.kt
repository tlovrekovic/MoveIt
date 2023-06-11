package com.lovrekovic.moveit.repository

import com.lovrekovic.moveit.database.Run
import com.lovrekovic.moveit.database.RunDAO
import javax.inject.Inject
//collect data from data sources
class MainRepository @Inject constructor(
    val runDao: RunDAO
){
    fun insertRun(run: Run)= runDao.insertRun(run)

    fun deleteRun(run: Run)= runDao.deleteRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByDistance() = runDao.getallRunsSortedByDistance()

    fun getAllRunsSortedByTimeMillis() = runDao.getAllRunsSortedByTimeInMillis()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAverageSpeed()

    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getTotalAvgSpeed() = runDao.getTotalAverageSpeed()

    fun getTotalDistance() = runDao.getTotalDistance()

    fun getTotalCaloriesBurned()= runDao.getTotalCaloriesBurned()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()

}