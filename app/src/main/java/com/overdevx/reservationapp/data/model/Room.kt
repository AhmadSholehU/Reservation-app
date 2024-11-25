package com.overdevx.reservationapp.data.model


data class RoomResponse(
    val status: String,
    val message: String,
    val data: List<Room>
)

data class Room(
    val room_id: Int,
    val room_number: String,
    val name: String,
    val building_name:String,
    val status_name: String,
    val room_type_id: Int,
)





