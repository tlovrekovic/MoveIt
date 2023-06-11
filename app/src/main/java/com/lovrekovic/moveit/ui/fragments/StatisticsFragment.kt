package com.lovrekovic.moveit.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lovrekovic.moveit.R
import com.lovrekovic.moveit.ui.viewmodels.MainViewModel
import com.lovrekovic.moveit.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.fragment_statistics) {
    private val viewModel : StatisticsViewModel by viewModels()
}