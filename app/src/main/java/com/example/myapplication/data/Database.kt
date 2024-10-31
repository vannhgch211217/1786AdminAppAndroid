package com.example.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Course::class, Class::class], version = 16)
abstract class AppDatabase : RoomDatabase() {
    abstract fun CourseDao(): CourseDao
    abstract fun ClassDao(): ClassDao
}
