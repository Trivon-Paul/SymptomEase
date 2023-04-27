package com.example.symptomease

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DiagnosticsService {
    @GET("/diagnosis")
    fun getDiagnosis(@Query("token") token : String, @Query("language") language : String,
                     @Query("symptoms", encoded = true) symptomsID : String, @Query("Gender") gender : String,
                     @Query("year_of_birth") year_of_birth : Int) : Call<List<Diagnostics>>
}