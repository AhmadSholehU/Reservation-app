package com.overdevx.reservationapp.data.presentation.monitoring.admin

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.BookingList
import com.overdevx.reservationapp.data.model.KetersediaanResponse
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.gray2
import com.overdevx.reservationapp.ui.theme.gray3
import com.overdevx.reservationapp.ui.theme.gray4
import com.overdevx.reservationapp.ui.theme.green
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.white2
import com.overdevx.reservationapp.utils.Resource
import com.overdevx.reservationapp.utils.convertDate
import com.overdevx.reservationapp.utils.convertDate2
import com.overdevx.reservationapp.utils.formatDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingListScreen(
    bookingViewModel: BookingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val unselectableDates = remember { mutableStateListOf<Long>() }
    val ketersediaanState by bookingViewModel.getKetersediaanState.collectAsStateWithLifecycle()
    val updateBookingState by bookingViewModel.updatatebookingState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }

    var selectedBooking by remember { mutableStateOf<BookingList?>(null) }
    var showStatusDialog by remember { mutableStateOf(false) }

    // State untuk menyimpan tanggal yang dipilih
    var selectedStartDate by remember { mutableStateOf<String?>(null) }
    var selectedEndDate by remember { mutableStateOf<String?>(null) }

    val bookingListState by bookingViewModel.getBookingListState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            delay(2000)
            bookingViewModel.getBookingList()
            isRefreshing = false
        }
    }
    LaunchedEffect(Unit) {
        bookingViewModel.getBookingList()
    }
    Column(modifier = Modifier.padding(10.dp)) {
        TopBarSection(onNavigateBack = { onNavigateBack() })
        PullToRefreshBox(
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.background(gray4, RoundedCornerShape(10.dp))
        ) {
            when (bookingListState) {
                Resource.Loading -> {
                    LoadingShimmerEffect()
                }

                is Resource.Success -> {
                    val bookingList = (bookingListState as Resource.Success<List<BookingList>>).data
                    if (bookingList != null) {
                        if (bookingList.isEmpty()) {
                            Text(text = "No booking available")
                        } else {
                            LazyColumn(
                                modifier = modifier
                                    .fillMaxSize()
                                    .padding(10.dp)

                            ) {
                                items(bookingList, key = { it.booking_id }) { bookingList ->
                                    BookingItem(booking = bookingList, onClick = {
                                        selectedBooking = bookingList
                                        selectedStartDate = bookingList.Booking.start_date
                                        selectedEndDate = bookingList.Booking.end_date
                                        showStatusDialog = true

                                    })
                                }
                            }
                        }
                    }
                }

                is Resource.Error -> {

                }

                is Resource.ErrorMessage -> {

                }

                Resource.Idle -> {

                }

            }
        }

        selectedBooking?.let { booking ->
            bookingViewModel.getKetersediaan(booking.room_id)
            if (showStatusDialog) {
                StatusDialog(
                    onDismiss = { showStatusDialog = false },
                    bookingList = booking,
                    onBooking = {
                        bookingViewModel.updateBookingRoom(booking.booking_room_id,
                            selectedStartDate!!, selectedEndDate!!
                        )
                    },
                    StartDate = selectedStartDate,
                    EndDate = selectedEndDate,
                    unselectableDates = unselectableDates,
                    onDateRangeSelected = { startDate, endDate ->
                        selectedStartDate = startDate
                        selectedEndDate = endDate
                    },
                    showDialog={ shouldShow->
                        showDialog=shouldShow
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            when (ketersediaanState) {
                is Resource.Loading -> {
                    // Show loading indicator if necessary
                }

                is Resource.Success -> {
                    val ketersediaanDate =
                        (ketersediaanState as Resource.Success<KetersediaanResponse>).data?.data
                    unselectableDates.clear()
                    if (ketersediaanDate != null) {
                        ketersediaanDate.forEach { ketersediaan ->
                            val originalDateFormat =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                            originalDateFormat.timeZone = TimeZone.getTimeZone("UTC")

                            // Modifikasi string tanggal
                            val modifiedStartDate = ketersediaan.start_date.replace("T01", "T00")

                            // Parse tanggal ke Long
                            val startDateInMillis = originalDateFormat.parse(modifiedStartDate)?.time
                            val endDateInMillis = originalDateFormat.parse(ketersediaan.end_date)?.time

                            Log.d("DATE", "Start Date: $modifiedStartDate, End Date: ${ketersediaan.end_date}")
                            // Tambahkan tanggal ke daftar unselectableDates
                            if (startDateInMillis != null && endDateInMillis != null) {
                                val current = Calendar.getInstance()
                                current.timeInMillis = startDateInMillis

                                while (current.timeInMillis <= endDateInMillis) {
                                    unselectableDates.add(current.timeInMillis)
                                    current.add(Calendar.DATE, 1) // Increment 1 hari
                                }
                            }
                        }

                    }
                    Log.d("DATE", unselectableDates.toString())
                }

                is Resource.Error -> {
                    // Handle error state (e.g., show a Snackbar or Toast)
                }

                is Resource.ErrorMessage -> {
                    // Handle specific error messages
                }

                is Resource.Idle -> {
                    // Do nothing, idle state
                }
            }
        }
        when (updateBookingState) {
            is Resource.Loading -> {
                // Show loading indicator if necessary
            }

            is Resource.Success -> {
                showSuccessDialog = true
                if (showSuccessDialog) {
                    SuccessDialog(
                        onDismiss = { showDialog = false },
                        onClick = {
                            bookingViewModel.resetUpdateBookingState()
                            bookingViewModel.getBookingList()
                        })
                }
            }

            is Resource.Error -> {
                // Handle error state (e.g., show a Snackbar or Toast)
            }

            is Resource.ErrorMessage -> {
                // Handle specific error messages
            }

            is Resource.Idle -> {
                // Do nothing, idle state
            }
        }

    }
}

@Composable
private fun TopBarSection(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = { onNavigateBack() },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(40.dp),
            colors = IconButtonDefaults.iconButtonColors(Color.Transparent)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint = secondary
            )
        }
        Spacer(modifier = modifier.width(10.dp))

        Text(
            text = "Daftar Ruang \n Terbooking",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 20.sp,
            color = secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )


    }


}

@Composable
private fun BookingItem(
    booking: BookingList,
    onClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(white)
    ) {
        Box(
            modifier = Modifier
                .background(primary)
                .size(width = 8.dp, height = 130.dp)

        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.padding(5.dp)) {
            Text(
                text = "ID BOOKING : ${booking.nomor_pesanan}",
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 14.sp,
                color = gray2,
                modifier = Modifier
            )

            Text(
                text = "${booking.Room.Building.name} - ${booking.Room.room_number}",
                fontFamily = FontFamily(listOf(Font(R.font.inter_bold))),
                fontSize = 18.sp,
                color = secondary,
                modifier = Modifier
            )

            Text(
                text = "Transaksi Berhasil",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 14.sp,
                color = green,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    onClick()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Box(
                    modifier = Modifier
                        .width(85.dp)
                        .height(25.dp)
                ) {
                    Row(modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = white,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(20.dp)
                        )

                        Text(
                            text = "Reschedule",
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 14.sp,
                            color = white,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                }

            }

        }
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.Center)
            ) {
                Row(modifier = Modifier) {
                    val startDate = convertDate(booking.Booking.start_date)
                    val endDate = convertDate(booking.Booking.end_date)
                    Column {
                        Text(
                            text = "Check In",
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 16.sp,
                            color = gray2,
                            modifier = Modifier
                        )
                        if (startDate != null) {
                            Text(
                                text = startDate,
                                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                fontSize = 16.sp,
                                color = green,
                                modifier = Modifier
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Check Out",
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 16.sp,
                            color = gray2,
                            modifier = Modifier
                        )
                        if (endDate != null) {
                            Text(
                                text = endDate,
                                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                fontSize = 16.sp,
                                color = primary,
                                modifier = Modifier
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_days),
                        contentDescription = null,
                        tint = gray2,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${booking.days} Hari",
                        fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                        fontSize = 16.sp,
                        color = secondary,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

            }
        }


    }
}

@Composable
private fun StatusDialog(
    onDismiss: () -> Unit,
    bookingList: BookingList,
    onBooking: () -> Unit,
    StartDate: String?,
    EndDate: String?,
    unselectableDates: MutableList<Long>,
    onDateRangeSelected: (String, String) -> Unit,
    showDialog: (Boolean) -> Unit,
    modifier: Modifier
) {

    var showModal by remember { mutableStateOf(false) }

    var selectedStartDate by remember { mutableStateOf<String?>(null) }
    var selectedEndDate by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = "Reschedule Booking ${bookingList.Room.room_number}",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 20.sp,
                    color = secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

        },
        text = {
            Column(Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
//                    (formattedSelectedStartDate?:formattedStartDate)?.let { BookingDateColumn(label = "Check In", date = it) }
//                    (formattedSelectedEndDate?:formattedEndDate)?.let { BookingDateColumn(label = "Check Out", date = it) }
                    (selectedStartDate ?: convertDate(bookingList.Booking.start_date))?.let {
                        BookingDateColumn(
                            label = "Check In",
                            date = it,
                            color = green
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    (selectedEndDate ?: convertDate(bookingList.Booking.end_date))?.let {
                        BookingDateColumn(
                            label = "Check Out",
                            date = it,
                            color = primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        showModal = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = secondary,
                    ),
                    modifier = Modifier
                ) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(25.dp)
                    ) {
                        Row(modifier = Modifier.align(Alignment.CenterStart)) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = white,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .size(20.dp)
                            )

                            Text(
                                text = "Pilih Tanggal",
                                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                fontSize = 14.sp,
                                color = white,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }

                    }

                }
                if (showModal) {
                    DatePickerWithDateSelectableDatesSample(
                        unselectableDates,
                        onDateRangeSelected = { startDate, endDate ->
                            selectedStartDate = convertDate2(startDate)
                            selectedEndDate = convertDate2(endDate)
                            onDateRangeSelected(selectedStartDate!!, selectedEndDate!!)
                            Log.d("DATE", selectedStartDate!!)
                            showModal = false
                        },
                        onDismiss = { showModal = false })

                }

            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Lakukan sesuatu dengan status dan waktu penyewaan yang dipilih
                    onBooking()
                    showDialog(true)
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = secondary,
                )
            ) {
                Text(
                    text = "UPDATE",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 16.sp,
                    color = white,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, secondary),
                colors = ButtonDefaults.buttonColors(containerColor = white)
            ) {
                Text(
                    text = "BATAL",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 16.sp,
                    color = secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
            }
        },
        containerColor = white,
        shape = RoundedCornerShape(10.dp),

        )


}

@Composable
private fun BookingDateColumn(label: String, date: String,color:Color) {
    Column(modifier = Modifier) {
        Text(
            text = label,
            fontFamily = FontFamily(Font(R.font.inter_semibold)),
            fontSize = 16.sp,
            color = secondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .border(1.dp, secondary, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = date,
                fontFamily = FontFamily(Font(R.font.inter_semibold)),
                fontSize = 14.sp,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}