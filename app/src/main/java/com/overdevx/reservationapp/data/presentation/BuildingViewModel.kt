package com.overdevx.reservationapp.data.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overdevx.reservationapp.data.model.Building
import com.overdevx.reservationapp.data.model.Room
import com.overdevx.reservationapp.data.repository.BuildingRepository
import com.overdevx.reservationapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuildingViewModel @Inject constructor(
    private val repository: BuildingRepository
) : ViewModel() {
    private val _buildingState = MutableStateFlow<Resource<List<Building>>>(Resource.Idle)
    val buildingState: StateFlow<Resource<List<Building>>> = _buildingState

//    // Menginisialisasi state dengan onStart dan stateIn
//    val buildingState = _buildingState
//        .onStart { emit(Resource.Loading) } // Emit loading sebelum fetch data
//        .stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5000L),
//            Resource.Loading // Nilai awal yang sesuai dengan Resource
//        )

    init {
       fetchBuilding()
    }

    fun fetchBuilding() {
        viewModelScope.launch {
            _buildingState.value = Resource.Loading
            _buildingState.value = repository.getBuilding()
        }

    }

    fun resetBuildingState() {
        _buildingState.value = Resource.Idle
    }
}