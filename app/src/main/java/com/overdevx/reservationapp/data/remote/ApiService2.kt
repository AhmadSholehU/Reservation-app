package com.overdevx.reservationapp.data.remote

import com.overdevx.reservationapp.data.model.BookingRequest
import com.overdevx.reservationapp.data.model.BookingResponse
import com.overdevx.reservationapp.data.model.BuildingResponse
import com.overdevx.reservationapp.data.model.LoginRequest
import com.overdevx.reservationapp.data.model.LoginResponse
import com.overdevx.reservationapp.data.model.MonitoringResponse
import com.overdevx.reservationapp.data.model.RoomResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService2 {

    @Headers("Content-Type: application/json")
    @POST("booking-rooms")
    suspend fun booking(@Body bookRequest: BookingRequest): Response<BookingResponse>

}

