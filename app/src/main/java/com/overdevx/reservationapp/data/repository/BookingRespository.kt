package com.overdevx.reservationapp.data.repository

import android.util.Log
import com.overdevx.reservationapp.data.model.BookingList
import com.overdevx.reservationapp.data.model.BookingRequest
import com.overdevx.reservationapp.data.model.BookingResponse
import com.overdevx.reservationapp.data.model.BookingRoom
import com.overdevx.reservationapp.data.model.BookingRoomResponse
import com.overdevx.reservationapp.data.model.KetersediaanResponse
import com.overdevx.reservationapp.data.model.UpdateBookingRequest
import com.overdevx.reservationapp.data.model.UpdateRoomsRequest
import com.overdevx.reservationapp.data.model.UpdateRoomsResponse
import com.overdevx.reservationapp.data.remote.ApiService
import com.overdevx.reservationapp.data.remote.ApiService2
import com.overdevx.reservationapp.utils.Resource
import javax.inject.Inject

class BookingRespository @Inject constructor(
   private val authenticateApiService: ApiService
) {
    suspend fun booking(room_id:Int, startDate:String,enddate:String): Resource<BookingResponse> {
        return try{
            val response = authenticateApiService.booking(BookingRequest(room_id, startDate,enddate))
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

    suspend fun getBookingRoom(roomId: Int): Resource<BookingRoomResponse> {
        return try {
            // Make the API call
            val response = authenticateApiService.getBookingRoom(roomId)

            // Check the API response status
            if (response.status == "success") {
                // Return the data if available
                val body = response.data
                if (body != null) {
                    Resource.Success(response)  // Return the single BookingRoomResponse
                } else {
                    Resource.ErrorMessage("Fetching booking room failed: No response body")
                }
            } else {
                Resource.ErrorMessage("Fetching booking room failed: ${response.message}")
            }
        } catch (e: Exception) {
            // Handle any exceptions
            Resource.Error(e)
        }
    }


    suspend fun updateBookingRoom(bookingRoomId:Int,start_date: String,end_date:String):Resource<BookingRoomResponse>{
        return try {
            val request = UpdateBookingRequest(start_date,end_date)
            val response = authenticateApiService.updateBooking(bookingRoomId, request)
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

    suspend fun getKetersediaan(roomId: Int): Resource<KetersediaanResponse> {
        return try {
            // Make the API call
            val response = authenticateApiService.getKetersediaan(roomId)

            // Check the API response status
            if (response.status == "success") {
                // Return the data if available
                val body = response.data
                if (body != null) {
                    Resource.Success(response)
                } else {
                    Resource.ErrorMessage("Fetching booking room failed: No response body")
                }
            } else {
                Resource.ErrorMessage("Fetching booking room failed: ${response.message}")
            }
        } catch (e: Exception) {
            // Handle any exceptions
            Resource.Error(e)
        }
    }

    suspend fun getBookingList(): Resource<List<BookingList>> {
        return try {
            val response = authenticateApiService.getBookinglist()
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