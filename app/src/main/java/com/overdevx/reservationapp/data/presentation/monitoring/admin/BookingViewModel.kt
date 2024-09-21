package com.overdevx.reservationapp.data.presentation.monitoring.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overdevx.reservationapp.data.model.BookingResponse
import com.overdevx.reservationapp.data.model.UpdateRoomsResponse
import com.overdevx.reservationapp.data.repository.BookingRespository
import com.overdevx.reservationapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRespository
) : ViewModel() {
    private val _room_id = MutableStateFlow("")
    val room_id: StateFlow<String> = _room_id

    private val _days = MutableStateFlow("")
    val days: StateFlow<String> = _days

    private val _bookingState = MutableStateFlow<Resource<BookingResponse>>(Resource.Idle)
    val bookingState: StateFlow<Resource<BookingResponse>> = _bookingState

    private val _updateRoomState = MutableStateFlow<Resource<UpdateRoomsResponse>>(Resource.Idle)
    val updateRoomState: StateFlow<Resource<UpdateRoomsResponse>> = _updateRoomState

    fun bookRoom(roomId: Int, days: Int) {
        viewModelScope.launch {
            _bookingState.value = Resource.Loading
            val result = bookingRepository.booking(roomId, days)
            _bookingState.value = result
        }
    }

    fun updateRoomStatus(roomId: Int, statusId: Int) {
        viewModelScope.launch {
            _updateRoomState.value = Resource.Loading
            val result = bookingRepository.updateRoomStatus(roomId, statusId)
            _updateRoomState.value = result
        }
    }

    fun resetUpdateState() {
        _updateRoomState.value = Resource.Idle
    }


    fun resetBookingState() {
        _bookingState.value = Resource.Idle
    }

}