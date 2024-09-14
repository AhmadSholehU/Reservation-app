package com.overdevx.reservationapp.data.repository

import android.util.Log
import com.overdevx.reservationapp.data.model.Monitoring
import com.overdevx.reservationapp.data.remote.ApiService
import com.overdevx.reservationapp.utils.Resource
import javax.inject.Inject

class MonitoringRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getMonitoring(): Resource<List<Monitoring>> {
        return try {
            val response = apiService.getMonitoring()
            if (response.status == "success") {
                Resource.Success(response.data)
            } else {
                Resource.ErrorMessage(response.message)
            }
        } catch (e: Exception) {
            Log.e("RetrofitError", "Error: ${e.message}")
            Resource.Error(e)
        }



    }
}