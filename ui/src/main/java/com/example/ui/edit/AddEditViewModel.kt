package com.example.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Task
import com.example.domain.model.TaskPriority
import com.example.domain.usecase.ObserveTaskUseCase
import com.example.domain.usecase.UpsertTaskUseCase
import com.example.domain.usecase.ToggleImportantUseCase
import com.example.domain.usecase.DeleteTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.data.work.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditUiState(
    val id: String? = null,
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueAtEpochMillis: Long? = null,
    val isImportant: Boolean = false,
)

@HiltViewModel
class AddEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeTask: ObserveTaskUseCase,
    private val upsertTask: UpsertTaskUseCase,
    private val toggleImportant: ToggleImportantUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditUiState())
    val state: StateFlow<AddEditUiState> = _state.asStateFlow()

    init {
        val id: String? = savedStateHandle.get<String>("id")
        if (id != null) {
            _state.update { it.copy(id = id) }
            viewModelScope.launch {
                observeTask(id).collect { task ->
                    if (task != null) {
                        _state.update {
                            it.copy(
                                title = task.title,
                                description = task.description,
                                priority = task.priority,
                                dueAtEpochMillis = task.dueAtEpochMillis,
                                isImportant = task.isImportant
                            )
                        }
                    }
                }
            }
        }
    }

    fun onTitleChange(v: String) { _state.update { it.copy(title = v) } }
    fun onDescChange(v: String) { _state.update { it.copy(description = v) } }
    fun onDueChange(v: Long?) { _state.update { it.copy(dueAtEpochMillis = v) } }
    fun onPriorityChange(v: TaskPriority) { _state.update { it.copy(priority = v) } }
    fun onImportantChange(v: Boolean) { _state.update { it.copy(isImportant = v) } }

    fun save() {
        val s = _state.value
        if (s.title.isBlank()) return
        viewModelScope.launch {
            upsertTask(
                Task(
                    id = s.id ?: "",
                    title = s.title.trim(),
                    description = s.description.trim(),
                    priority = s.priority,
                        dueAtEpochMillis = s.dueAtEpochMillis,
                        isImportant = s.isImportant
                )
            )
            val now = System.currentTimeMillis()
            val due = s.dueAtEpochMillis
            if (due != null && due > now) {
                reminderScheduler.scheduleReminder(
                    delayMillis = due - now,
                    title = s.title,
                    text = "Due soon"
                )
            }
        }
    }

    fun delete() {
        val id = _state.value.id
        if (id.isNullOrBlank()) return
        viewModelScope.launch {
            try {
                deleteTask(id)
            } catch (_: Exception) { }
        }
    }

    fun toggleImportant() {
        viewModelScope.launch {
            val s = state.value
            toggleImportant(s.id ?: return@launch, !s.isImportant)
        }
    }
}
