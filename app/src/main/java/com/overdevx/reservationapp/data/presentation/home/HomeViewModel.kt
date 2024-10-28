package com.overdevx.reservationapp.data.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overdevx.reservationapp.data.model.DetailService
import com.overdevx.reservationapp.data.model.History
import com.overdevx.reservationapp.data.repository.DetailServiceRepository
import com.overdevx.reservationapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val detailServiceRepository: DetailServiceRepository
):ViewModel() {
    private val _detailServiceState = MutableStateFlow<Resource<List<DetailService>>>(Resource.Idle)
    val detailServiceState: StateFlow<Resource<List<DetailService>>> = _detailServiceState

    init {
        fetchDetailService()
    }
    fun fetchDetailService() {
        viewModelScope.launch {
            delay(1000)
            _detailServiceState.value = Resource.Loading
            val result = detailServiceRepository.getDetailService()
            _detailServiceState.value = result
        }
    }
}