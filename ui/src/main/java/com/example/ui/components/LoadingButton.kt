package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Enhanced Button component that manages its own loading state independently.
 * 
 * This component addresses the requirement for individual button loading states,
 * ensuring only the clicked button shows a loading indicator while other buttons
 * remain interactive.
 * 
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param isLoading Whether this specific button is in a loading state
 * @param enabled Whether the button is enabled for interaction
 * @param colors ButtonColors to customize the button appearance
 * @param elevation ButtonElevation to customize the button elevation
 * @param border BorderStroke to customize the button border
 * @param contentPadding PaddingValues for the button content
 * @param content The content to be displayed inside the button
 */
@Composable
fun LoadingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = colors.contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        content()
    }
}

/**
 * Enhanced OutlinedButton component that manages its own loading state independently.
 * 
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param isLoading Whether this specific button is in a loading state
 * @param enabled Whether the button is enabled for interaction
 * @param colors ButtonColors to customize the button appearance
 * @param elevation ButtonElevation to customize the button elevation
 * @param border BorderStroke to customize the button border
 * @param contentPadding PaddingValues for the button content
 * @param content The content to be displayed inside the button
 */
@Composable
fun LoadingOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = colors.contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        content()
    }
}

/**
 * Enhanced TextButton component that manages its own loading state independently.
 * 
 * @param onClick Callback invoked when the button is clicked
 * @param modifier Modifier to be applied to the button
 * @param isLoading Whether this specific button is in a loading state
 * @param enabled Whether the button is enabled for interaction
 * @param colors ButtonColors to customize the button appearance
 * @param elevation ButtonElevation to customize the button elevation
 * @param border BorderStroke to customize the button border
 * @param contentPadding PaddingValues for the button content
 * @param content The content to be displayed inside the button
 */
@Composable
fun LoadingTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                strokeWidth = 2.dp,
                color = colors.contentColor
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        content()
    }
}