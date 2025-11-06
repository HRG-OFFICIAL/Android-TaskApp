package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Enhanced IconButton component that manages its own loading state independently.
 * 
 * This component is specifically designed for icon-based actions like delete buttons,
 * addressing the requirement for individual loading states per button while maintaining
 * proper sizing and positioning of loading indicators.
 * 
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param isLoading Whether this specific button is in a loading state
 * @param enabled Whether the button is enabled for interaction
 * @param colors IconButtonColors to customize the button appearance
 * @param icon The icon content to be displayed when not loading
 */
@Composable
fun LoadingIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    icon: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = colors
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = colors.contentColor
            )
        } else {
            icon()
        }
    }
}

/**
 * Enhanced FilledIconButton component that manages its own loading state independently.
 * 
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param isLoading Whether this specific button is in a loading state
 * @param enabled Whether the button is enabled for interaction
 * @param colors IconButtonColors to customize the button appearance
 * @param icon The icon content to be displayed when not loading
 */
@Composable
fun LoadingFilledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    icon: @Composable () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = colors
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = colors.contentColor
            )
        } else {
            icon()
        }
    }
}

/**
 * Enhanced OutlinedIconButton component that manages its own loading state independently.
 * 
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param isLoading Whether this specific button is in a loading state
 * @param enabled Whether the button is enabled for interaction
 * @param colors IconButtonColors to customize the button appearance
 * @param border BorderStroke to customize the button border
 * @param icon The icon content to be displayed when not loading
 */
@Composable
fun LoadingOutlinedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
    border: BorderStroke? = IconButtonDefaults.outlinedIconButtonBorder(enabled = enabled && !isLoading),
    icon: @Composable () -> Unit
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = colors,
        border = border
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = colors.contentColor
            )
        } else {
            icon()
        }
    }
}