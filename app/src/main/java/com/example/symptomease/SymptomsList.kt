package com.example.symptomease

import com.google.gson.annotations.SerializedName

data class SymptomsList(@SerializedName("Name") val symptomName :String,
                        @SerializedName("ID") val symptomID :Int)

//Addition to data class for condition and specialist recommendation?

