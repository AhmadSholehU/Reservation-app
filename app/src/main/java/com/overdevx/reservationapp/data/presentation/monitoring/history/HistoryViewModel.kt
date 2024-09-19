package com.overdevx.reservationapp.data.presentation.monitoring.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overdevx.reservationapp.data.model.History
import com.overdevx.reservationapp.data.model.Monitoring
import com.overdevx.reservationapp.data.repository.HistoryRepository
import com.overdevx.reservationapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository
):ViewModel(){
    private val _historyState = MutableStateFlow<Resource<List<History>>>(Resource.Idle)
    val historyState: StateFlow<Resource<List<History>>> = _historyState

    init {
        fetchHistory()
    }
    fun fetchHistory() {
        viewModelScope.launch {
            _historyState.value = Resource.Loading
            val result = repository.getMonitoring()
            _historyState.value = result
        }
    }
}