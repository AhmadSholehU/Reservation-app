package com.overdevx.reservationapp.data.remote

import com.overdevx.reservationapp.data.model.BookingRequest
import com.overdevx.reservationapp.data.model.BookingResponse
import com.overdevx.reservationapp.data.model.BookingRoomResponse
import com.overdevx.reservationapp.data.model.BuildingResponse
import com.overdevx.reservationapp.data.model.HistoryResponse
import com.overdevx.reservationapp.data.model.LoginRequest
import com.overdevx.reservationapp.data.model.LoginResponse
import com.overdevx.reservationapp.data.model.MonitoringResponse
import com.overdevx.reservationapp.data.model.RoomResponse
import com.overdevx.reservationapp.data.model.UpdateBookingRequest
import com.overdevx.reservationapp.data.model.UpdateRoomsRequest
import com.overdevx.reservationapp.data.model.UpdateRoomsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("rooms/buildings/{id}")
    suspend fun getRooms(@Path("id") buildingId: Int): RoomResponse

    @GET("buildingsMon")
    suspend fun getMonitoring(): MonitoringResponse

    @GET("buildings")
    suspend fun getBuilding(): BuildingResponse

    @Headers("Content-Type: application/json")
    @POST("booking-rooms")
    suspend fun booking(@Body bookRequest: BookingRequest): Response<BookingResponse>

    @Headers("Content-Type: application/json")
    @PUT("rooms/{id}")
    suspend fun updateRoom(
        @Path("id") roomId: Int,
        @Body updateRoomsRequest: UpdateRoomsRequest
    ): Response<UpdateRoomsResponse>

    @Headers("Content-Type: application/json")
    @PUT("booking-rooms/{id}")
    suspend fun updateBooking(
        @Path("id") roomId: Int,
        @Body updateRoomsRequest: UpdateBookingRequest
    ): Response<BookingRoomResponse>

    @GET("booking-rooms/room/{id}")
    suspend fun getBookingRoom(@Path("id") roomId: Int): BookingRoomResponse

    @GET("history")
    suspend fun getHistory(): Response<HistoryResponse>
}

