package com.example.myapplication

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.myapplication.screens.FormScreen
import com.example.myapplication.screens.ListScreen
import com.example.myapplication.screens.ListClassScreen
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.ClassDao
import com.example.myapplication.data.ClassService
import com.example.myapplication.data.CourseDao
import com.example.myapplication.data.CourseService
import com.example.myapplication.screens.FormClassScreen
import com.example.myapplication.screens.DetailScreen
import com.example.myapplication.screens.DetailClassScreen
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import androidx.compose.runtime.collectAsState


val LocalNav = compositionLocalOf<NavHostController> { error("Not initialized") }
val LocalCourseDao = compositionLocalOf<CourseDao> { error("Not initialized") }
val LocalClassDao = compositionLocalOf<ClassDao> { error("Not initialized") }
val LocalUid = compositionLocalOf<Int?> { error("Not initialized") }
val SetLocalUid = compositionLocalOf<(Int?) -> Unit> { error("Not initialized") }

// Define the screens for navigation
enum class Screens {
    List, Form, FormClass, ListClass, Detail, DetailClass
}

@Composable
fun App() {
    val nav = rememberNavController()
    val (selectedUid, setSelectedUid) = remember { mutableStateOf<Int?>(0) }

    val db = Room.databaseBuilder(
        klass = AppDatabase::class.java,
        name = "database-name",
        context = LocalContext.current
    ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

    val courseDao = db.CourseDao()
    val classDao = db.ClassDao()

    // Retrofit setup
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.55.110:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Define services
    val courseService = retrofit.create(CourseService::class.java)
    val classService = retrofit.create(ClassService::class.java)
    // Observe courses and classes changes
    val coursesFlow = courseDao.getAllCourse().collectAsState(initial = emptyList())
    val classesFlow = classDao.getAllClass().collectAsState(initial = emptyList())

    // Function to sync courses and classes
    suspend fun syncData() {
        try {
            val localCourses = coursesFlow.value
            val localClasses = classesFlow.value

            // Sync Courses
            val courseResponse = courseService.insertCourse(localCourses)

            // Sync Classes
            val classResponse = classService.insertClass(localClasses)

            if (courseResponse.isSuccessful && classResponse.isSuccessful) {
                Log.d("Sync", "Courses and Classes synced successfully")
            } else {
                Log.e("Sync", "Sync failed: ${courseResponse.errorBody()?.string()} or ${classResponse.errorBody()?.string()}")
            }
        } catch (e: IOException) {
            Log.e("Sync", "Network error: ${e.message}")
        } catch (e: Exception) {
            Log.e("Sync", "Error syncing data: ${e.message}")
        }
    }

    // Sync whenever the courses or classes list changes
    LaunchedEffect(coursesFlow.value, classesFlow.value) {
        syncData()
    }

    // Navigation setup
    CompositionLocalProvider(
        LocalNav provides nav,
        LocalCourseDao provides courseDao,
        LocalClassDao provides classDao
    ) {
        NavHost(navController = nav, startDestination = Screens.List.name) {
            composable(route = Screens.List.name) { ListScreen() }
            composable(route = Screens.Form.name) { FormScreen() }
            composable(route = Screens.FormClass.name) { FormClassScreen() }
            composable(route = Screens.ListClass.name) { ListClassScreen() }
            composable(
                route = "${Screens.Detail.name}/{courseId}",
                arguments = listOf(navArgument("courseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
                DetailScreen(courseId = courseId)
            }
            composable(
                route = "${Screens.DetailClass.name}/{classId}",
                arguments = listOf(navArgument("classId") { type = NavType.IntType })
            ) { backStackEntry ->
                val classId = backStackEntry.arguments?.getInt("classId") ?: 0
                DetailClassScreen(classId = classId)
            }
        }
    }
}
