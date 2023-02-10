package com.meanwhile.flowcodelab.fakes

import com.meanwhile.flowcodelab.domain.IGetDaysUntilWeekendUseCase
import kotlinx.coroutines.flow.flow

class FakeFlowGetDaysUseCase : IGetDaysUntilWeekendUseCase{
    override fun invoke() = flow {
        emit(99)
    }
}