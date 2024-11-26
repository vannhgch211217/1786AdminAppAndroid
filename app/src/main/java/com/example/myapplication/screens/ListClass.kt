package com.example.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.myapplication.LocalClassDao
import com.example.myapplication.LocalCourseDao
import com.example.myapplication.data.Class
import com.example.myapplication.data.Course
import com.example.myapplication.LocalNav
import com.example.myapplication.Screens
import com.example.myapplication.header.MyTopBar

@Composable
fun ListClassScreen() {
    val nav = LocalNav.current
    val classDao = LocalClassDao.current
    val courseDao = LocalCourseDao.current
    val classes = remember { mutableStateOf(listOf<Class>()) }
    val courses = remember { mutableStateOf(listOf<Course>()) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        classes.value = classDao.getAll()
        courses.value = courseDao.getAll()
    }

    val filteredClasses = classes.value.filter {
        it.teacher?.contains(searchText.text, ignoreCase = true) == true ||
                it.date?.contains(searchText.text, ignoreCase = true) == true
    }

    Scaffold(
        topBar = { MyTopBar(title = "Class List",onBackPressed = {}, navigationIcon = {}) }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)) {
            // Search TextField
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search by Teacher Name or Date") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(filteredClasses) { classItem ->
                    val associatedCourse = courses.value.find { course -> course.cid == classItem.cid } // Example logic to find course

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color(0xFFE2E2E2))
                            .padding(16.dp)
                            .clickable {
                                nav.navigate("${Screens.DetailClass.name}/${classItem.cid}")
                            }
                    ) {
                        Column {
                            Text(text = "Teacher: ${classItem.teacher ?: "N/A"}")
                            Text(text = "Comment: ${classItem.comment ?: "N/A"}")
                            Text(text = "Date: ${classItem.date ?: "N/A"}")
                            associatedCourse?.let {
                                Text(text = "Course: ${it.type}, Day: ${it.day}, Time: ${it.time}")
                            } ?: Text(text = "Course: N/A")
                        }
                    }
                }
                if (filteredClasses.isEmpty()) {
                    item {
                        Text(text = "No classes available")
                    }
                }
            }
            Button(
                onClick = { nav.navigate(Screens.List.name) },
                modifier = Modifier.fillMaxWidth().padding(top=16.dp)
            ) {
                Text(text = "View Course List")
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

