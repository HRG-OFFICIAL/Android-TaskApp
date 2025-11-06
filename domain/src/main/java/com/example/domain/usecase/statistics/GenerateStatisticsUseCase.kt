package com.example.domain.usecase.statistics

import com.example.domain.model.TaskStatistics
import com.example.domain.repository.StatisticsRepository
import javax.inject.Inject

class GenerateStatisticsUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(): TaskStatistics {
        return statisticsRepository.generateStatistics()
    }
}
