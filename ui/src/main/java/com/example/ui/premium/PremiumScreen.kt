package com.example.ui.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.billing.PremiumFeature

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    viewModel: PremiumViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PremiumHeader()
        }
        
        item {
            SubscriptionPlans(
                plans = uiState.subscriptionPlans,
                selectedPlan = uiState.selectedPlan,
                onPlanSelected = viewModel::selectPlan
            )
        }
        
        item {
            PremiumFeatures(
                features = uiState.premiumFeatures,
                isPremium = uiState.isPremium
            )
        }
        
        item {
            SubscribeButton(
                isPremium = uiState.isPremium,
                isLoading = uiState.isLoading,
                onSubscribe = viewModel::subscribe,
                onManageSubscription = viewModel::manageSubscription
            )
        }
        
        item {
            BenefitsSection()
        }
    }
}

@Composable
private fun PremiumHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFFFD700)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Upgrade to Premium",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Unlock powerful features to boost your productivity",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SubscriptionPlans(
    plans: List<SubscriptionPlan>,
    selectedPlan: SubscriptionPlan?,
    onPlanSelected: (SubscriptionPlan) -> Unit
) {
    Column {
        Text(
            text = "Choose Your Plan",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            plans.forEach { plan ->
                SubscriptionPlanCard(
                    plan = plan,
                    isSelected = plan == selectedPlan,
                    onSelect = { onPlanSelected(plan) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SubscriptionPlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
        ) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = plan.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = plan.price,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = plan.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (plan.isPopular) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "Most Popular",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumFeatures(
    features: List<PremiumFeature>,
    isPremium: Boolean
) {
    Column {
        Text(
            text = "Premium Features",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        features.forEach { feature ->
            FeatureRow(
                feature = feature,
                isPremium = isPremium
            )
        }
    }
}

@Composable
private fun FeatureRow(
    feature: PremiumFeature,
    isPremium: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isPremium && feature.isPremium) Icons.Default.CheckCircle
            else if (feature.isPremium) Icons.Default.Lock
            else Icons.Default.Check,
            contentDescription = null,
            tint = if (isPremium && feature.isPremium) Color.Green
            else if (feature.isPremium) MaterialTheme.colorScheme.onSurfaceVariant
            else Color.Green
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = feature.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SubscribeButton(
    isPremium: Boolean,
    isLoading: Boolean,
    onSubscribe: () -> Unit,
    onManageSubscription: () -> Unit
) {
    if (isPremium) {
        OutlinedButton(
            onClick = onManageSubscription,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Manage Subscription")
        }
    } else {
        Button(
            onClick = onSubscribe,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Subscribe Now")
        }
    }
}

@Composable
private fun BenefitsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Why Upgrade?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val benefits = listOf(
                "🚀 Boost productivity with AI-powered insights",
                "👥 Collaborate with your team seamlessly",
                "📊 Get detailed analytics and reports",
                "🔒 Keep your data secure with cloud backup",
                "🎨 Personalize your experience with custom themes"
            )
            
            benefits.forEach { benefit ->
                Text(
                    text = benefit,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val price: String,
    val description: String,
    val isPopular: Boolean = false
)
