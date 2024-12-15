package com.overdevx.reservationapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.overdevx.reservationapp.data.model.BookingList
import com.overdevx.reservationapp.data.model.BookingRoominit
import com.overdevx.reservationapp.data.remote.ApiService

class BookingListPagingSource(
    private val apiService: ApiService
) : PagingSource<Int, BookingRoominit>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookingRoominit> {
        return try {
            val currentPage = params.key ?: 1 // Halaman pertama
            val response = apiService.getBookinglist(currentPage)

            LoadResult.Page(
                data = response.data ?: emptyList(),
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (currentPage < response.totalPages) currentPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, BookingRoominit>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}
