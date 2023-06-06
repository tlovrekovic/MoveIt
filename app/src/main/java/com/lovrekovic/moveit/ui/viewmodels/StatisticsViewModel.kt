package com.lovrekovic.moveit.ui.viewmodels

import com.lovrekovic.moveit.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
) {
}