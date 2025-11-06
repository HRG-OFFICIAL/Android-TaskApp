package com.example.domain.usecase

import com.example.domain.model.AuthUser
import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): AuthUser {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(password.isNotBlank()) { "Password cannot be blank" }
        require(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { 
            "Invalid email format" 
        }
        require(password.length >= 6) { "Password must be at least 6 characters" }
        
        return authRepository.createAccount(email, password)
    }
}