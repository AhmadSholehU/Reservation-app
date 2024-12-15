package com.overdevx.reservationapp.data.model

data class BookingListinitResponse(
    val status: String,
    val message: String,
    val data: List<BookingRoominit>,
    val totalItems: Int,
    val currentPage: Int,
    val totalPages: Int
)

data class BookingRoominit(
    val bookingRoomId: Int,
    val startDate: String,
    val endDate: String,
    val days: Int,
    val buildingName: String
)
