package com.lovrekovic.moveit.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.lovrekovic.moveit.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel(){
}