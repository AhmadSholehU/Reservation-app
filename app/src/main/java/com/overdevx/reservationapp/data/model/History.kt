package com.overdevx.reservationapp.data.model

data class HistoryResponse(
    val status: String,
    val message: String,
    val data: List<History>
)
data class History(
    val booking_room_id: Int,
    val booking_id: Int,
    val room_id: Int,
    val days: Int,
    val detail: BookingDetail
)

data class BookingDetail(
    val room_number: String,
    val type_name: String,
    val Booking: BookingInfo,
    val Building: BuildingInfo
)

data class BookingInfo(
    val booking_date: String
)

data class BuildingInfo(
    val name: String
)