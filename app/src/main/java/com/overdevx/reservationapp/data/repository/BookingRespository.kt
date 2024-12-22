package com.overdevx.reservationapp.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.overdevx.reservationapp.data.model.BookingList
import com.overdevx.reservationapp.data.model.BookingListResponse
import com.overdevx.reservationapp.data.model.BookingListinitResponse
import com.overdevx.reservationapp.data.model.BookingRequest
import com.overdevx.reservationapp.data.model.BookingResponse
import com.overdevx.reservationapp.data.model.BookingRoom
import com.overdevx.reservationapp.data.model.BookingRoomResponse
import com.overdevx.reservationapp.data.model.BookingRoominit
import com.overdevx.reservationapp.data.model.History
import com.overdevx.reservationapp.data.model.KetersediaanResponse
import com.overdevx.reservationapp.data.model.RoomDataUpdate
import com.overdevx.reservationapp.data.model.UpdateBookingRequest
import com.overdevx.reservationapp.data.model.UpdateRoomsRequest
import com.overdevx.reservationapp.data.model.UpdateRoomsResponse
import com.overdevx.reservationapp.data.paging.BookingListPagingSource
import com.overdevx.reservationapp.data.paging.BookingRoomPagingSource
import com.overdevx.reservationapp.data.paging.HistoryListPagingSource
import com.overdevx.reservationapp.data.remote.ApiService
import com.overdevx.reservationapp.data.remote.ApiService2
import com.overdevx.reservationapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookingRespository @Inject constructor(
   private val authenticateApiService: ApiService
) {
    suspend fun booking(room_id:List<Int>, startDate:String,enddate:String): Resource<BookingResponse> {
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

    suspend fun updateRoomStatus(roomId:List<Int>, statusId: Int): Resource<UpdateRoomsResponse> {
        return try {
            val request = UpdateRoomsRequest(roomIds=roomId,roomData = RoomDataUpdate(statusId))
            val response = authenticateApiService.updateRoom(request)
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

    suspend fun getBookingRoombyId(roomId: Int): Resource<BookingListResponse> {
        return try {
            // Make the API call
            val response = authenticateApiService.getBookingRoomsbyId(roomId)

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

    suspend fun deleteBookingRoom(bookingRoomId:Int):Resource<BookingRoomResponse>{
        return try {
            val response = authenticateApiService.deleteBooking(bookingRoomId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body)
                } else {
                    Resource.ErrorMessage("Delete room failed: No response body")
                }
            } else {
                Resource.ErrorMessage("Delete room failed: ${response.message()}")
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

    suspend fun getKetersediaanBooking(roomId: Int): Resource<KetersediaanResponse> {
        return try {
            // Make the API call
            val response = authenticateApiService.getKetersediaanBooking(roomId)

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

    fun getBookingList(): Pager<Int,BookingRoominit> {
        return Pager(
            config = PagingConfig(
                pageSize = 4, // Jumlah item per halaman
                enablePlaceholders = false
            ),
            pagingSourceFactory = { BookingListPagingSource(authenticateApiService) }
        )
    }

    fun getBookingRooms(): Pager<Int,BookingList> {
        return Pager(
            config = PagingConfig(
                pageSize = 4, // Jumlah item per halaman
                enablePlaceholders = false
            ),
            pagingSourceFactory = { BookingRoomPagingSource(authenticateApiService) }
        )
    }



}