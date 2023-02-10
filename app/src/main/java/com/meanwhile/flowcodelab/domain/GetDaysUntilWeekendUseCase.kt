package com.meanwhile.flowcodelab.domain

import java.time.DayOfWeek
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TIME_LIMIT = 2500L

class GetDaysUntilWeekendUseCase @Inject constructor(): IGetDaysUntilWeekendUseCase{

    override operator fun invoke(): Flow<Int> = flow {
        while (true) {
            delay(TIME_LIMIT)
            val currentTime = LocalDateTime.now()
            emit(DayOfWeek.SATURDAY.value - currentTime.dayOfWeek.value)
            delay(TIME_LIMIT)
        }
    }
}