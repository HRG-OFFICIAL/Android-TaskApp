package com.example.domain.usecase

import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String) {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { 
            "Invalid email format" 
        }
        
        authRepository.resetPassword(email)
    }
}