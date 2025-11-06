package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EnhancedFloatingActionButtons(
    onAddTask: () -> Unit,
    onVoiceInput: () -> Unit = {},
    showVoiceButton: Boolean = false,
    isExtended: Boolean = false,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onAddTask,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Task")
    }
}

@Composable
fun MultiActionFAB(
    onAddTask: () -> Unit,
    onVoiceInput: () -> Unit,
    onQuickNote: () -> Unit = {},
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Secondary actions
        AnimatedVisibility(
            visible = isExpanded,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Voice Input
                SmallFloatingActionButton(
                    onClick = {
                        onVoiceInput()
                        onExpandedChange(false)
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Quick Note
                SmallFloatingActionButton(
                    onClick = {
                        onQuickNote()
                        onExpandedChange(false)
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Icon(
                        Icons.Default.Add, // Replace with note icon
                        contentDescription = "Quick Note",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        
        // Main FAB
        FloatingActionButton(
            onClick = {
                if (isExpanded) {
                    onAddTask()
                    onExpandedChange(false)
                } else {
                    onExpandedChange(true)
                }
            },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Task"
            )
        }
    }
}