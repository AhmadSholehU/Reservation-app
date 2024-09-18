package com.overdevx.reservationapp.data.repository

import android.util.Log
import com.overdevx.reservationapp.data.model.BookingRequest
import com.overdevx.reservationapp.data.model.BookingResponse
import com.overdevx.reservationapp.data.model.UpdateRoomsRequest
import com.overdevx.reservationapp.data.model.UpdateRoomsResponse
import com.overdevx.reservationapp.data.remote.ApiService
import com.overdevx.reservationapp.data.remote.ApiService2
import com.overdevx.reservationapp.utils.Resource
import javax.inject.Inject

class BookingRespository @Inject constructor(
   private val authenticateApiService: ApiService
) {
    suspend fun booking(room_id:Int, days:Int): Resource<BookingResponse> {
        return try{
            val response = authenticateApiService.booking(BookingRequest(room_id, days))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.ErrorMessage("Booking failed: No response body")
                }
            } else {
                Resource.ErrorMessage("Booking failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("RetrofitError", "Error: ${e.message}")
            Resource.Error(e)
        }
    }

    suspend fun updateRoomStatus(roomId: Int, statusId: Int): Resource<UpdateRoomsResponse> {
        return try {
            val request = UpdateRoomsRequest(status_id = statusId)
            val response = authenticateApiService.updateRoom(roomId, request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.ErrorMessage("Update room failed: No response body")
                }
            } else {
                Resource.ErrorMessage("Update room failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}