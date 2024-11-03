package com.overdevx.reservationapp.data.model

data class KetersediaanResponse(
    val status: String,
    val message: String,
    val data: List<Ketersediaan>
)

data class Ketersediaan(
    val start_date: String,
    val end_date: String,
)