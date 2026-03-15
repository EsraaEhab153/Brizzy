package com.example.brizzy.presentation.alerts.view

import android.Manifest
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brizzy.data.weather.model.AlertEntity
import com.example.brizzy.presentation.alerts.viewModel.AlertsViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.brizzy.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import java.util.Calendar
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(viewModel: AlertsViewModel) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
        }
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    val alerts by viewModel.alerts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var alertToDelete by remember { mutableStateOf<AlertEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F1B3C))) {
        Column(modifier = Modifier.fillMaxSize()) {

            AlertsHeader(
                activeCount = alerts.size,
                totalCount = alerts.size,
                onAddClick = { showAddDialog = true }
            )

            if (alerts.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "No Alerts",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No active alerts", color = Color.White.copy(alpha = 0.6f), fontSize = 18.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(items = alerts, key = { it.id }) { alert ->
                         SwipeToDeleteAlertBox(
                            alert = alert,
                            onDeleteInitiated = {
                                alertToDelete = alert
                                showConfirmDialog = true
                            }
                        )
                    }
                }
            }
        }

        DeleteAlertConfirmationDialog(
            show = showConfirmDialog,
            alertName = alertToDelete?.cityName ?: "",
            onConfirm = {
                alertToDelete?.let { viewModel.deleteAlert(it) }
                showConfirmDialog = false
                alertToDelete = null
            },
            onDismiss = {
                showConfirmDialog = false
                alertToDelete = null
            }
        )

        if (showAddDialog) {
            AddAlertDialog(
                onDismiss = { showAddDialog = false },
                onSave = { newAlert ->
                    viewModel.insertAlert(newAlert, context)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AlertsHeader(activeCount: Int, totalCount: Int, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, top = 64.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Alerts", style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.Bold)
            Text("$activeCount active - $totalCount total", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(top = 4.dp))
        }

        Surface(
            color = Color.White.copy(alpha = 0.1f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.clickable { onAddClick() }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Add Alert", tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Alert", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun AlertCard(alert: AlertEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val startStr = dateFormat.format(Date(alert.startTime))
    var isEnabled by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = if (alert.isNotification) R.drawable.bell else R.drawable.alarm
                    ),
                    contentDescription = "alert",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (alert.isNotification) "Notification Alert" else "Alarm Sound Alert",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(alert.cityName, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(" • $startStr", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                }
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = { isEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF29B2DD),
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteAlertBox(
    alert: AlertEntity,
    onDeleteInitiated: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDeleteInitiated()
                return@rememberSwipeToDismissBoxState false
            }
            return@rememberSwipeToDismissBoxState false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) Color.Transparent else Color.Red.copy(alpha = 0.8f),
                label = "color"
            )

            Box(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 8.dp).clip(RoundedCornerShape(20.dp)).background(color)
            ) {
                if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp)
                    )
                }
            }
        },
        content = { AlertCard(alert = alert) },
        enableDismissFromStartToEnd = false
    )
}

@Composable
fun DeleteAlertConfirmationDialog(show: Boolean, alertName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Alert", color = Color.White) },
            text = { Text("Are you sure you want to delete the alert for $alertName?", color = Color.White.copy(alpha = 0.7f)) },
            confirmButton = {
                TextButton(onClick = onConfirm) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF29B2DD)) }
            },
            containerColor = Color(0xFF0F1B3C),
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun AddAlertDialog(onDismiss: () -> Unit, onSave: (AlertEntity) -> Unit) {
    val context = LocalContext.current
    var isNotification by remember { mutableStateOf(true) }
    var cityName by remember { mutableStateOf("Alexandria") }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var endTime by remember { mutableStateOf(System.currentTimeMillis() + 86400000) }
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F1B3C),
        shape = RoundedCornerShape(24.dp),
        title = { Text("New Alert", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {

                Text("Alert Type", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isNotification,
                        onClick = { isNotification = true },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF29B2DD), unselectedColor = Color.White)
                    )
                    Text("Notification", color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = !isNotification,
                        onClick = { isNotification = false },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF5252), unselectedColor = Color.White)
                    )
                    Text("Alarm", color = Color.White, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("From", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().clickable {
                        showDateTimePicker(context, System.currentTimeMillis()) { selectedTime ->
                            startTime = selectedTime
                            if (startTime >= endTime) endTime = startTime + 3600000
                        }
                    }
                ) {
                    Text(
                        text = dateFormat.format(Date(startTime)),
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("To", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().clickable {
                        showDateTimePicker(context, startTime) { selectedTime ->
                            endTime = selectedTime
                        }
                    }
                ) {
                    Text(
                        text = dateFormat.format(Date(endTime)),
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        AlertEntity(
                            cityName = cityName,
                            lat = 31.2001,
                            lon = 29.9187,
                            startTime = startTime,
                            endTime = endTime,
                            isNotification = isNotification
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29B2DD))
            ) { Text("Save", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.White.copy(alpha = 0.7f)) }
        }
    )
}

fun showDateTimePicker(
    context: Context,
    minTimeInMillis: Long,
    onDateTimeSelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0)
            if (selectedCalendar.timeInMillis < minTimeInMillis) {
                Toast.makeText(context, "Please select a valid future time!", Toast.LENGTH_SHORT).show()
            } else {
                onDateTimeSelected(selectedCalendar.timeInMillis)
            }

        }, hour, minute, false).show()

    }, year, month, day)
    datePickerDialog.datePicker.minDate = minTimeInMillis
    datePickerDialog.show()
}