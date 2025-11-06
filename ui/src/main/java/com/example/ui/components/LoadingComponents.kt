package com.example.ui.components

/**
 * Loading Components Module
 * 
 * This module provides enhanced button components with individual loading state management.
 * These components address the UI requirements for independent loading states per button,
 * ensuring that only the clicked button shows a loading indicator while other buttons
 * remain interactive.
 * 
 * Key Features:
 * - Individual loading state management per button instance
 * - Proper loading indicator sizing and positioning
 * - Maintains button dimensions during loading states
 * - Supports all Material 3 button variants
 * - Accessible loading indicators with appropriate colors
 * 
 * Components included:
 * - LoadingButton: Enhanced Button with individual loading state
 * - LoadingOutlinedButton: Enhanced OutlinedButton with individual loading state
 * - LoadingTextButton: Enhanced TextButton with individual loading state
 * - LoadingIconButton: Enhanced IconButton with individual loading state
 * - LoadingFilledIconButton: Enhanced FilledIconButton with individual loading state
 * - LoadingOutlinedIconButton: Enhanced OutlinedIconButton with individual loading state
 * 
 * Usage Requirements:
 * - Requirements 1.1: Only clicked buttons show loading indicators
 * - Requirements 1.2: Other buttons remain interactive during loading
 * - Requirements 1.3: Loading indicators are removed when operations complete
 * - Requirements 1.4: Each UI component maintains independent loading state
 */

// Re-export all loading components for convenient importing
// This allows consumers to import all components with:
// import com.example.ui.components.*