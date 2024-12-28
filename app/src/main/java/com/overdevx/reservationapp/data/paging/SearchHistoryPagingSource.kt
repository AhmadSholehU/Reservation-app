package com.overdevx.reservationapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.overdevx.reservationapp.data.model.BookingList
import com.overdevx.reservationapp.data.model.BookingRoominit
import com.overdevx.reservationapp.data.model.History
import com.overdevx.reservationapp.data.remote.ApiService

class SearchHistoryPagingSource(
    private val api: ApiService,
    private val searchTerm: String
) : PagingSource<Int, History>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, History> {
        return try {
            val page = params.key ?: 1
            val response = api.searchHistorylist(searchTerm,page)
            LoadResult.Page(
                data = response.data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, History>): Int? {
        return state.anchorPosition?.let { position ->
            val closestPage = state.closestPageToPosition(position)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }
    }
}
