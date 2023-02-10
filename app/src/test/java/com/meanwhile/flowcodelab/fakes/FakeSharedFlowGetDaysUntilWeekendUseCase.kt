package com.meanwhile.flowcodelab.fakes

import com.meanwhile.flowcodelab.domain.IGetDaysUntilWeekendUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeSharedFlowGetDaysUntilWeekendUseCase: IGetDaysUntilWeekendUseCase {

    //Use Shared flow because we want not to have initial value
    private lateinit var _days : MutableSharedFlow<Int>

    override fun invoke(): Flow<Int> {
        _days = MutableSharedFlow(replay = 1, extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.DROP_LATEST)
        return _days
    }

    suspend fun emit(value: Int, delayMillis: Long? = null) {
        delayMillis?.let{
            delay(delayMillis)
        }
        _days.emit(value)
    }
}