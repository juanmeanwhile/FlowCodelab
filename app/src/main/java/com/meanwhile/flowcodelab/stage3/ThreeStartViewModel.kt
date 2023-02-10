package com.meanwhile.flowcodelab.stage3

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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Everytime we are in loading, we are loosing the current days and returning -1.
 * If we were in a Pull To refresh loading, this mean that everytime the user pull we remove the content and show empty screen instead.
 * Our goal is to keep the content state when loading.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ThreeStartViewModel @Inject constructor(getDaysUntilWeekendUseCase: IGetDaysUntilWeekendUseCase) : ViewModel() {

    /**
     * Trigger for executing use case again.
     * We need Shared Flow because StateFlow includes [distinctUntilChanged()], which will skip repeated values
     */
    private val _getDaysTrigger = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     * Single flow exposing ui state
     */
    val uiState: StateFlow<UiState> = _getDaysTrigger.flatMapLatest {
        getDaysUntilWeekendUseCase()
            .map { days ->
                OneUiState(days, Status.SUCCESS)
            }.onStart {
                emit(OneUiState(-1, Status.LOADING)) // FIXME when loading, we always return -1 in days but the user want's to see the previous content
            }.catch {
                emit(OneUiState(-1, Status.ERROR))
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
            _getDaysTrigger.emit(Unit)
        }
    }
}