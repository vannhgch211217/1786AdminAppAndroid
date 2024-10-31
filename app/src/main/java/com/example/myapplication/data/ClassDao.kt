package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

@Dao
interface ClassDao {
    @Query("SELECT * FROM classes")
    fun getAll(): List<Class>

    @Query("SELECT * FROM classes")
    fun getAllClass(): Flow<List<Class>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(classEntity: Class)

    @Delete
    fun delete(classEntity: Class)

    @Update
    fun update(classEntity: Class)

    @Query("SELECT * FROM classes WHERE cid = :classId")
    fun getClassById(classId: Int): Class
}
interface ClassService {

    @POST("/api/syncClasses")
    suspend fun insertClass(@Body course: List<Class>): Response<Void>

}

