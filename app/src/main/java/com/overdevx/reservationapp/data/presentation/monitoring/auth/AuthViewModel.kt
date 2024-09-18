package com.overdevx.reservationapp.data.presentation.monitoring.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overdevx.reservationapp.data.model.Data
import com.overdevx.reservationapp.data.model.LoginResponse
import com.overdevx.reservationapp.data.repository.AuthRepository
import com.overdevx.reservationapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    private val _loginState = MutableStateFlow<Resource<LoginResponse>>(Resource.Idle)
    val loginState: StateFlow<Resource<LoginResponse>> = _loginState

     fun login() {
        val email = _email.value
        val password = _password.value
        // Validasi input jika diperlukan
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = Resource.ErrorMessage("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = authRepository.login(email, password)
            _loginState.value = result
        }
    }

    // Fungsi untuk mereset login state
    fun resetLoginState() {
        _loginState.value = Resource.Idle
        _email.value = ""
        _password.value = ""
    }

    fun logoutUser(){
        viewModelScope.launch {
            authRepository.logout()
        }
    }

}