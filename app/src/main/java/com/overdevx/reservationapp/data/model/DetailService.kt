package com.overdevx.reservationapp.data.model

data class DetailServiceResponse(
    val status: String,
    val message: String,
    val data: List<DetailService>
)
data class DetailService(
    val detail_service_id: Int,
    val nama: String,
    val deskripsi: String,
    val harga: Int,
    val rating: Double,
    val foto: String,
    val jumlah_kamar: Int
)
