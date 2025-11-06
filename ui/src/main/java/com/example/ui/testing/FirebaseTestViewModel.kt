package com.example.ui.testing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.testing.FirebaseTestRunner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseTestViewModel @Inject constructor(
    private val firebaseTestRunner: FirebaseTestRunner
) : ViewModel() {

    private val _uiState = MutableStateFlow(FirebaseTestUiState())
    val uiState: StateFlow<FirebaseTestUiState> = _uiState.asStateFlow()

    /**
     * Run the Firebase connection test and populate sample data
     */
    fun runFirebaseTest() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                testResults = listOf(
                    TestResult("🚀 Starting Firebase test...", TestResultType.INFO)
                )
            )

            try {
                val success = firebaseTestRunner.runFirebaseTest()
                
                val results = if (success) {
                    listOf(
                        TestResult("🚀 Starting Firebase test...", TestResultType.INFO),
                        TestResult("✅ Firebase connection successful", TestResultType.SUCCESS),
                        TestResult("✅ Sample users created", TestResultType.SUCCESS),
                        TestResult("✅ Sample tasks populated", TestResultType.SUCCESS),
                        TestResult("✅ Data verification completed", TestResultType.SUCCESS),
                        TestResult("🎉 Firebase test completed successfully!", TestResultType.SUCCESS)
                    )
                } else {
                    listOf(
                        TestResult("🚀 Starting Firebase test...", TestResultType.INFO),
                        TestResult("❌ Firebase test failed", TestResultType.ERROR),
                        TestResult("Please check your Firebase configuration", TestResultType.ERROR)
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    testResults = results,
                    error = if (!success) "Firebase test failed" else null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    testResults = listOf(
                        TestResult("🚀 Starting Firebase test...", TestResultType.INFO),
                        TestResult("❌ Firebase test failed: ${e.message}", TestResultType.ERROR)
                    ),
                    error = e.message
                )
            }
        }
    }

    /**
     * Clean up test data from Firebase
     */
    fun cleanupTestData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                testResults = _uiState.value.testResults + TestResult(
                    "🧹 Cleaning up test data...", 
                    TestResultType.INFO
                )
            )

            try {
                firebaseTestRunner.cleanupDemoData()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    testResults = _uiState.value.testResults + TestResult(
                        "✅ Test data cleanup completed", 
                        TestResultType.SUCCESS
                    )
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    testResults = _uiState.value.testResults + TestResult(
                        "❌ Cleanup failed: ${e.message}", 
                        TestResultType.ERROR
                    )
                )
            }
        }
    }

    /**
     * Clear test results
     */
    fun clearResults() {
        _uiState.value = _uiState.value.copy(
            testResults = emptyList(),
            error = null
        )
    }
}