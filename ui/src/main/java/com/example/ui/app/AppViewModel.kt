package com.example.ui.app

import androidx.lifecycle.ViewModel
import com.example.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    val user = authRepository.currentUser
}
