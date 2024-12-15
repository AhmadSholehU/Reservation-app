package com.overdevx.reservationapp.data.model

data class BookingListResponse(
	val data: List<BookingList>,
	val message: String,
	val status: String,
	val totalItems: Int,
	val currentPage: Int,
	val totalPages: Int
)

data class BookingData(
	val start_date: String,
	val end_date: String
)

data class BuildingData(
	val name: String
)

data class BookingRoomList(
	val booking_room_id: Int,
	val booking_id: Int,
	val Booking: BookingData
)

data class BookingList(
	val booking_detail_id: Int,
	val booking_room_id: Int,
	val room_id: Int,
	val nomor_pesanan: String,
	val days: Int,
	val BookingRoom: BookingRoomList,
	val Room: RoomDataList
)


data class RoomDataList(
	val Building: BuildingData,
	val room_number: String,
)

