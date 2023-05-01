package com.example.symptomease

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(SavedSymptomEntity::class), version = 1)
abstract class SymptomRoomDatabase : RoomDatabase(){
    abstract fun savedSymptomsDAO() : SavedSymptomsDAO
}