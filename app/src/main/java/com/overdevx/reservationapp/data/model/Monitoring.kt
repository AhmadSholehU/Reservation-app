package com.overdevx.reservationapp.data.model

data class MonitoringResponse(
    val message: String,
    val status: String,
    val data: List<Monitoring>
)


data class RoomData(
    val room_name: String,
    val room_status: String,
    val room_type: String
)

data class RoomStatus(
    val available: RoomCount,
    val not_available: RoomCount,
    val booked: RoomCount
)

data class RoomCount(
    val count: Int
)

data class Monitoring(
    val building_id: Int,
    val building_name: String,
    val room_status: RoomStatus,
    val rooms: List<RoomData>
)

