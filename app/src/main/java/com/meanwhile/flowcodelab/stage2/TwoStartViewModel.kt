package com.meanwhile.flowcodelab.stage2

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
 * ViewModel which gets the number of days left until the weekend with:
 *  - loading, errors and success state
 *  - offer retry possibility
 *
 *  Our goal is again to get rid of the mutable flow as much as possible
 */
@HiltViewModel
class TwoStartViewModel @Inject constructor(val getDaysUntilWeekendUseCase: IGetDaysUntilWeekendUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(initUiState())

    /**
     * Single flow exposing ui state
     */
    val uiState: StateFlow<OneUiState> = _uiState // FIXME I don't want to be mutable, who knows who will emit what

    init {
        onRefresh()
    }

    /**
     * Refresh the information. Can be called from outside to refresh the information. A pull to refresh or an error/retry state are classic examples
     */
    fun onRefresh(){
        getDaysUntilTheWeekend() // FIXME everytime we call this method we are duplicating collectors.
    }

    private fun getDaysUntilTheWeekend() {
        viewModelScope.launch {
            _uiState.emit(OneUiState(-1, Status.LOADING))

            kotlin.runCatching {
                getDaysUntilWeekendUseCase().collect { days ->
                    println("Collector: $days")
                    _uiState.emit(OneUiState(days, Status.SUCCESS))
                }
            }.onFailure {
                _uiState.emit(OneUiState(-1, Status.ERROR))
            }
        }
    }

    private fun initUiState() = OneUiState(-1, Status.LOADING)
}