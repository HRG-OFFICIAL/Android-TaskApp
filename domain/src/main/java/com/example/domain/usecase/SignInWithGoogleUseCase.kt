package com.example.domain.usecase

import com.example.domain.model.AuthUser
import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(idToken: String): AuthUser {
        require(idToken.isNotBlank()) { "ID token cannot be blank" }
        return authRepository.signInWithGoogleIdToken(idToken)
    }
}