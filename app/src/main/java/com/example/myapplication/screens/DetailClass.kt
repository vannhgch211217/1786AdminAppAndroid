package com.example.myapplication.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import com.example.myapplication.LocalNav
import com.example.myapplication.LocalClassDao
import com.example.myapplication.LocalCourseDao
import com.example.myapplication.Screens
import com.example.myapplication.data.Class
import com.example.myapplication.data.Course
import com.example.myapplication.header.MyTopBar


@Composable
fun DetailClassScreen(classId: Int) {
    val classDao = LocalClassDao.current
    val courseDao = LocalCourseDao.current
    val nav = LocalNav.current
    val teacherState = remember { mutableStateOf("") }
    val commentState = remember { mutableStateOf("") }

    // States to hold error messages for validation
    val teacherErrorState = remember { mutableStateOf("") }
    val courseErrorState = remember { mutableStateOf("") }

    // State to hold the list of courses
    val courseList = remember { mutableStateOf<List<Course>>(emptyList()) }

    // State to hold the selected course ID
    val selectedCourseId = remember { mutableStateOf(0) }

    // Date state
    val (classDate, setClassDate) = remember { mutableStateOf(Date()) } // Move this declaration here
    val (showDatePicker, setShowDatePicker) = remember { mutableStateOf(false) }

    // Load class details to edit
    val currentClass = remember { mutableStateOf<Class?>(null) }
    LaunchedEffect(classId) {
        currentClass.value = classDao.getClassById(classId)
        currentClass.value?.let {
            teacherState.value = it.teacher
            commentState.value = it.comment ?: ""
            selectedCourseId.value = it.courseId.toInt()
            // Set date from the existing class (make sure to parse date correctly)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parsedDate = dateFormat.parse(it.date) ?: Date() // Safely parse the date
            setClassDate(parsedDate) // Now this reference should be resolved
        }
    }

    // Retrieve courses from the database
    LaunchedEffect(Unit) {
        courseList.value = courseDao.getAll()
    }

    // DatePickerDialog
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        calendar.time = classDate

        DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                setClassDate(calendar.time) // Use setClassDate here
                setShowDatePicker(false)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = { MyTopBar(title = "Class Details",onBackPressed = {nav.navigate(Screens.ListClass.name)}) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .verticalScroll(rememberScrollState()) // Enable vertical scrolling
            ) {
                CustomTextField(
                    valueState = teacherState,
                    label = "Course teacher",
                    placeholder = "Enter teacher",
                    keyboardOptions = keyboardOptionsText
                )
                // Display error message for teacher
                if (teacherErrorState.value.isNotEmpty()) {
                    Text(
                        text = teacherErrorState.value,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                CustomTextField(
                    valueState = commentState,
                    label = "Comment duration",
                    placeholder = "Enter comment",
                    keyboardOptions = keyboardOptionsText
                )

                // Radio buttons for Course selection
                Text("Select Course", style = commonTextStyle, modifier = Modifier.padding(start = 30.dp, end = 16.dp, top = 16.dp))
                courseList.value.forEach { course ->
                    Row(modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
                        RadioButton(
                            selected = (selectedCourseId.value == course.cid),
                            onClick = { selectedCourseId.value = course.cid },
                            modifier = Modifier.offset(y = (-12).dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${course.type}, Day: ${course.day}, Time: ${course.time}", style = commonTextStyle)
                    }
                }
                // Display error message for course selection
                if (courseErrorState.value.isNotEmpty()) {
                    Text(
                        text = courseErrorState.value,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Text(
                    text = "Selected Date: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(classDate)}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { setShowDatePicker(true) }
                        .padding(start = 16.dp, end = 16.dp) // Uniform padding
                        .padding(bottom=16.dp)
                        .background(Color(0xFFE2E2EE), RoundedCornerShape(16.dp)) // Gray background
                        .padding(16.dp) // Additional padding for text
                )

                Button(
                    onClick = {
                        // Reset error states
                        teacherErrorState.value = ""
                        courseErrorState.value = ""
                        val selectedCourse = courseList.value.find { it.cid == selectedCourseId.value }
                        val courseDay = selectedCourse?.day ?: ""

                        // Check if the selected date matches the course day
                        val selectedDateString = SimpleDateFormat("EEEE", Locale.getDefault()).format(classDate)

                        if (selectedDateString != courseDay) {
                            courseErrorState.value = "Selected date does not match course day."
                        } else {
                            // Reset course error if date matches
                            courseErrorState.value = ""

                            // Validate teacher input
                            if (teacherState.value.isEmpty()) {
                                teacherErrorState.value = "Teacher cannot be empty."
                            }

                            // Validate course selection
                            if (selectedCourseId.value == 0) {
                                courseErrorState.value = "Please select a course."
                            }

                            // If all validations pass, update class
                            if (teacherErrorState.value.isEmpty() && courseErrorState.value.isEmpty()) {
                                // Create the updated Class
                                val updatedClass = Class(
                                    cid = currentClass.value!!.cid, // Ensure you set the id of the existing class
                                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(classDate),
                                    comment = commentState.value,
                                    teacher = teacherState.value,
                                    courseId = selectedCourseId.value // Use the selected course ID here
                                )
                                classDao.update(updatedClass) // Assuming you have an update method
                                nav.navigate(Screens.ListClass.name)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp) // Padding for the button
                ) {
                    Text(text = "Update")
                }
                Button(onClick = {
                    classDao.delete(classDao.getClassById(classId))

                    nav.navigate(Screens.ListClass.name)
                }, modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)) {
                    Text(text = "Delete")
                }
            }
        }
    }
}







