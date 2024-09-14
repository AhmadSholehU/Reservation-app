package com.overdevx.reservationapp.data.remote

import com.overdevx.reservationapp.data.model.BuildingResponse
import com.overdevx.reservationapp.data.model.MonitoringResponse
import com.overdevx.reservationapp.data.model.RoomResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("rooms/buildings/{id}")
    suspend fun getRooms(@Path("id") buildingId: Int): RoomResponse

    @GET("buildingsMon")
    suspend fun getMonitoring(): MonitoringResponse

    @GET("buildings")
    suspend fun getBuilding(): BuildingResponse


}