package com.example.domain.repository

import com.example.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<AuthUser?>
    suspend fun signInAnonymously(): AuthUser
    suspend fun signInWithEmail(email: String, password: String): AuthUser
    suspend fun createAccount(email: String, password: String): AuthUser
    suspend fun signInWithGoogleIdToken(idToken: String): AuthUser
    suspend fun resetPassword(email: String)
    suspend fun signOut()
}
