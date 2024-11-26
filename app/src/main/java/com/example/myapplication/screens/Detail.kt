package com.example.myapplication.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import com.example.myapplication.LocalNav
import com.example.myapplication.LocalCourseDao
import com.example.myapplication.Screens
import com.example.myapplication.data.Course
import com.example.myapplication.header.MyTopBar

@Composable
fun DetailScreen(courseId: Int) {
    val courseDao = LocalCourseDao.current
    val nav = LocalNav.current

    val course = remember { mutableStateOf<Course?>(null) }

    LaunchedEffect(courseId) {
        course.value = courseDao.getCourseById(courseId)
    }

    course.value?.let { courseData ->
        val capacityState = remember { mutableStateOf(courseData.capacity) }
        val durationState = remember { mutableStateOf(courseData.duration) }
        val pricePerClassState = remember { mutableStateOf(courseData.price) }
        val descriptionState = remember { mutableStateOf(courseData.description.toString()) }

        val selectedClassType = remember { mutableStateOf(courseData.type) }
        val classTypes = listOf("Flow Yoga", "Aerial Yoga", "Family Yoga")

        val selectedDate = remember { mutableStateOf(courseData.day) }

        val (time, setTime) = remember { mutableStateOf(Calendar.getInstance()) }
        val timeParts = courseData.time.split(":").map { it.toInt() }
//        time.set(Calendar.HOUR_OF_DAY, timeParts[0])
//        time.set(Calendar.MINUTE, timeParts[1])

        val (showTimePicker, setShowTimePicker) = remember { mutableStateOf(false) }

        // State for confirmation dialog
        val updateConfirm = remember { mutableStateOf(false) }
        val deleteConfirm = remember { mutableStateOf(false) }

        // TimePickerDialog
        if (showTimePicker) {
            TimePickerDialog(
                LocalContext.current,
                { _, hourOfDay, minute ->
                    time.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    time.set(Calendar.MINUTE, minute)
                    setShowTimePicker(false)
                },
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE),
                true
            ).show()
        }

        if (updateConfirm.value) {
            AlertDialog(
                onDismissRequest = { updateConfirm.value = false },
                title = { Text("Confirm") },
                text = { Text("Are you sure you want to do this action?") },
                confirmButton = {
                    TextButton(onClick = {
                        val updatedCourse = Course(
                            cid = courseData.cid,
                            day = selectedDate.value,
                            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(time.time),
                            capacity = capacityState.value,
                            duration = durationState.value,
                            price = pricePerClassState.value,
                            type = selectedClassType.value,
                            description = descriptionState.value
                        )

                        courseDao.update(updatedCourse)

                        nav.navigate(Screens.List.name)
                        updateConfirm.value = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { updateConfirm.value = false }) {
                        Text("No")
                    }
                }
            )
        }

        if (deleteConfirm.value) {
            AlertDialog(
                onDismissRequest = { deleteConfirm.value = false },
                title = { Text("Confirm") },
                text = { Text("Are you sure you want to do this action?") },
                confirmButton = {
                    TextButton(onClick = {
                        courseDao.delete(courseDao.getCourseById(courseId))
                        nav.navigate(Screens.List.name)
                        deleteConfirm.value = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteConfirm.value = false }) {
                        Text("No")
                    }
                }
            )
        }

        Scaffold(
            topBar = { MyTopBar(title = "Course Details", onBackPressed = { nav.navigate(Screens.List.name) }) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = innerPadding.calculateTopPadding())
                    .verticalScroll(rememberScrollState()) // Enable vertical scrolling
            ) {
                CustomTextField(
                    valueState = capacityState,
                    label = "Course capacity",
                    placeholder = "Enter capacity",
                    keyboardOptions = keyboardOptionsNumber
                )

                CustomTextField(
                    valueState = durationState,
                    label = "Course duration",
                    placeholder = "Enter duration",
                    keyboardOptions = keyboardOptionsNumber
                )

                CustomTextField(
                    valueState = pricePerClassState,
                    label = "Course price",
                    placeholder = "Enter price",
                    keyboardOptions = keyboardOptionsNumber
                )

                CustomTextField(
                    valueState = descriptionState,
                    label = "Course description",
                    placeholder = "Enter description",
                    keyboardOptions = keyboardOptionsText
                )

                Text("Type of Class", style = commonTextStyle, modifier = Modifier.padding(start = 30.dp, end = 16.dp, top = 16.dp))
                classTypes.forEach { classType ->
                    Row(modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
                        RadioButton(
                            selected = (selectedClassType.value == classType),
                            onClick = { selectedClassType.value = classType },
                            modifier = Modifier.offset(y = (-12).dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(classType, style = commonTextStyle)
                    }
                }

                Text("Day of Week", style = commonTextStyle, modifier = Modifier.padding(start = 30.dp, end = 16.dp, top = 16.dp))
                listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday").forEach { day ->
                    Row(modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
                        RadioButton(
                            selected = (selectedDate.value == day),
                            onClick = { selectedDate.value = day },
                            modifier = Modifier.offset(y = (-12).dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(day, style = commonTextStyle)
                    }
                }

                Text(
                    text = "Selected Time: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(time.time)}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { setShowTimePicker(true) }
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .background(Color(0xFFE2E2EE), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                )

                Button(onClick = {
                    updateConfirm.value = true
                }, modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)) {
                    Text(text = "Update")
                }

                Button(onClick = {
                    deleteConfirm.value = true
                }, modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)) {
                    Text(text = "Delete")
                }
            }
        }
    } ?: run {
        Text("Course not found", modifier = Modifier.fillMaxSize(), style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
    }
}
