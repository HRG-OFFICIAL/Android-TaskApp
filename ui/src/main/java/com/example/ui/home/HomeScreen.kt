package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import java.text.SimpleDateFormat as SimpleDateFormat1
import java.util.Calendar
import java.util.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.model.Task
import com.example.ui.components.*
import com.example.ui.theme.TaskColors
import com.example.ui.settings.LocalUiSettingsController
import com.example.ui.profile.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(), 
    navController: NavController? = null
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    // List view only; remove grid toggle

    var selectedTasks by remember { mutableStateOf(setOf<String>()) }
    var isSelectionMode by remember { mutableStateOf(false) }
    
    val filteredTasks = remember(state.tasks, searchQuery, selectedFilter) {
        state.tasks.filter { task ->
            val matchesSearch = searchQuery.isBlank() || 
                task.title.contains(searchQuery, ignoreCase = true) || 
                task.description.contains(searchQuery, ignoreCase = true)
            
            val matchesFilter = when (selectedFilter) {
                "Active" -> !task.isDone
                "Completed" -> task.isDone
                "Important" -> task.isImportant
                else -> true
            }
            
            matchesSearch && matchesFilter
        }
    }

    // Group tasks into intuitive buckets with headers
    val dayGroups = remember(filteredTasks) {
        val now = System.currentTimeMillis()
        val startToday = startOfDay(now)
        val startTomorrow = startToday + 24L * 60L * 60L * 1000L
        val startDayAfter = startTomorrow + 24L * 60L * 60L * 1000L

        val map = linkedMapOf<String, MutableList<Task>>()

        fun add(key: String, t: Task) {
            map.getOrPut(key) { mutableListOf() }.add(t)
        }

        filteredTasks.forEach { t ->
            val due = t.dueAtEpochMillis
            if (due == null) {
                add("No Date", t)
            } else {
                when {
                    !t.isDone && due < startToday -> add("Overdue", t)
                    due in startToday until startTomorrow -> add("Today", t)
                    due in startTomorrow until startDayAfter -> add("Tomorrow", t)
                    else -> add(formatHeaderDate(due), t)
                }
            }
        }

        // Sort within groups: important first, then by due/created time
        val sortedInGroups = map.mapValues { (_, list) ->
            list.sortedWith(compareBy({ !it.isImportant }, { it.dueAtEpochMillis ?: it.createdAtEpochMillis }))
        }

        // Order headers: No Date -> Overdue -> Today -> Tomorrow -> specific dates ascending
        fun headerOrder(header: String): Int = when (header) {
            "No Date" -> 0
            "Overdue" -> 1
            "Today" -> 2
            "Tomorrow" -> 3
            else -> 4
        }

        sortedInGroups.entries
            .map { (header, list) ->
                val epochKey = when (header) {
                    "No Date" -> Long.MAX_VALUE
                    "Overdue" -> Long.MIN_VALUE
                    "Today" -> startToday
                    "Tomorrow" -> startTomorrow
                    else -> list.minOfOrNull { it.dueAtEpochMillis ?: Long.MAX_VALUE } ?: Long.MAX_VALUE
                }
                Triple(headerOrder(header), epochKey, Pair(header, list))
            }
            .sortedWith(compareBy<Triple<Int, Long, Pair<String, List<Task>>>>({ it.first }).thenBy { it.second })
            .map { it.third }
    }
    
    val lazyListState = rememberLazyListState()
    val isFabExtended by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }

    // Obtain ProfileViewModel at composable scope for logout action
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val uiSettings = LocalUiSettingsController.current

    // Clear local tasks and load mock data on startup
    val context = LocalContext.current
    var seeded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!seeded) {
            // Clear all local tasks reliably
            viewModel.clearAll()

            // Load mock tasks from assets
            try {
                val json = context.assets.open("mock_tasks.json").bufferedReader().use { it.readText() }
                val array = org.json.JSONArray(json)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val title = obj.optString("title")
                    val description = obj.optString("description")
                    val isDone = obj.optBoolean("isDone", false)
                    val priorityStr = obj.optString("priority", "MEDIUM")
                    val priority = com.example.domain.model.TaskPriority.values().firstOrNull { it.name == priorityStr } ?: com.example.domain.model.TaskPriority.MEDIUM
                    val dueAt = if (obj.has("dueAtEpochMillis") && !obj.isNull("dueAtEpochMillis")) obj.getLong("dueAtEpochMillis") else null
                    val tags = mutableListOf<String>()
                    val tagsArr = obj.optJSONArray("tags")
                    if (tagsArr != null) {
                        for (t in 0 until tagsArr.length()) tags.add(tagsArr.getString(t))
                    }
                    val isImportant = obj.optBoolean("isImportant", false)
                    val progress = obj.optDouble("progress", 0.0).toFloat()
                    val colorIndex = obj.optInt("colorIndex", 0)

                    val task = Task(
                        id = "", // let DB autogenerate local id
                        title = title,
                        description = description,
                        isDone = isDone,
                        priority = priority,
                        dueAtEpochMillis = dueAt,
                        tags = tags,
                        isImportant = isImportant,
                        progress = progress,
                        colorIndex = colorIndex
                    )
                    viewModel.upsert(task)
                }
            } catch (_: Exception) { }

            // Ensure coverage across Day Buckets with programmatically generated tasks
            runCatching {
                val now = System.currentTimeMillis()
                val startToday = startOfDay(now)
                val startTomorrow = startToday + 24L * 60L * 60L * 1000L
                val startDayAfter = startTomorrow + 24L * 60L * 60L * 1000L
                val hour = 60L * 60L * 1000L

                val bucketTasks = listOf(
                    // Overdue – not done and past today
                    Task(
                        title = "Pay electricity bill",
                        description = "Monthly bill pending",
                        isDone = false,
                        priority = com.example.domain.model.TaskPriority.HIGH,
                        dueAtEpochMillis = startToday - 6L * hour,
                        isImportant = true,
                    ),
                    Task(
                        title = "Finish bug fix PR",
                        description = "Address critical issue #482",
                        isDone = false,
                        priority = com.example.domain.model.TaskPriority.URGENT,
                        dueAtEpochMillis = startToday - 18L * hour,
                        tags = listOf("work"),
                        isImportant = true,
                    ),

                    // Today – due between now and midnight
                    Task(
                        title = "Prepare daily standup notes",
                        description = "Summarize blockers and progress",
                        priority = com.example.domain.model.TaskPriority.MEDIUM,
                        dueAtEpochMillis = startToday + 3L * hour,
                        tags = listOf("work"),
                    ),
                    Task(
                        title = "Grocery pickup",
                        description = "Vegetables, fruits, and milk",
                        priority = com.example.domain.model.TaskPriority.LOW,
                        dueAtEpochMillis = startToday + 18L * hour,
                        tags = listOf("home"),
                    ),

                    // Tomorrow – due tomorrow
                    Task(
                        title = "Plan sprint backlog",
                        description = "Refine user stories and estimates",
                        priority = com.example.domain.model.TaskPriority.HIGH,
                        dueAtEpochMillis = startTomorrow + 10L * hour,
                        tags = listOf("work", "planning"),
                        isImportant = true,
                    ),
                    Task(
                        title = "Client follow-up email",
                        description = "Send update on integrations",
                        priority = com.example.domain.model.TaskPriority.MEDIUM,
                        dueAtEpochMillis = startTomorrow + 15L * hour,
                        tags = listOf("work"),
                    ),

                    // Specific Dates – future tasks beyond tomorrow
                    Task(
                        title = "Doctor appointment",
                        description = "Annual health check",
                        priority = com.example.domain.model.TaskPriority.MEDIUM,
                        dueAtEpochMillis = startDayAfter + 9L * hour,
                        tags = listOf("health"),
                    ),
                    Task(
                        title = "Team offsite prep",
                        description = "Arrange venue and agenda",
                        priority = com.example.domain.model.TaskPriority.HIGH,
                        dueAtEpochMillis = startDayAfter + 5L * 24L * hour + 12L * hour,
                        tags = listOf("work"),
                        isImportant = true,
                    ),

                    // No Date – tasks without due date (removed specific titles per request)
                )

                bucketTasks.forEach { viewModel.upsert(it) }
            }

            seeded = true
        }
    }

    // Parse "Time:" line from description and return today's epoch millis for the start time
    fun parseStartTime(desc: String): Long? {
        val lines = desc.split('\n')
        val timeLine = lines.firstOrNull { it.trim().startsWith("Time:") } ?: return null
        // Extract the first time occurrence (e.g., 6:30 AM)
        val regex = Regex("(\\d{1,2}:\\d{2}\\s*[AP]M)")
        val match = regex.find(timeLine) ?: return null
        val timeStr = match.groupValues[1].replace("\u00A0", " ").trim()
        return try {
            val sdf = SimpleDateFormat1("h:mm a", Locale.getDefault())
            val cal = Calendar.getInstance()
            val parsed = sdf.parse(timeStr)
            if (parsed != null) {
                val parsedCal = Calendar.getInstance().apply { time = parsed }
                cal.set(Calendar.HOUR_OF_DAY, parsedCal.get(Calendar.HOUR_OF_DAY))
                cal.set(Calendar.MINUTE, parsedCal.get(Calendar.MINUTE))
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            } else null
        } catch (_: Exception) {
            null
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { 
                        Text("${selectedTasks.size} selected") 
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            isSelectionMode = false
                            selectedTasks = emptySet()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Cancel")
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            selectedTasks.forEach { taskId ->
                                viewModel.delete(taskId)
                            }
                            isSelectionMode = false
                            selectedTasks = emptySet()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                        IconButton(onClick = {
                            viewModel.markImportant(selectedTasks, true)
                            isSelectionMode = false
                            selectedTasks = emptySet()
                        }) {
                            Icon(Icons.Default.Star, contentDescription = "Mark Important")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    scrollBehavior = scrollBehavior
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            getCurrentDateString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {},
                    actions = {
                        // Stats button visible on top bar
                        IconButton(onClick = { navController?.navigate("stats") }) {
                            Icon(Icons.Default.BarChart, contentDescription = "Statistics")
                        }
                        // Hamburger dropdown menu for Settings, Notifications, Logout
                        var menuExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    menuExpanded = false
                                    navController?.navigate("settings")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Notifications") },
                                onClick = {
                                    menuExpanded = false
                                    navController?.navigate("notifications")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Log out") },
                                onClick = {
                                    menuExpanded = false
                                    // Sign out and navigate to signin
                                    profileViewModel.onSignOut()
                                    navController?.navigate("signin") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                EnhancedFloatingActionButtons(
                    onAddTask = { navController?.navigate("edit") },
                    onVoiceInput = {},
                    showVoiceButton = uiSettings.showVoiceIcon,
                    isExtended = isFabExtended
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        if (filteredTasks.isEmpty() && searchQuery.isBlank()) {
            // Empty state - show neutral message instead of CTA that navigates away
            val emptyMessage = when (selectedFilter) {
                "Completed" -> "No tasks are completed"
                "Important" -> "No important tasks"
                "Active" -> "No active tasks"
                else -> "No tasks yet"
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Empty",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        emptyMessage,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // List view only
            LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .padding(paddingValues),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header with search and filters (non-sticky for compatibility)
                    item {
                        Surface(color = MaterialTheme.colorScheme.surface) {
                            SearchAndFilterHeader(
                                searchQuery = searchQuery,
                                onSearchQueryChange = { searchQuery = it },
                                completedTasks = state.tasks.count { it.isDone },
                                totalTasks = state.tasks.size,
                                showProgress = uiSettings.showProgressHeader,

                                selectedFilter = selectedFilter,
                                onFilterChange = { selectedFilter = it },
                                onProgressClick = { navController?.navigate("stats") },
                            )
                        }
                    }

                    // Task items grouped by date
                    dayGroups.forEach { (header, tasksInGroup) ->
                        @OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
                        stickyHeader {
                            GroupHeader(title = header)
                        }
                        items(items = tasksInGroup, key = { it.id }) { task ->
                            val index = tasksInGroup.indexOf(task)
                            StaggeredAnimation(
                                visible = true,
                                index = index
                            ) {
                                EnhancedTaskItem(
                                    task = task,
                                    isSelected = selectedTasks.contains(task.id),
                                    onToggleComplete = { viewModel.toggleDone(task) },
                                    onEdit = { navController?.navigate("edit/${task.id}") },
                                    onClick = {
                                        if (isSelectionMode) {
                                            selectedTasks = if (selectedTasks.contains(task.id)) {
                                                selectedTasks - task.id
                                            } else {
                                                selectedTasks + task.id
                                            }
                                            if (selectedTasks.isEmpty()) {
                                                isSelectionMode = false
                                            }
                                        } else {
                                            navController?.navigate("edit/${task.id}")
                                        }
                                    },
                                    onLongClick = {
                                        if (!isSelectionMode) {
                                            isSelectionMode = true
                                            selectedTasks = setOf(task.id)
                                        }
                                    },
                                    isListView = true
                                )
                            }
                        }
                    }

                    // Bottom spacing for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
        }
    }
}

private fun getCurrentDateString(): String {
    val formatter = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
    return formatter.format(Date())
}

private fun startOfDay(millis: Long): Long {
    val cal = Calendar.getInstance().apply { timeInMillis = millis }
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

private fun formatHeaderDate(millis: Long): String {
    val formatter = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
private fun GroupHeader(title: String) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}