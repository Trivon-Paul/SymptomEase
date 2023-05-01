package com.example.symptomease.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        
    }
    val text: LiveData<String> = _text

    val spinnerData = MutableLiveData<List<String>>()

    fun sendData(list : List<String>){
        spinnerData.value = list
    }
}