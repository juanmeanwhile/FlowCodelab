package com.meanwhile.flowcodelab

import com.meanwhile.flowcodelab.fakes.FakeSharedFlowGetDaysUntilWeekendUseCase
import com.meanwhile.flowcodelab.stage0.UiState
import com.meanwhile.flowcodelab.stage1.OneUiState
import com.meanwhile.flowcodelab.stage1.Status
import com.meanwhile.flowcodelab.stage2.TwoEndViewModel
import com.meanwhile.flowcodelab.stage2.TwoStartViewModel
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
class StageTwoTest : BehaviorSpec({

    Dispatchers.setMain(UnconfinedTestDispatcher())
    val fakeUseCase = FakeSharedFlowGetDaysUntilWeekendUseCase()
    val tested = TwoStartViewModel(fakeUseCase)

    Given("a TwoStartViewModel") {
        When("use case is executed and return a number") {

            val ret = mutableListOf<UiState>()

            Then("uiState first emitted value is loading").config(coroutineTestScope = true) {
                val collectJob = launch(UnconfinedTestDispatcher(testCoroutineScheduler)) {
                    tested.uiState.toList(ret)
                }
                fakeUseCase.emit(99)
                collectJob.cancel()

                assertEquals(Status.LOADING, (ret[0] as? OneUiState)?.status)
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

        // This test considers that the previous When has been called, if executed alone uiState won't have th expeted values
        When("onRefresh is called") {
            val ret = mutableListOf<UiState>()

            Then("uiState first emitted value contains previously emitted value of Success and 99 days").config(coroutineTestScope = true) {
                val collectJob = launch(UnconfinedTestDispatcher(testCoroutineScheduler)) {
                    tested.uiState.toList(ret)
                }
                tested.onRefresh()
                fakeUseCase.emit(77)

                println(ret)
                collectJob.cancel()

                assertEquals(Status.SUCCESS, (ret[0] as? OneUiState)?.status)
                assertEquals(99, ret[0].daysUntilWeekend)
            }
            Then("uiState second emitted is Loading and -1 days") {
                assertEquals(Status.LOADING, (ret[1] as? OneUiState)?.status)
                assertEquals(-1, ret[1].daysUntilWeekend)
            }
            Then("uiState third emitted is success and 77 days") {
                assertEquals(Status.SUCCESS, (ret[2] as? OneUiState)?.status)
                assertEquals(77, ret[2].daysUntilWeekend)
            }
        }
    }
    //Test the whole flow from thebeginning
    Given("A TwoEndViewModel with Flow based use case") {
        val useCase = FakeSharedFlowGetDaysUntilWeekendUseCase()
        val tested = TwoStartViewModel(useCase)

        When("onRefresh is called") {
            val ret = mutableListOf<UiState>()

            Then("uiState first emitted value contains previously emitted value of Loading and -1 days").config(coroutineTestScope = true) {
                val collectJob = launch(UnconfinedTestDispatcher(testCoroutineScheduler)) {
                    tested.uiState.take(4).toList(ret)
                }

                // emit first value
                useCase.emit(99)
                tested.onRefresh()
                useCase.emit(77)

                collectJob.cancel()

                assertEquals(Status.LOADING, (ret[0] as? OneUiState)?.status)
                assertEquals(-1, ret[0].daysUntilWeekend)
            }
            Then("uiState second emitted is Success and 99 days") {
                assertEquals(Status.SUCCESS, (ret[1] as? OneUiState)?.status)
                assertEquals(99, ret[1].daysUntilWeekend)
            }
            Then("uiState third emitted is loading and -1 days") {
                assertEquals(Status.LOADING, (ret[2] as? OneUiState)?.status)
                assertEquals(-1, ret[2].daysUntilWeekend)
            }
            Then("uiState second emitted is Success and 77 days") {
                assertEquals(Status.SUCCESS, (ret[3] as? OneUiState)?.status)
                assertEquals(77, ret[3].daysUntilWeekend)
            }
        }
    }
})