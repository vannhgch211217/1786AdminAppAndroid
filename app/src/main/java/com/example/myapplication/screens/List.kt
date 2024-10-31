package com.example.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.LocalCourseDao
import com.example.myapplication.data.Course
import com.example.myapplication.LocalNav
import com.example.myapplication.Screens
import com.example.myapplication.header.MyTopBar

@Composable
fun ListScreen() {
    val nav = LocalNav.current
    val courseDao = LocalCourseDao.current
    val courses = remember { mutableStateOf(listOf<Course>()) }

    LaunchedEffect(Unit) {
        courses.value = courseDao.getAll()
    }

    Scaffold(
        topBar = { MyTopBar(
            title = "Course List",
            onBackPressed = { },
            navigationIcon = { }
        ) }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding) // Add innerPadding for proper spacing
            .padding(16.dp)) {
            // Scrollable list of courses
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(courses.value) { course ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color(0xFFE2E2E2)) // Grey background
                            .padding(16.dp) // Inner padding for content
                            .clickable {
                                nav.navigate("${Screens.Detail.name}/${course.cid}")
                            }
                    ) {
                        Column {
                            Text(text = "Capacity: ${course.capacity}")
                            Text(text = "Duration: ${course.duration}")
                            Text(text = "Price: ${course.price}")
                            Text(text = "Type of Class: ${course.type}")
                            Text(text = "Description: ${course.description}")
                            Text(text = "Time: ${course.time}")
                            Text(text = "Day of Week: ${course.day}")
                        }
                    }
                }

                // If there are no courses, show a message
                if (courses.value.isEmpty()) {
                    item {
                        Text(text = "No courses available")
                    }
                }
            }

            // Buttons to navigate to FormScreen
            Button(
                onClick = { nav.navigate(Screens.ListClass.name) },
                modifier = Modifier.fillMaxWidth().padding(top=16.dp)
            ) {
                Text(text = "View Class List")
            }
            Button(
                onClick = { nav.navigate(Screens.FormClass.name) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text(text = "Add New Class")
            }
            Button(
                onClick = { nav.navigate(Screens.Form.name) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add New Course")
            }

        }
    }
}

