package com.overdevx.reservationapp.data.model

data class HistoryResponse(
    val status: String,
    val message: String,
    val data: List<History>
)
data class History(
    val bookingRoomId: Int,
    val bookingId: Int,
    val roomId: Int,
    val days: Int,
    val detail: BookingDetail
)

data class BookingDetail(
    val roomNumber: String,
    val typeName: String,
    val booking: BookingInfo,
    val building: BuildingInfo
)

data class BookingInfo(
    val bookingDate: String
)

data class BuildingInfo(
    val name: String
)