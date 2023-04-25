package com.example.symptomease

import com.google.gson.annotations.SerializedName

//data class SymptomsList(val listOfSymptoms: List<Symptoms>)

data class SymptomsList(@SerializedName("Name") val symptomName :String)
