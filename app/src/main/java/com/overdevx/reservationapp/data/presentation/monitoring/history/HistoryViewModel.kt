package com.overdevx.reservationapp.data.presentation.monitoring.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.overdevx.reservationapp.data.model.History
import com.overdevx.reservationapp.data.model.Monitoring
import com.overdevx.reservationapp.data.repository.HistoryRepository
import com.overdevx.reservationapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository
):ViewModel(){
    private val _historyState = MutableStateFlow<Resource<List<History>>>(Resource.Idle)
    val historyState: StateFlow<Resource<List<History>>> = _historyState
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val historyList = _searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) {
            repository.getHistoryList().flow.cachedIn(viewModelScope)
        } else {
            repository.searchHistoryPaging(query).flow.cachedIn(viewModelScope)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun fetchHistory() {
        viewModelScope.launch {
            delay(1000)
            _historyState.value = Resource.Loading
            val result = repository.getMonitoring()
            _historyState.value = result
        }
    }
}