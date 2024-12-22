package com.overdevx.reservationapp.data.model

data class HistoryResponse(
    val status: String,
    val message: String,
    val data: List<History>,
    val totalItems: Int,
    val currentPage: Int,
    val totalPages: Int
)
data class History(
    val id:Int,
    val booking_room_id: Int,
    val room_id: Int,
    val days: Int,
    val nomor_pesanan:String,
    var start_date:String,
    val end_date:String,
    val changed_at: String,
    val Room: BookingDetail
)

data class BookingDetail(
    val room_number: String,
    val Building: BuildingInfo
)

data class BuildingInfo(
    val name: String
)
