package com.meanwhile.flowcodelab.stage0

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meanwhile.flowcodelab.domain.IGetDaysUntilWeekendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Goodbye _uiState. No mutable state is present, less problems for the future
 * Removed collect + emit
 * getDaysUntilWeekendUseCase is no longer a val (cannot be triggered)
 */
@HiltViewModel
class ZeroEndViewModel @Inject constructor(getDaysUntilWeekendUseCase: IGetDaysUntilWeekendUseCase) : ViewModel() {

    /**
     * Single flow exposing ui state
     */
    val uiState: StateFlow<UiState> = getDaysUntilWeekendUseCase()
        .map { days ->
            generateUiState(days)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ZeroUiState(-1))

    private fun generateUiState(daysUntilWeekend: Int): ZeroUiState {
        return ZeroUiState(daysUntilWeekend)
    }
}