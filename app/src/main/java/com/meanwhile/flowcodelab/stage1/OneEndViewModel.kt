package com.meanwhile.flowcodelab.stage1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meanwhile.flowcodelab.domain.IGetDaysUntilWeekendUseCase
import com.meanwhile.flowcodelab.stage0.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * - No mutable state, the future is bullet proof against someone emitting there
 */
@HiltViewModel
class OneEndViewModel @Inject constructor(getDaysUntilWeekendUseCase: IGetDaysUntilWeekendUseCase) : ViewModel() {

    /**
     * Single flow exposing ui state
     */
    val uiState: StateFlow<UiState> = getDaysUntilWeekendUseCase()
        .map { days ->
            OneUiState(days, Status.SUCCESS)
        }.catch {
            OneUiState(-1, Status.ERROR)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), OneUiState(-1, Status.LOADING))
}