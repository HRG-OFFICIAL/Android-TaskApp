package com.example.data.repository

import com.example.domain.model.AuthUser
import com.example.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override val currentUser: Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.let { user ->
                AuthUser(
                    uid = user.uid,
                    displayName = user.displayName,
                    email = user.email,
                    photoUrl = user.photoUrl?.toString(),
                    isAnonymous = user.isAnonymous,
                    isEmailVerified = user.isEmailVerified,
                    providerId = user.providerData.firstOrNull()?.providerId,
                    creationTimestamp = user.metadata?.creationTimestamp,
                    lastSignInTimestamp = user.metadata?.lastSignInTimestamp
                )
            })
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInAnonymously(): AuthUser {
        val result = auth.signInAnonymously().await()
        val user = result.user ?: throw IllegalStateException("No user returned")
        return AuthUser(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl?.toString(),
            isAnonymous = user.isAnonymous,
            isEmailVerified = user.isEmailVerified,
            providerId = user.providerData.firstOrNull()?.providerId,
            creationTimestamp = user.metadata?.creationTimestamp,
            lastSignInTimestamp = user.metadata?.lastSignInTimestamp
        )
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("No user returned")
        return AuthUser(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl?.toString(),
            isAnonymous = user.isAnonymous,
            isEmailVerified = user.isEmailVerified,
            providerId = user.providerData.firstOrNull()?.providerId,
            creationTimestamp = user.metadata?.creationTimestamp,
            lastSignInTimestamp = user.metadata?.lastSignInTimestamp
        )
    }

    override suspend fun createAccount(email: String, password: String): AuthUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("No user returned")
        return AuthUser(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl?.toString(),
            isAnonymous = user.isAnonymous,
            isEmailVerified = user.isEmailVerified,
            providerId = user.providerData.firstOrNull()?.providerId,
            creationTimestamp = user.metadata?.creationTimestamp,
            lastSignInTimestamp = user.metadata?.lastSignInTimestamp
        )
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): AuthUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: throw IllegalStateException("No user returned")
        return AuthUser(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl?.toString(),
            isAnonymous = user.isAnonymous,
            isEmailVerified = user.isEmailVerified,
            providerId = user.providerData.firstOrNull()?.providerId,
            creationTimestamp = user.metadata?.creationTimestamp,
            lastSignInTimestamp = user.metadata?.lastSignInTimestamp
        )
    }

    override suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun signOut() { 
        auth.signOut() 
    }
}
