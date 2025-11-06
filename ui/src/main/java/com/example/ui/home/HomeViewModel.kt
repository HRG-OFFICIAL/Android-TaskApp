package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Task
import com.example.domain.model.TaskPriority
import com.example.domain.usecase.DeleteTaskUseCase
import com.example.domain.usecase.ObserveTasksUseCase
import com.example.domain.usecase.SetTaskDoneUseCase
import com.example.domain.usecase.UpsertTaskUseCase
import com.example.domain.usecase.ClearAllTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskAction {
    DELETE,
    TOGGLE_COMPLETE,
    QUICK_ADD
}

data class TaskActionState(
    val taskId: Int,
    val action: TaskAction,
    val isLoading: Boolean
)

data class HomeUiState(
    val tasks: List<Task> = emptyList(),
    val taskActionStates: Map<String, Boolean> = emptyMap(),
    val quickAddLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    fun isTaskLoading(taskId: Int, action: TaskAction): Boolean {
        return taskActionStates["${taskId}_${action.name}"] ?: false
    }
    
    fun isQuickAddLoading(): Boolean = quickAddLoading
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeTasks: ObserveTasksUseCase,
    private val upsertTask: UpsertTaskUseCase,
    private val setTaskDone: SetTaskDoneUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val clearAllTasks: ClearAllTasksUseCase,
) : ViewModel() {

    private val _taskActionStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val _quickAddLoading = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        observeTasks(),
        _taskActionStates,
        _quickAddLoading
    ) { tasks, actionStates, quickAddLoading ->
        HomeUiState(
            tasks = tasks,
            taskActionStates = actionStates,
            quickAddLoading = quickAddLoading
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun addQuickTask(title: String) {
        if (title.isBlank()) return
        _quickAddLoading.value = true
        viewModelScope.launch {
            try {
                upsertTask(
                    Task(
                        title = title.trim(),
                        description = "",
                        priority = TaskPriority.MEDIUM
                    )
                )
            } finally {
                _quickAddLoading.value = false
            }
        }
    }

    fun toggleDone(task: Task) {
        val actionKey = "${task.id}_${TaskAction.TOGGLE_COMPLETE.name}"
        setTaskActionLoading(actionKey, true)
        viewModelScope.launch {
            try {
                setTaskDone(task.id, !task.isDone)
            } finally {
                setTaskActionLoading(actionKey, false)
            }
        }
    }

    fun delete(id: String) {
        val actionKey = "${id}_${TaskAction.DELETE.name}"
        setTaskActionLoading(actionKey, true)
        viewModelScope.launch {
            try {
                deleteTask(id)
            } finally {
                setTaskActionLoading(actionKey, false)
            }
        }
    }

    fun markImportant(ids: Set<String>, important: Boolean) {
        viewModelScope.launch {
            val tasksToUpdate = uiState.value.tasks.filter { ids.contains(it.id) }
            tasksToUpdate.forEach { upsertTask(it.copy(isImportant = important)) }
        }
    }

    fun upsert(task: Task) {
        viewModelScope.launch {
            upsertTask(task)
        }
    }

    fun deleteAll(ids: List<String>) {
        viewModelScope.launch {
            ids.forEach { deleteTask(it) }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            try {
                clearAllTasks()
            } catch (_: Exception) { }
        }
    }

    fun toggleImportant(task: Task) {
        viewModelScope.launch {
            upsertTask(task.copy(isImportant = !task.isImportant))
        }
    }

    private fun setTaskActionLoading(actionKey: String, isLoading: Boolean) {
        val currentStates = _taskActionStates.value.toMutableMap()
        if (isLoading) {
            currentStates[actionKey] = true
        } else {
            currentStates.remove(actionKey)
        }
        _taskActionStates.value = currentStates
    }
}
