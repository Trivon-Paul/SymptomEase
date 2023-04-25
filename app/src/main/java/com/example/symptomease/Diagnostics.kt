package com.example.symptomease

import com.google.gson.annotations.SerializedName

data class Diagnostics(@SerializedName("Issue") val issue: Issue)

data class Issue(val Name: String, val IcdName : String)

data class Specialist(@SerializedName("Specialisation") val special : Specialisation)

data class Specialisation(val Name: String)


