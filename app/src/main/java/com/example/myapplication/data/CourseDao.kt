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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses")
    fun getAllCourse(): Flow<List<Course>>

    @Query("SELECT * FROM courses")
    fun getAll(): List<Course>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(course: Course)

    @Delete
    fun delete(course: Course)

    @Query("DELETE FROM courses")
    fun deleteAllCourses()

    @Query("SELECT * FROM courses WHERE cid = :courseId ")
    fun getCourseById(courseId: Int): Course

    @Update
    fun update(course: Course)

    @Query("SELECT MAX(cid) FROM courses")
    fun getMaxCourseId(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg courses: Course)
}
interface CourseService {

    @POST("/api/syncCourses")
    suspend fun insertCourse(@Body course: List<Course>): Response<Void>

}
