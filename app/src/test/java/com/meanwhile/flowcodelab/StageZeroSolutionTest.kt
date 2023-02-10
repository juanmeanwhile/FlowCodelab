package com.meanwhile.flowcodelab

import com.meanwhile.flowcodelab.fakes.FakeSharedFlowGetDaysUntilWeekendUseCase
import com.meanwhile.flowcodelab.stage0.UiState
import com.meanwhile.flowcodelab.stage0.ZeroEndViewModel
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
class StageZeroSolutionTest : BehaviorSpec({

    Dispatchers.setMain(UnconfinedTestDispatcher())

    val fakeUseCase = FakeSharedFlowGetDaysUntilWeekendUseCase()
    val tested = ZeroEndViewModel(fakeUseCase)

    Given("a ZeroEndViewModel") {
        When("use case emits a int") {
            var ret = listOf<UiState>()

            Then("uiState initial value has -1 days").config(coroutineTestScope = true) {
                val collectJob = launch(UnconfinedTestDispatcher(testCoroutineScheduler)) {
                    ret = tested.uiState.take(2).toList()
                }
                fakeUseCase.emit(99)
                collectJob.cancel()

                assertEquals(-1, ret[0].daysUntilWeekend)
            }

            Then("emitted value in uiState contains the same int") {
                assertEquals(99, ret[1].daysUntilWeekend)
            }
        }
    }
})