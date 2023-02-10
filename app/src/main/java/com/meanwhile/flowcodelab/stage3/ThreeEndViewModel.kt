package com.meanwhile.flowcodelab.stage3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meanwhile.flowcodelab.domain.GetDaysUntilWeekendUseCase
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
 * ViewModel offering a trigger mechanism to execute our use case again.
 * When returning loading state, it still preserves the previous days value so it can be displayed to the user along with loading.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ThreeEndViewModel @Inject constructor(getDaysUntilWeekendUseCase: IGetDaysUntilWeekendUseCase) : ViewModel() {

    /**
     * Trigger for executing use case again.
     * We need Shared Flow because StateFlow includes [distincUntilChanged()], which will skip repeated values
     */
    private val _getDaysTrigger = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     * Single flow exposing ui state
     */
    val uiState: StateFlow<UiState> = _getDaysTrigger.flatMapLatest {

        val currentDays = getCurrentState().daysUntilWeekend

        getDaysUntilWeekendUseCase()
            .map { days ->
                OneUiState(days, Status.SUCCESS)
            }.onStart {
                emit(OneUiState(currentDays, Status.LOADING))
            }.catch {
                emit(OneUiState(-1, Status.ERROR))
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), OneUiState(-1, Status.LOADING))

    // We need a private method for this because the compiler won't let us access uiState inside it's lambda.
    private fun getCurrentState() = uiState.value as OneUiState
    // TODO check this works :)

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