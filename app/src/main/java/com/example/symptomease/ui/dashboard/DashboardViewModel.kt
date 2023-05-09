package com.example.symptomease.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    /**
     * 3 steps to implement a ViewModel:
    1. Separate your data from your UI
    controller by creating a class that
    extends ViewModel
    2. Set up communications between
    your ViewModel and your UI
    controller
    3. Use your ViewModel in your UI
    controller
     */

    /* ??????????????
        fun setPosition(positionPassed: Int) {
        position.value = positionPassed
    }

    fun getPosition(): Int? {
        return position.value
    }

     */

    private val _text = MutableLiveData<String>().apply {
        
    }
    val text: LiveData<String> = _text

    val spinnerData = MutableLiveData<List<String>>()

    fun sendData(list : List<String>){
        spinnerData.value = list
    }
}