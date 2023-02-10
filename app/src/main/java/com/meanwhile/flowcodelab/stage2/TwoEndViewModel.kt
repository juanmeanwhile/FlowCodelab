package com.meanwhile.flowcodelab.stage2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meanwhile.flowcodelab.domain.IGetDaysUntilWeekendUseCase
import com.meanwhile.flowcodelab.stage0.UiState
import com.meanwhile.flowcodelab.stage1.OneUiState
import com.meanwhile.flowcodelab.stage1.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * - We needed a mutable, but it's scope is reduced to it's minimum and it's purpose really clear.
 * - Everytime the trigger emits a new value:
 *      * flatMapLatest will cancel previous lambda if is still being executed (like if a request is in progress or a heavy operation like sorting a few thousand products)
 *      * flatMapLatest will return a new flow.
 *      * onStart starts that new flow with the value we say, in this case Loading.
 *
 * - Flatmap latest will cancel in progress execution of previous lambda, freeing resources if for any reason retry is triggered several times in little time
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TwoEndViewModel @Inject constructor(getDaysUntilWeekendUseCase: IGetDaysUntilWeekendUseCase) : ViewModel() {

    /**
     * Trigger for executing use case again.
     * We need Shared Flow because StateFlow includes [distinctUntilChanged()], which will skip repeated values
     */
    private val _getDaysTrigger = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     * Single flow exposing ui state
     */
    val uiState: StateFlow<OneUiState> = _getDaysTrigger.flatMapLatest { // creates a new flow everytime _getDaysTrigger emits something. THis flow is created in the labda.
        getDaysUntilWeekendUseCase()
            .map { days ->
                OneUiState(days, Status.SUCCESS)
            }.onStart { // emit [Status.Loading] everytime we refresh
                emit(OneUiState(-1, Status.LOADING))
            }.catch {
                emit(OneUiState(-1, Status.ERROR))
            }.onEach {
                println("each: $it")
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), OneUiState(-1, Status.LOADING))

    init {
        onRefresh()
    }

    /**
     * Refresh data exposed
     */
    public fun onRefresh(){
        viewModelScope.launch {
            println("refresh")
            _getDaysTrigger.emit(Unit)
        }
    }
}