package com.overdevx.reservationapp.data.repository

import android.util.Log
import com.overdevx.reservationapp.data.model.LoginRequest
import com.overdevx.reservationapp.data.model.LoginResponse
import com.overdevx.reservationapp.data.remote.ApiService
import com.overdevx.reservationapp.utils.Resource
import com.overdevx.reservationapp.utils.TokenProvider
import javax.inject.Inject

class AuthRepository @Inject constructor(
   private val publicApiService: ApiService,
   private val tokenProvider: TokenProvider
) {
    suspend fun login(email: String, password: String): Resource<LoginResponse> {
        return try{
            val response = publicApiService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenProvider.saveToken(body.data.token)
                    Resource.Success(body)
                } else {
                    Resource.ErrorMessage("Login failed: No response body")
                }
            } else {
                Resource.ErrorMessage("Login failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("RetrofitError", "Error: ${e.message}")
            Resource.Error(e)
        }

    }
    suspend fun logout(){
        tokenProvider.clearToken()
    }
}