package com.overdevx.reservationapp.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.overdevx.reservationapp.data.model.History
import com.overdevx.reservationapp.data.model.Monitoring
import com.overdevx.reservationapp.data.paging.HistoryListPagingSource
import com.overdevx.reservationapp.data.remote.ApiService
import com.overdevx.reservationapp.utils.Resource
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getMonitoring(): Resource<List<History>> {
        return try {
            val response = apiService.getHistory()
            // Cek apakah response sukses dan status adalah 'success'
            if (response.isSuccessful && response.body()?.status == "success") {
                val data = response.body()?.data
                if (data != null) {
                    // Jika data tidak null, kembalikan sebagai Resource.Success
                    Resource.Success(data)
                } else {
                    // Jika data null, kembalikan error message yang sesuai
                    Resource.ErrorMessage("Data is null")
                }
            } else {
                // Jika status bukan 'success' atau tidak ada body, kembalikan pesan error
                val errorMessage = response.body()?.message ?: "Unknown error"
                Resource.ErrorMessage(errorMessage)
            }
        } catch (e: Exception) {
            // Tangani exception dan tampilkan log error
            Log.e("RetrofitError", "Error: ${e.message}")
            Resource.Error(e)
        }

    }

    fun getHistoryList(): Pager<Int, History> {
        return Pager(
            config = PagingConfig(
                pageSize = 5, // Jumlah item per halaman
                enablePlaceholders = false
            ),
            pagingSourceFactory = { HistoryListPagingSource(apiService) }
        )
    }
}