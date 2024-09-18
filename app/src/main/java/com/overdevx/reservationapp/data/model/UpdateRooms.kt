package com.overdevx.reservationapp.data.model


data class UpdateRoomsResponse(
    val data: UpdateRooms,
    val message: String,
    val status: String
)
data class UpdateRooms (
    val room_id: Int,
    val room_number: String,
    val building_id: Int,
    val room_type_id: Int,
    val status_id: Int
)

data class UpdateRoomsRequest(
    val status_id: Int
)