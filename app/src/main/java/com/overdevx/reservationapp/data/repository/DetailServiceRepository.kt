package com.overdevx.reservationapp.data.repository

import android.util.Log
import com.overdevx.reservationapp.data.model.DetailService
import com.overdevx.reservationapp.data.remote.ApiService
import com.overdevx.reservationapp.utils.Resource
import javax.inject.Inject

class DetailServiceRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getDetailService(): Resource<List<DetailService>> {
        return try {
            val response = apiService.getDetailService()
            // Cek apakah response sukses dan status adalah 'success'
            if (response.isSuccessful && response.body()?.status == "sukses") {
                val data = response.body()?.data
                Log.d("RetrofitSuccess","$data")
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
}