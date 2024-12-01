package com.overdevx.reservationapp.data.presentation.monitoring.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.overdevx.reservationapp.data.model.BookingList
import com.overdevx.reservationapp.data.model.BookingResponse
import com.overdevx.reservationapp.data.model.BookingRoomResponse
import com.overdevx.reservationapp.data.model.KetersediaanResponse
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

    private val _updatatebookingState = MutableStateFlow<Resource<BookingRoomResponse>>(Resource.Idle)
    val updatatebookingState: StateFlow<Resource<BookingRoomResponse>> = _updatatebookingState

    private val _updateRoomState = MutableStateFlow<Resource<UpdateRoomsResponse>>(Resource.Idle)
    val updateRoomState: StateFlow<Resource<UpdateRoomsResponse>> = _updateRoomState

    private val _getBookingRoomState = MutableStateFlow<Resource<BookingRoomResponse>>(Resource.Idle)
    val getBookingState: StateFlow<Resource<BookingRoomResponse>> = _getBookingRoomState

    private val _getKetersediaanState = MutableStateFlow<Resource<KetersediaanResponse>>(Resource.Idle)
    val getKetersediaanState: StateFlow<Resource<KetersediaanResponse>> = _getKetersediaanState

    private val _getBookingListState = MutableStateFlow<Resource<List<BookingList>>>(Resource.Idle)
    val getBookingListState: StateFlow<Resource<List<BookingList>>> = _getBookingListState

    val bookingRooms = bookingRepository.getBookingRooms().flow.cachedIn(viewModelScope)
    fun bookRoom(roomId: Int, startDate: String,endDate:String) {
        viewModelScope.launch {
            _bookingState.value = Resource.Loading
            val result = bookingRepository.booking(roomId, startDate,endDate)
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

    fun getBookingRoom(roomId: Int) {
        viewModelScope.launch {
            _getBookingRoomState.value = Resource.Loading
            val result = bookingRepository.getBookingRoom(roomId)
            _getBookingRoomState.value = result  // Update the state with the fetched data
        }
    }

    fun updateBookingRoom(bookingRoomId:Int,start_date: String,end_date:String){
        viewModelScope.launch {
            _updatatebookingState.value=Resource.Loading
            val result=bookingRepository.updateBookingRoom(bookingRoomId,start_date,end_date)
            _updatatebookingState.value=result
        }
    }

    fun getKetersediaan(roomId: Int) {
        viewModelScope.launch {
            _getKetersediaanState.value = Resource.Loading
            val result = bookingRepository.getKetersediaan(roomId)
            _getKetersediaanState.value = result  // Update the state with the fetched data
        }
    }

    fun getBookingList() {
        viewModelScope.launch {
            _getBookingListState.value = Resource.Loading
            val result = bookingRepository.getBookingList()
            _getBookingListState.value = result  // Update the state with the fetched data
        }
    }

    fun resetUpdateState() {
        _updateRoomState.value = Resource.Idle
    }

    fun resetBookingState() {
        _bookingState.value = Resource.Idle
    }

    fun resetUpdateBookingState() {
        _updatatebookingState.value = Resource.Idle
    }

    fun resetKetersediaanState() {
        _getKetersediaanState.value = Resource.Idle
    }

}