package com.meanwhile.flowcodelab.stage4

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * Combine Data from local data source and a different remote data source
 */
class FourStartRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    // FIXME The returned flow won't react to changes in the items at LocalDataSource
    fun getDataFlow(): Flow<ItemsWithPromotion> = flow {
        val items = localDataSource.getItemsFlow().first()
        val promotion = remoteDataSource.getPromotion(items)
        val mergedData = mergeData(items, promotion)
        emit(mergedData)
    }

    private fun mergeData(items: List<BagItem>, promotion: Promotion?): ItemsWithPromotion {
        return ItemsWithPromotion(items, promotion)
    }
}

data class ItemsWithPromotion(val items: List<BagItem>, val promo: Promotion?)

data class BagItem(val id: String)

data class Promotion(val text: String)

interface LocalDataSource {
    fun getItemsFlow(): Flow<List<BagItem>>
}

interface RemoteDataSource {
    suspend fun getPromotion(items: List<BagItem>): Promotion?
}