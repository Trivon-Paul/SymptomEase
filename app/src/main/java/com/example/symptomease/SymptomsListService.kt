package com.example.symptomease
import retrofit2.Call
import retrofit2.http.*

interface SymptomsListService {
    @GET("/symptoms")
    fun getAllSymptoms(@Query("token") token : String, @Query("language") language : String) : Call<List<SymptomsList>>

    //should I make a conditionListService and SpecialistRecommendationListService?


}