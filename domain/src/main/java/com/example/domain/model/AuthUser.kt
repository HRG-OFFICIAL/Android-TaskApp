package com.example.domain.model

data class AuthUser(
    val uid: String,
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val isEmailVerified: Boolean = false,
    val providerId: String? = null,
    val creationTimestamp: Long? = null,
    val lastSignInTimestamp: Long? = null,
)
