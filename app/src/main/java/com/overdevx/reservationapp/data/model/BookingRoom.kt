package com.overdevx.reservationapp.data.model

data class BookingRoomResponse(
    val status: String,
    val message: String,
    val data: BookingRoom
)

data class BookingRoom(
    val booking_room_id : Int,
    val booking_id:Int,
    val room_id: Int,
    val days: Int,
    val Booking: BookingDate,
    val Room:RoomInfo,
)

data class BookingDate(
    val booking_date: String
)

data class RoomInfo(
    val room_number: String,
    val Building:BuildingRoom
)

data class BuildingRoom(
    val name: String
)