package com.meanwhile.flowcodelab.stage1

import com.meanwhile.flowcodelab.stage0.UiState

data class OneUiState(
    override val daysUntilWeekend: Int,
    val status: Status
) : UiState

enum class Status {
    LOADING, ERROR, SUCCESS
}
