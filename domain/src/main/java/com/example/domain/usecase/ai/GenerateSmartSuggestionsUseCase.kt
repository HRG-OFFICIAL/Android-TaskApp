package com.example.domain.usecase.ai

import com.example.domain.model.SmartSuggestion
import com.example.domain.repository.AIRepository
import javax.inject.Inject

class GenerateSmartSuggestionsUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(taskId: String): List<SmartSuggestion> {
        return aiRepository.generateSuggestions(taskId)
    }
}
