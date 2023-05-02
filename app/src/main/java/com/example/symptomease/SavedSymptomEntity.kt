package com.example.symptomease

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symptom_table")
data class SavedSymptomEntity(

    @PrimaryKey(autoGenerate = true)
    val id : Int,

    @ColumnInfo(name = "symptom_name")
    var symptomName  : String,

    @ColumnInfo(name = "symptom_description")
    var symptomDescription : String
)
