package com.example.ui.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.NotificationData
import com.example.domain.model.NotificationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onNavigateBack: () -> Unit = {}) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // Mock notification data
    val mockNotifications = listOf(
        NotificationData(
            type = NotificationType.TASK_OVERDUE,
            title = "You're behind schedule",
            message = "Task 'Project Update' is overdue by 1 day.",
            userId = "local",
            scheduledAt = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        ),
        NotificationData(
            type = NotificationType.TASK_DUE,
            title = "Upcoming deadline",
            message = "Task 'Client Presentation' due today at 5:00 PM.",
            userId = "local",
            scheduledAt = System.currentTimeMillis() + 2 * 60 * 60 * 1000
        ),
        NotificationData(
            type = NotificationType.TASK_REMINDER,
            title = "Friendly reminder",
            message = "Don't forget to review 'Sprint Backlog'.",
            userId = "local",
            scheduledAt = System.currentTimeMillis() + 60 * 60 * 1000
        ),
        NotificationData(
            type = NotificationType.TASK_COMPLETED,
            title = "Nice work!",
            message = "You completed 'Email Follow-ups'. Keep it up!",
            userId = "local",
            scheduledAt = System.currentTimeMillis() - 2 * 60 * 60 * 1000
        ),
        NotificationData(
            type = NotificationType.SMART_SUGGESTION,
            title = "Smart suggestion",
            message = "Consider marking 'Budget Review' as Important.",
            userId = "local",
            scheduledAt = System.currentTimeMillis()
        ),
        NotificationData(
            type = NotificationType.PRODUCTIVITY_INSIGHT,
            title = "Productivity insight",
            message = "You’re most productive at 10 AM. Plan complex tasks then.",
            userId = "local",
            scheduledAt = System.currentTimeMillis()
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* future: mark all read */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Mark all read")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockNotifications, key = { it.id }) { n ->
                NotificationItem(n)
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun NotificationItem(n: NotificationData) {
    val (icon, tint) = when (n.type) {
        NotificationType.TASK_OVERDUE -> Icons.Default.Warning to MaterialTheme.colorScheme.error
        NotificationType.TASK_DUE -> Icons.Default.Alarm to MaterialTheme.colorScheme.primary
        NotificationType.TASK_REMINDER -> Icons.Default.Alarm to MaterialTheme.colorScheme.primary
        NotificationType.TASK_COMPLETED -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
        NotificationType.SMART_SUGGESTION -> Icons.Default.Notifications to MaterialTheme.colorScheme.secondary
        NotificationType.PRODUCTIVITY_INSIGHT -> Icons.Default.Notifications to MaterialTheme.colorScheme.tertiary
        else -> Icons.Default.Notifications to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(n.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(n.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}