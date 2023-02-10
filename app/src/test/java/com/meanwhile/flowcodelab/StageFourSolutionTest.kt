package com.meanwhile.flowcodelab

import com.meanwhile.flowcodelab.fakes.FakeLocalDataSource
import com.meanwhile.flowcodelab.fakes.FakeRemoteDataSource
import com.meanwhile.flowcodelab.stage4.BagItem
import com.meanwhile.flowcodelab.stage4.FourEndRepository
import com.meanwhile.flowcodelab.stage4.FourStartRepository
import com.meanwhile.flowcodelab.stage4.ItemsWithPromotion
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher


@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
class StageFourSolutionTest : BehaviorSpec({
    val localDataSource = FakeLocalDataSource()
    val remoteDataSource = FakeRemoteDataSource()

    Given("A FourStartRepository") {
        val tested = FourEndRepository(localDataSource, remoteDataSource)

        When("Local data source emits something, response is combined with remote") {
            val dataFlow = tested.getDataFlow()

            Then("data flow emits item with cake promotion").config(coroutineTestScope = true) {

                var ret = ItemsWithPromotion(listOf(), null)
                val collectJob = launch(UnconfinedTestDispatcher(testCoroutineScheduler)) {
                    ret = dataFlow.first()
                }

                localDataSource.emit(listOf(BagItem("a")))
                collectJob.cancel()

                assertEquals("a", ret.items.first().id)
                assertEquals("cake", ret.promo?.text)

            }
        }

        When("A local emits a second time"){
            val dataFlow = tested.getDataFlow()
            Then(" dataFlow emits the latest values values").config(coroutineTestScope = true) {

                var ret = ItemsWithPromotion(listOf(), null)
                val collectJob = launch(UnconfinedTestDispatcher(testCoroutineScheduler)) {
                    ret = dataFlow.take(2).last()
                }

                localDataSource.emit(listOf(BagItem("a")))
                localDataSource.emit(listOf(BagItem("b")))
                collectJob.cancel()

                assertEquals("b", ret.items.first().id)

            }
        }
    }
})