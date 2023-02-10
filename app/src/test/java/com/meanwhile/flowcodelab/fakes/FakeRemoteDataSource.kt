package com.meanwhile.flowcodelab.fakes

import com.meanwhile.flowcodelab.stage4.BagItem
import com.meanwhile.flowcodelab.stage4.Promotion
import com.meanwhile.flowcodelab.stage4.RemoteDataSource

class FakeRemoteDataSource: RemoteDataSource {
    override suspend fun getPromotion(items: List<BagItem>): Promotion? {
        return Promotion(text = "cake")
    }
}