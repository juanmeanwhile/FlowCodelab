package com.meanwhile.flowcodelab

import com.meanwhile.flowcodelab.fakes.FakeSharedFlowGetDaysUntilWeekendUseCase
import com.meanwhile.flowcodelab.stage0.OneStartViewModel
import com.meanwhile.flowcodelab.stage0.UiState
import com.meanwhile.flowcodelab.stage1.OneUiState
import com.meanwhile.flowcodelab.stage1.Status
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
class StageOneTest: BehaviorSpec( {

    Dispatchers.setMain(UnconfinedTestDispatcher())
    val fakeUseCase = FakeSharedFlowGetDaysUntilWeekendUseCase()

    val testedStart = OneStartViewModel(fakeUseCase)

    Given("a OneStartViewModel"){
        When("use case is executed and return a number") {

           val ret = mutableListOf<UiState>()

            Then("uiState first emitted value is loading").config (coroutineTestScope = true) {
                val collectJob = launch(UnconfinedTestDispatcher(testCoroutineScheduler)) {
                    testedStart.uiState.toList(ret)
                }
                fakeUseCase.emit(99)
                assertEquals(Status.LOADING, (ret[0] as? OneUiState)?.status)

                collectJob.cancel()
            }
            Then("uiState first emitted value contains -1 days") {
                assertEquals(-1, ret[0].daysUntilWeekend)
            }
            Then("uiState second emitted value is Success") {
                assertEquals(Status.SUCCESS, (ret[1] as? OneUiState)?.status)
            }
            Then("uiState second emitted value contains 99 days") {
                assertEquals(99, ret[1].daysUntilWeekend)
            }
        }
    }
})