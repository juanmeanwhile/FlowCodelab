package com.meanwhile.flowcodelab

import com.meanwhile.flowcodelab.fakes.FakeSharedFlowGetDaysUntilWeekendUseCase
import com.meanwhile.flowcodelab.stage0.UiState
import com.meanwhile.flowcodelab.stage0.ZeroStartViewModel
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class StageZeroTest: BehaviorSpec({

    Dispatchers.setMain(UnconfinedTestDispatcher())
    val fakeUseCase = FakeSharedFlowGetDaysUntilWeekendUseCase()
    val testedStart = ZeroStartViewModel(fakeUseCase)

    Given("a ZeroStartViewModel"){
        When("use case emits a int"){
            var ret= listOf<UiState>()

            Then("First emitted value in uiState has days default value").config(coroutineTestScope = true){
                val collectJob = launch(UnconfinedTestDispatcher(testCoroutineScheduler)) {
                    ret = testedStart.uiState.take(2).toList()
                }
                fakeUseCase.emit(99)
                collectJob.cancel()

                assertEquals(-1, ret[0].daysUntilWeekend)
            }

            Then("emitted value in uiState contains days from the use case"){
                assertEquals(99, ret[1].daysUntilWeekend)
            }
        }
    }
})