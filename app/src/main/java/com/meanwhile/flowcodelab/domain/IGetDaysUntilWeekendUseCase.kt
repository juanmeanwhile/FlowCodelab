package com.meanwhile.flowcodelab.domain

import kotlinx.coroutines.flow.Flow

interface IGetDaysUntilWeekendUseCase {
    operator fun invoke(): Flow<Int>
}