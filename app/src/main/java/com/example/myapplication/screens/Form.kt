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
import androidx.compose.material.icons.filled.Person
import com.example.myapplication.LocalNav
import com.example.myapplication.LocalCourseDao
import com.example.myapplication.Screens
import com.example.myapplication.data.Course
import com.example.myapplication.header.MyTopBar


val commonTextStyle = TextStyle(
    color = Color.Black,
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold
)

val textFieldShape = RoundedCornerShape(16.dp)

val keyboardOptionsNumber = KeyboardOptions(
    imeAction = ImeAction.Done,
    keyboardType = KeyboardType.Number
)

val keyboardOptionsText = KeyboardOptions(
    imeAction = ImeAction.Done
)

@Composable
fun FormScreen() {
    val courseDao = LocalCourseDao.current
    val nav = LocalNav.current
    val errorMessage = remember { mutableStateOf("") }
    val capacityState = remember { mutableStateOf("") }
    val durationState = remember { mutableStateOf("") }
    val pricePerClassState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }

    // Trạng thái xác nhận lưu
    val saveConfirm = remember { mutableStateOf(false) }

    // Radio button state
    val selectedClassType = remember { mutableStateOf("Flow Yoga") }
    val classTypes = listOf("Flow Yoga", "Aerial Yoga", "Family Yoga")

    // Date and time states
    val selectedDate = remember { mutableStateOf("Monday") }
    val dayOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val (time, setTime) = remember { mutableStateOf(Calendar.getInstance()) }
    val (showTimePicker, setShowTimePicker) = remember { mutableStateOf(false) }

    val courseInfo = """
        Capacity: ${capacityState.value}
        Duration: ${durationState.value}
        Price per Class: ${pricePerClassState.value}
        Description: ${descriptionState.value}
        Type of Class: ${selectedClassType.value}
        Day of Week: ${selectedDate.value}
        Time: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(time.time)}
    """.trimIndent()

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

    // Popup xác nhận lưu
    if (saveConfirm.value) {
        AlertDialog(
            onDismissRequest = { saveConfirm.value = false },
            title = { Text("Confirm") },
            text = { Text(courseInfo) },
            confirmButton = {
                TextButton(onClick = {
                    // Xác nhận lưu khóa học
                    val newCourse = Course(
                        id = courseDao.getMaxCourseId() + 1,
                        day = selectedDate.value,
                        time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(time.time),
                        capacity = capacityState.value,
                        duration = durationState.value,
                        price = pricePerClassState.value,
                        type = selectedClassType.value,
                        description = descriptionState.value
                    )
                    courseDao.insert(newCourse)
                    nav.navigate(Screens.List.name)
                    saveConfirm.value = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { saveConfirm.value = false }) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        topBar = { MyTopBar(title = "Add new course", onBackPressed = { nav.navigate(Screens.List.name) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = innerPadding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
            CustomTextField(valueState = capacityState, label = "Course capacity", placeholder = "Enter capacity", keyboardOptions = keyboardOptionsNumber)
            CustomTextField(valueState = durationState, label = "Course duration", placeholder = "Enter duration", keyboardOptions = keyboardOptionsNumber)
            CustomTextField(valueState = pricePerClassState, label = "Course price", placeholder = "Enter price", keyboardOptions = keyboardOptionsNumber)
            CustomTextField(valueState = descriptionState, label = "Course description", placeholder = "Enter description", keyboardOptions = keyboardOptionsText)

            // Radio buttons for Class Type
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

            // Radio buttons for Day of Week
            Text("Day of Week", style = commonTextStyle, modifier = Modifier.padding(start = 30.dp, end = 16.dp, top = 16.dp))
            dayOfWeek.forEach { day ->
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

            // Time Picker Text
            Text(
                text = "Selected Time: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(time.time)}",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { setShowTimePicker(true) }
                    .padding(start = 16.dp, end = 16.dp, bottom=16.dp)
                    .background(Color(0xFFE2E2EE), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            )

            if (errorMessage.value.isNotEmpty()) {
                Text(
                    text = errorMessage.value,
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
            }

            Button(onClick = {
                // Clear the error message initially
                errorMessage.value = ""

                // Validate input fields
                when {
                    capacityState.value.isEmpty() -> errorMessage.value = "Capacity cannot be empty"
                    durationState.value.isEmpty() -> errorMessage.value = "Duration cannot be empty"
                    pricePerClassState.value.isEmpty() -> errorMessage.value = "Price cannot be empty"
                    selectedDate.value.isEmpty() -> errorMessage.value = "Select a day"
                    selectedClassType.value.isEmpty() -> errorMessage.value = "Select a class type"
                    else -> saveConfirm.value = true // Hiển thị popup xác nhận
                }
            }, modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)) {
                Text(text = "Save")
            }
        }
    }
}


@Composable
fun CustomTextField(
    valueState: MutableState<String>,
    label: String,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    onClick: (() -> Unit)? = null,
    isReadOnly: Boolean = false
) {
    val disableKeyboard = LocalSoftwareKeyboardController.current

    TextField(
        value = valueState.value,
        onValueChange = { newValue -> valueState.value = newValue },
        textStyle = commonTextStyle,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        trailingIcon = {
            IconButton(onClick = { valueState.value = "" }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "clear"
                )
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = textFieldShape,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = { disableKeyboard?.hide() }
        ),
        readOnly = isReadOnly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .run { if (onClick != null) clickable(onClick = onClick) else this }
    )
}
