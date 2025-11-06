package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    completedTasks: Int,
    totalTasks: Int,
    showProgress: Boolean = true,

    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onProgressClick: () -> Unit = {},
    isGrid: Boolean = false,
    onLayoutChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Compact progress bar with inline text (as in Tasks app)
        if (showProgress && totalTasks > 0) {
            val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0f
            LinearProgressStatus(
                progress = progress,
                text = "$completedTasks / $totalTasks completed",
                modifier = Modifier.clickable { onProgressClick() },
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                progressColor = MaterialTheme.colorScheme.primary,
                height = 18.dp
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Compact Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp),
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                AnimatedVisibility(visible = searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Chips
        FilterChipGroup(
            selectedFilter = selectedFilter,
            onFilterChange = onFilterChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FilterChipGroup(
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf("All", "Active", "Completed", "Important")
    
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterChange(filter) },
                label = { 
                    Text(
                        text = filter,
                        fontWeight = if (selectedFilter == filter) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun LinearProgressStatus(
    progress: Float,
    text: String,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    height: androidx.compose.ui.unit.Dp = 24.dp
) {
    Column(modifier = modifier) {
        val progressNormalized = progress.coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(trackColor),
            contentAlignment = Alignment.Center
        ) {
            // Progress background
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressNormalized)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(height / 2))
                    .background(progressColor)
                    .align(Alignment.CenterStart)
            )
            
            // Progress text
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LayoutToggle(
    isGrid: Boolean,
    onLayoutChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // List view button
        FilterChip(
            selected = !isGrid,
            onClick = { onLayoutChange(false) },
            leadingIcon = { Icon(Icons.Default.ViewList, contentDescription = null) },
            label = { Text("List") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Grid view button
        FilterChip(
            selected = isGrid,
            onClick = { onLayoutChange(true) },
            leadingIcon = { Icon(Icons.Default.ViewModule, contentDescription = null) },
            label = { Text("Grid") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}