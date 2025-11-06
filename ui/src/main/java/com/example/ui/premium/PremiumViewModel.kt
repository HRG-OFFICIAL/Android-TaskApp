package com.example.ui.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.billing.BillingManager
import com.example.core.billing.PremiumFeatureManager
import com.example.core.billing.PremiumFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val billingManager: BillingManager,
    private val premiumFeatureManager: PremiumFeatureManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PremiumUiState())
    val uiState: StateFlow<PremiumUiState> = _uiState.asStateFlow()
    
    init {
        setupSubscriptionPlans()
        loadPremiumFeatures()
    }
    
    private fun setupSubscriptionPlans() {
        val plans = listOf(
            SubscriptionPlan(
                id = "free",
                name = "Free",
                price = "$0/month",
                description = "Basic features"
            ),
            SubscriptionPlan(
                id = "premium_monthly",
                name = "Premium",
                price = "$9.99/month",
                description = "All features",
                isPopular = true
            ),
            SubscriptionPlan(
                id = "premium_yearly",
                name = "Premium Yearly",
                price = "$99.99/year",
                description = "All features + 2 months free"
            )
        )
        
        _uiState.value = _uiState.value.copy(
            subscriptionPlans = plans,
            selectedPlan = plans[1] // Default to monthly premium
        )
    }
    
    private fun loadPremiumFeatures() {
        val features = PremiumFeature.values().toList()
        _uiState.value = _uiState.value.copy(
            premiumFeatures = features
        )
    }
    
    fun selectPlan(plan: SubscriptionPlan) {
        _uiState.value = _uiState.value.copy(selectedPlan = plan)
    }
    
    fun subscribe() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // This would typically launch the billing flow
                // For now, we'll just simulate success
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isPremium = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun manageSubscription() {
        // This would typically open the subscription management screen
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class PremiumUiState(
    val subscriptionPlans: List<SubscriptionPlan> = emptyList(),
    val selectedPlan: SubscriptionPlan? = null,
    val premiumFeatures: List<PremiumFeature> = emptyList(),
    val isPremium: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
