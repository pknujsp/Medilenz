package com.android.mediproject.core.data.remote.recallsuspension

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.android.mediproject.core.common.DATA_GO_KR_PAGE_SIZE
import com.android.mediproject.core.model.remote.recall.RecallSuspensionListResponse
import com.android.mediproject.core.network.datasource.penalties.recallsuspension.RecallSuspensionDataSource
import com.android.mediproject.core.network.datasource.penalties.recallsuspension.RecallSuspensionListDataSourceImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecallSuspensionRepositoryImpl @Inject constructor(
    private val recallSuspensionDataSource: RecallSuspensionDataSource,
    private val recallSuspensionListDataSource: RecallSuspensionListDataSourceImpl,
) : RecallSuspensionRepository {

    override suspend fun getRecallDisposalList(): Flow<PagingData<RecallSuspensionListResponse.Body.Item.Item>> =
        Pager(config = PagingConfig(pageSize = DATA_GO_KR_PAGE_SIZE), pagingSourceFactory = {
            recallSuspensionListDataSource
        }).flow

    override suspend fun getRecentRecallDisposalList(
        pageNo: Int, numOfRows: Int
    ): Result<List<RecallSuspensionListResponse.Body.Item.Item>> = recallSuspensionDataSource.getRecallSuspensionList(
        pageNo, numOfRows
    ).map {
        it.body.items.map { item ->
            item.item
        }
    }

    override suspend fun getDetailRecallSuspension(
        company: String?, product: String?
    ) = recallSuspensionDataSource.getDetailRecallSuspensionInfo(
        company = company, product = product
    ).map {
        it.body.items.first().item
    }

}