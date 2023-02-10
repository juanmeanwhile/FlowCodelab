package com.meanwhile.flowcodelab.stage0

data class ZeroUiState(
    override val daysUntilWeekend: Int
) : UiState

interface UiState {
    val daysUntilWeekend: Int
}