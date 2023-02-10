package com.meanwhile.flowcodelab.stage0

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meanwhile.flowcodelab.domain.GetDaysUntilWeekendUseCase
import com.meanwhile.flowcodelab.domain.IGetDaysUntilWeekendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Simple ViewModel which executes a use case when is initialized and emit its result to the ui
 */
@HiltViewModel
class ZeroStartViewModel @Inject constructor(val getDaysUntilWeekendUseCase: IGetDaysUntilWeekendUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(ZeroUiState(daysUntilWeekend = -1))

    /**
     * Single flow exposing ui state
     */
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            getDaysUntilWeekendUseCase().collect { days ->
                _uiState.emit(generateUiState(days))
            }
        }
    }

    private fun generateUiState(daysUntilWeekend: Int): ZeroUiState {
        return ZeroUiState(daysUntilWeekend)
    }
}