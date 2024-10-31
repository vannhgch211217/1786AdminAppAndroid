package com.example.myapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

@Entity(tableName = "classes", foreignKeys = [ForeignKey(
    entity = Course::class,
    parentColumns = ["cid"],
    childColumns = ["courseId"],
    onDelete = ForeignKey.CASCADE
)]
    )
data class Class(
    @PrimaryKey(autoGenerate = true) val cid: Int = 0,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "teacher") val teacher: String,
    @ColumnInfo(name = "comment") val comment: String? = null,
    @ColumnInfo(name = "courseId") val courseId: Int
)

