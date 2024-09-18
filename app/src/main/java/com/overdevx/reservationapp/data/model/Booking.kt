package com.overdevx.reservationapp.data.model


data class BookingResponse(
    val data: Booking,
    val message: String,
    val status: String
)
data class Booking(
  val booking_room_id:Int,
  val booking_id:Int,
  val room_id:Int,
  val days:Int,
)

data class BookingRequest(
    val room_id:Int,
    val days:Int,
)