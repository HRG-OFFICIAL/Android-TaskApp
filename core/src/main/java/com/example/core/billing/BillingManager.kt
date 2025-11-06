package com.example.core.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener, BillingClientStateListener {
    
    private lateinit var billingClient: BillingClient
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()
    
    private val _purchases = MutableStateFlow<List<Purchase>>(emptyList())
    val purchases: StateFlow<List<Purchase>> = _purchases.asStateFlow()
    
    private val _subscriptionStatus = MutableStateFlow(SubscriptionStatus.FREE)
    val subscriptionStatus: StateFlow<SubscriptionStatus> = _subscriptionStatus.asStateFlow()
    
    init {
        initializeBillingClient()
    }
    
    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()
        
        billingClient.startConnection(this)
    }
    
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            _isReady.value = true
            queryPurchases()
        }
    }
    
    override fun onBillingServiceDisconnected() {
        _isReady.value = false
    }
    
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            _purchases.value = purchases
            updateSubscriptionStatus(purchases)
        }
    }
    
    fun queryPurchases() {
        if (!billingClient.isReady) return
        
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _purchases.value = purchases
                updateSubscriptionStatus(purchases)
            }
        }
    }
    
    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        if (!billingClient.isReady) return
        
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }
    
    fun queryProductDetails(productIds: List<String>, callback: (List<ProductDetails>) -> Unit) {
        if (!billingClient.isReady) return
        
        val productList = productIds.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        
        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                callback(productDetailsList)
            }
        }
    }
    
    private fun updateSubscriptionStatus(purchases: List<Purchase>) {
        val hasActiveSubscription = purchases.any { purchase ->
            purchase.products.any { productId ->
                productId == "premium_monthly" || productId == "premium_yearly"
            } && purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        }
        
        _subscriptionStatus.value = if (hasActiveSubscription) {
            SubscriptionStatus.PREMIUM
        } else {
            SubscriptionStatus.FREE
        }
    }
    
    fun isPremiumUser(): Boolean {
        return _subscriptionStatus.value == SubscriptionStatus.PREMIUM
    }
    
    fun cleanup() {
        billingClient.endConnection()
    }
}

enum class SubscriptionStatus {
    FREE, PREMIUM
}
