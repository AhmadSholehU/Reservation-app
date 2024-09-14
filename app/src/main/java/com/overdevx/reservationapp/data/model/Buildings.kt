package com.overdevx.reservationapp.data.model

data class BuildingResponse(
    val data: List<Building>,
    val message: String,
    val status: String
)

data class Building(
    val building_id: Int,
    val name: String
)