package com.overdevx.reservationapp.data.presentation.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overdevx.reservationapp.data.model.Monitoring
import com.overdevx.reservationapp.data.repository.MonitoringRepository
import com.overdevx.reservationapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val repository: MonitoringRepository
):ViewModel() {

    private val _monitoringState = MutableStateFlow<Resource<List<Monitoring>>>(Resource.Idle)
    val monitoringState: StateFlow<Resource<List<Monitoring>>> = _monitoringState

    // Variabel global untuk menyimpan data gedung setelah fetch
    private val _roomCounts = MutableStateFlow<List<Monitoring>>(emptyList())
    val roomCounts: StateFlow<List<Monitoring>> = _roomCounts

    fun fetchMonitoring() {
        viewModelScope.launch {
            _monitoringState.value = Resource.Loading
            val result = repository.getMonitoring()
            _monitoringState.value = result

            // Jika data berhasil diambil, simpan ke _roomCounts
            if (result is Resource.Success) {
                _roomCounts.value = result.data ?: emptyList()
            }
        }
    }

}