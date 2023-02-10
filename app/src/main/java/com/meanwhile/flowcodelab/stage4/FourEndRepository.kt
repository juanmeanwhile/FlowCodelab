package com.meanwhile.flowcodelab.stage4

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest

/**
 * Combine Data from local data source and a different remote data source.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FourEndRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {

    fun getDataFlow() = localDataSource.getItemsFlow().mapLatest { items -> // previous executing lambda is cancelled if still ongoing
        val promotion = remoteDataSource.getPromotion(items)
        val mergedData = mergeData(items, promotion)
        mergedData
    }

    private fun mergeData(items: List<BagItem>, promotion: Promotion?): ItemsWithPromotion {
        return ItemsWithPromotion(items, promotion)
    }
}