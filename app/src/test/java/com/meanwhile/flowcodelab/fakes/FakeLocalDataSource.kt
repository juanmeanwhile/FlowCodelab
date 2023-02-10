package com.meanwhile.flowcodelab.fakes

import com.meanwhile.flowcodelab.stage4.BagItem
import com.meanwhile.flowcodelab.stage4.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource : LocalDataSource {

    private val _dataFlow = MutableSharedFlow<List<BagItem>>()

    override fun getItemsFlow(): Flow<List<BagItem>> {
        return _dataFlow
    }

    suspend fun emit(items: List<BagItem>) {
        _dataFlow.emit(items)
    }


}