package com.overdevx.reservationapp.data.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overdevx.reservationapp.data.model.Room
import com.overdevx.reservationapp.data.repository.RoomRepository
import com.overdevx.reservationapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomsViewModel @Inject constructor(
    private val repository: RoomRepository
):ViewModel() {
    private val _roomState = MutableStateFlow<Resource<List<Room>>>(Resource.Idle)
    val roomState: StateFlow<Resource<List<Room>>> = _roomState

    fun fetchRooms(buildingId: Int) {
        viewModelScope.launch {
            _roomState.value = Resource.Loading
            _roomState.value = repository.getRooms(buildingId)
        }
    }

}