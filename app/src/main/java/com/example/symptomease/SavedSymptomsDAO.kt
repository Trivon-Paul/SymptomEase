package com.example.symptomease

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SavedSymptomsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSavedSymptom(saved : SavedSymptomEntity)

    @Update
    fun updateSavedSymptom(saved : SavedSymptomEntity)

    @Query("SELECT * FROM symptom_table")
    fun getSavedSymptoms(): List<SavedSymptomEntity>
}