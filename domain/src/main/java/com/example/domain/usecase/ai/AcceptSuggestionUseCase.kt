package com.example.domain.usecase.ai

import com.example.domain.repository.AIRepository
import javax.inject.Inject

class AcceptSuggestionUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(suggestionId: String) {
        aiRepository.acceptSuggestion(suggestionId)
    }
}
