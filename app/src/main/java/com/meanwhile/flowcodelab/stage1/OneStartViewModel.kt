package com.meanwhile.flowcodelab.stage0

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meanwhile.flowcodelab.domain.IGetDaysUntilWeekendUseCase
import com.meanwhile.flowcodelab.stage1.OneUiState
import com.meanwhile.flowcodelab.stage1.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Simple ViewModel which executes a use case when is initialized and emit its result to the ui
 * but has Loading, error and  success state
 */
@HiltViewModel
class OneStartViewModel @Inject constructor(val getDaysUntilWeekendUseCase: IGetDaysUntilWeekendUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(initUiState())

    /**
     * Single flow exposing ui state
     */
    val uiState: StateFlow<OneUiState> = _uiState // FIXME I don't want to be mutable, who knows who will emit what from where

    init {
        getDaysUntilTheWeekend()
    }

    private fun getDaysUntilTheWeekend() {
        viewModelScope.launch {
            _uiState.emit(OneUiState(-1, Status.LOADING))

            kotlin.runCatching {
                getDaysUntilWeekendUseCase().collect { days ->
                    _uiState.emit(OneUiState(days, Status.SUCCESS))
                }
            }.onFailure {
                _uiState.emit(OneUiState(-1, Status.ERROR))
            }
        }
    }


    private fun initUiState() = OneUiState(-1, Status.SUCCESS)
}