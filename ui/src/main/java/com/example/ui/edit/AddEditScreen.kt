package com.example.ui.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    vm: AddEditViewModel = hiltViewModel(),
    onSaved: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val s by vm.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (s.id.isNullOrBlank()) "Add To‑Do Details" else "Edit Task Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!s.id.isNullOrBlank()) {
                        IconButton(onClick = { vm.delete(); onSaved() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Column(Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                // Fields
                OutlinedTextField(value = s.title, onValueChange = vm::onTitleChange, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = s.description, onValueChange = vm::onDescChange, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                Spacer(Modifier.height(12.dp))

                // Due date picker
                var showDatePicker by remember { mutableStateOf(false) }
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = s.dueAtEpochMillis)
                val dateFormatter = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }

                OutlinedTextField(
                    value = s.dueAtEpochMillis?.let { dateFormatter.format(Date(it)) } ?: "No date",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Due date") },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Pick date")
                            }
                            if (s.dueAtEpochMillis != null) {
                                IconButton(onClick = { vm.onDueChange(null) }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear date")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            Button(onClick = {
                                val picked = datePickerState.selectedDateMillis
                                vm.onDueChange(picked)
                                showDatePicker = false
                            }) { Text("Set") }
                        },
                        dismissButton = {
                            Button(onClick = { showDatePicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { vm.save(); onSaved() }) { Text("Save") }
                }
            }

            // Delete moved to top app bar actions; no bottom FAB
        }
    }
}
