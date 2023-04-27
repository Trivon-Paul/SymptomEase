package com.example.symptomease

import com.google.gson.annotations.SerializedName

data class Diagnostics(
    @SerializedName("Issue") val issue: Issue,
    @SerializedName("Specialisation") val specialisation:List<Specialist>)

data class Issue(val Name: String, val IcdName : String, val ProfName: String, val Accuracy: Double)


//data class Specialist(@SerializedName("Specialisation") val special : Specialisation)

data class Specialist(val Name: String, val ID: Int, val SpecialistID: Int)


