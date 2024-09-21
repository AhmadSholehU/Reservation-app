package com.overdevx.reservationapp.data.presentation.monitoring.history

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.History
import com.overdevx.reservationapp.data.presentation.monitoring.admin.ErrorItem
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.gray3
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.white2
import com.overdevx.reservationapp.utils.Resource
import com.overdevx.reservationapp.utils.formatDate

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel= hiltViewModel(),
    modifier: Modifier = Modifier) {

    val historyState by historyViewModel.historyState.collectAsStateWithLifecycle()

    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxSize()) {

            Text(
                text = "Riwayat",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 22.sp,
                color = secondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )


        Spacer(modifier = Modifier.height(10.dp))
        when (historyState) {
            is Resource.Loading -> {
                Column(Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = primary
                    )
                }
            }
            is Resource.Success -> {
                val bookingHistoryList = (historyState as Resource.Success<List<History>>).data
                Log.d("HomeScreen", "Booking History List: $bookingHistoryList")
                val groupedData = bookingHistoryList?.groupBy { formatDate(it.detail.Booking.booking_date) }
                Log.d("HomeScreen", "Grouped Data: $groupedData")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(gray3)
                        .padding(10.dp)

                ) {
                    groupedData?.forEach { (date, bookings) ->
                        item {
                            // Date Header
                            Text(
                                text = date,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = secondary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(bookings, key = {it.booking_id}) { booking ->
                            BookingItem(booking)
                        }
                    }
                }

            }
            is Resource.ErrorMessage -> {
                val errorMessage = (historyState as Resource.ErrorMessage).message
                Text(text = "Error: $errorMessage")
                Log.e("HomeScreen", "Error: $errorMessage")
            }

            is Resource.Error -> {
                // Handle error dari Exception
                val exceptionMessage =
                    (historyState as Resource.Error).exception.message ?: "Unknown error occurred"
                ErrorItem(errorMsg = exceptionMessage)
            }

            is Resource.Idle -> {
                LaunchedEffect(Unit) {
                    historyViewModel.fetchHistory()
                }
            }
            else -> {}
        }

    }
}

@Composable
fun BookingItem(history: History) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = secondary)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = history.detail.Building.name,
                color = white,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Ruang ${history.detail.room_number}",
                color = white2,
                fontSize = 12.sp
            )
        }
    }
}


