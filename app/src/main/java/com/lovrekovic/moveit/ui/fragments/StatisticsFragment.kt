package com.lovrekovic.moveit.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.lovrekovic.moveit.R
import com.lovrekovic.moveit.other.TrackingUtility
import com.lovrekovic.moveit.ui.viewmodels.MainViewModel
import com.lovrekovic.moveit.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.fragment_statistics) {
    private val viewModel : StatisticsViewModel by viewModels()
    lateinit var tvTotalTime : TextView
    lateinit var tvTotalCalories : TextView
    lateinit var tvTotalDistance : TextView
    lateinit var tvAverageSpeed : TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvAverageSpeed = view.findViewById(R.id.tvAverageSpeed)
        tvTotalCalories = view.findViewById(R.id.tvTotalCalories)
        tvTotalDistance = view.findViewById(R.id.tvTotalDistance)
        tvTotalTime = view.findViewById(R.id.tvTotalTime)
        subscribeToObservers()
    }

    private fun subscribeToObservers(){
        viewModel.totalTimeRUn.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                tvTotalTime.text = totalTimeRun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let{
                val km = it/1000f
                val totalDistance = round(km * 10f)/10f
                val totalDistanceString = "˘${totalDistance}km"
                tvTotalDistance.text = totalDistanceString
            }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed = round(it * 10f) / 10f
                val avgSpeedString = "${avgSpeed} km/h"
                tvAverageSpeed.text = avgSpeedString
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let{
                val totalCalories = "${it}kcal"
                tvTotalCalories.text = totalCalories
            }
        })
    }
}