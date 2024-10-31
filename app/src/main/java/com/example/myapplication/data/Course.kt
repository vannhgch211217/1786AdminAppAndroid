package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val cid: Int = 0,
    @ColumnInfo(name = "id") val id: Int?=null,
    @ColumnInfo(name = "day") val day: String,
    @ColumnInfo(name = "time") val time: String ,
    @ColumnInfo(name = "capacity") val capacity: String ,
    @ColumnInfo(name = "duration") val duration: String ,
    @ColumnInfo(name = "price") val price: String ,
    @ColumnInfo(name = "type") val type: String ,
    @ColumnInfo(name = "description") val description: String? = null
)