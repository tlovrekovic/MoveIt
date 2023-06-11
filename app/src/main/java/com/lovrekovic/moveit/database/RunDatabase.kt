package com.lovrekovic.moveit.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(
    entities=[Run::class],
    version=1,
    exportSchema = false
)
//govori roomdb kao bi mogao pronaci konverter
@TypeConverters(Converters::class)
abstract class RunDatabase:RoomDatabase() {
    abstract fun getRunDao(): RunDAO
}