package com.overdevx.reservationapp.data.model

data class LoginResponse(
    val data: Data,
    val message: String,
    val status: String
)

data class Data(
    val username: String,
    val email: String,
    val token: String
)

data class LoginRequest(
    val email: String,
    val password: String
)