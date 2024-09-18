package com.overdevx.reservationapp.data.repository

import android.util.Log
import com.overdevx.reservationapp.data.model.Room
import com.overdevx.reservationapp.data.remote.ApiService
import com.overdevx.reservationapp.utils.Resource
import javax.inject.Inject

class RoomRepository @Inject constructor(
   private val publicApiService: ApiService
) {
    suspend fun getRooms(buildingId:Int): Resource<List<Room>> {
        return try {
            val response = publicApiService.getRooms(buildingId)
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