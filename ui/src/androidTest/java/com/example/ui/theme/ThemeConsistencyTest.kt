package com.example.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemeConsistencyTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun appTheme_usesBluePrimaryColor() {
        var primaryColor: Color? = null
        
        composeTestRule.setContent {
            AppTheme {
                primaryColor = MaterialTheme.colorScheme.primary
                Column {
                    Text("Theme Test")
                }
            }
        }
        
        // Verify that the primary color is blue (Material Blue 700: #1976D2)
        val expectedBlue = Color(0xFF1976D2)
        assert(primaryColor == expectedBlue) {
            "Expected primary color to be $expectedBlue but was $primaryColor"
        }
    }
    
    @Test
    fun appTheme_consistentAcrossComponents() {
        var buttonPrimaryColor: Color? = null
        var textPrimaryColor: Color? = null
        
        composeTestRule.setContent {
            AppTheme {
                Column {
                    Button(onClick = { }) {
                        Text("Button")
                        buttonPrimaryColor = MaterialTheme.colorScheme.primary
                    }
                    Text(
                        text = "Text",
                        color = MaterialTheme.colorScheme.primary
                    )
                    textPrimaryColor = MaterialTheme.colorScheme.primary
                }
            }
        }
        
        // Verify both components use the same primary color
        assert(buttonPrimaryColor == textPrimaryColor) {
            "Button and text should use the same primary color"
        }
        
        // Verify it's the expected blue color
        val expectedBlue = Color(0xFF1976D2)
        assert(buttonPrimaryColor == expectedBlue) {
            "Primary color should be Material Blue 700"
        }
    }
    
    @Test
    fun appTheme_lightAndDarkModeConsistency() {
        var lightPrimaryColor: Color? = null
        var darkPrimaryColor: Color? = null
        
        // Test light theme
        composeTestRule.setContent {
            AppTheme(useDarkTheme = false) {
                lightPrimaryColor = MaterialTheme.colorScheme.primary
                Text("Light Theme")
            }
        }
        
        // Test dark theme
        composeTestRule.setContent {
            AppTheme(useDarkTheme = true) {
                darkPrimaryColor = MaterialTheme.colorScheme.primary
                Text("Dark Theme")
            }
        }
        
        // Both should use the same blue color (fixed theme)
        val expectedBlue = Color(0xFF1976D2)
        assert(lightPrimaryColor == expectedBlue) {
            "Light theme should use Material Blue 700"
        }
        assert(darkPrimaryColor == expectedBlue) {
            "Dark theme should use Material Blue 700"
        }
    }
    
    @Test
    fun appTheme_noDefaultMaterialColors() {
        var primaryColor: Color? = null
        var secondaryColor: Color? = null
        
        composeTestRule.setContent {
            AppTheme {
                primaryColor = MaterialTheme.colorScheme.primary
                secondaryColor = MaterialTheme.colorScheme.secondary
                Text("Color Test")
            }
        }
        
        // Verify we're not using default Material 3 purple colors
        val materialPurple = Color(0xFF6750A4) // Default Material 3 primary
        val materialPurpleSecondary = Color(0xFF625B71) // Default Material 3 secondary
        
        assert(primaryColor != materialPurple) {
            "Should not use default Material 3 purple primary color"
        }
        assert(secondaryColor != materialPurpleSecondary) {
            "Should not use default Material 3 purple secondary color"
        }
        
        // Verify we're using our blue theme
        val expectedBlue = Color(0xFF1976D2)
        val expectedBlueGrey = Color(0xFF546E7A)
        
        assert(primaryColor == expectedBlue) {
            "Should use our custom blue primary color"
        }
        assert(secondaryColor == expectedBlueGrey) {
            "Should use our custom blue-grey secondary color"
        }
    }
}