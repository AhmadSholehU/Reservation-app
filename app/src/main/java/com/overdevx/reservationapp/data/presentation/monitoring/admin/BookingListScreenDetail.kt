package com.overdevx.reservationapp.data.presentation.monitoring.admin

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.BookingList
import com.overdevx.reservationapp.data.model.BookingListResponse
import com.overdevx.reservationapp.data.model.KetersediaanResponse
import com.overdevx.reservationapp.data.presentation.home.nonScaledSp
import com.overdevx.reservationapp.ui.theme.background2
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.gray2
import com.overdevx.reservationapp.ui.theme.green
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.yellow2
import com.overdevx.reservationapp.utils.AutoResizedText
import com.overdevx.reservationapp.utils.Resource
import com.overdevx.reservationapp.utils.convertDate
import com.overdevx.reservationapp.utils.convertDate2
import com.overdevx.reservationapp.utils.reverseConvertDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Composable
fun BookingListScreenDetail(
    bookingViewModel: BookingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateBack2: () -> Unit,
    bookingRoomId:Int,
    modifier: Modifier = Modifier) {
    var selectedBooking by remember { mutableStateOf<List<BookingList?>?>(null) }
    var selectedStartDate by remember { mutableStateOf<String?>(null) }
    var selectedEndDate by remember { mutableStateOf<String?>(null) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    val unselectableDates = remember { mutableStateListOf<Long>() }
    var showSuccessDialog by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val ketersediaanState by bookingViewModel.getKetersediaanState.collectAsStateWithLifecycle()
    val updateBookingState by bookingViewModel.updatatebookingState.collectAsStateWithLifecycle()
    val deleteBookingState by bookingViewModel.deletebookingState.collectAsStateWithLifecycle()
    LaunchedEffect(bookingRoomId) {
        bookingViewModel.getKetersediaanBooking(bookingRoomId)
        bookingViewModel.getBookingListbyId(bookingRoomId)
    }

    val bookingListbyIdState by bookingViewModel.getBookingListbyIdState.collectAsStateWithLifecycle()
    var jumlahKamar by remember { mutableStateOf(0) }
    Column(modifier = Modifier.padding(10.dp)) {
        TopBarSection(onNavigateBack = { onNavigateBack() })
        when(bookingListbyIdState){
            is Resource.Error -> {

            }
            is Resource.ErrorMessage -> {

            }
            Resource.Idle -> {

            }
            Resource.Loading -> {
                LoadingShimmerEffect()
            }
            is Resource.Success -> {
                val bookingList = (bookingListbyIdState as Resource.Success<BookingListResponse>).data?.data
                if (bookingList.isNullOrEmpty()) {
                    AutoResizedText(
                        text = "Tidak ada ruang terbooking ",
                        color = secondary,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 14.nonScaledSp,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }else{
                    BookingItem2(
                        booking = bookingList[0],
                        jumlahKamar = jumlahKamar,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    ListRoom(
                        bookingViewModel,
                        onClick = {
                            selectedBooking = bookingList
                            selectedStartDate = bookingList[0]?.BookingRoom?.Booking?.start_date
                            selectedEndDate = bookingList[0]?.BookingRoom?.Booking?.end_date
                            showStatusDialog = true
                        },
                        ondeleteClick = {
                            showDeleteDialog = true


                        },
                        onJumlahKamar = {
                            jumlahKamar=it
                        },
                        modifier = Modifier.padding(start = 10.dp, end =10.dp))

                }
            }
        }

        if(showDeleteDialog){
            DeleteDialog(
                onDismiss = { showDeleteDialog = false },
                onClick = {
                    bookingViewModel.deleteBookingRoom(bookingRoomId)
                }
            )
        }
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
                        SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                            Locale.getDefault()
                        )
                    originalDateFormat.timeZone = TimeZone.getTimeZone("UTC")

                    // Modifikasi string tanggal
                    val modifiedStartDate =
                        ketersediaan.start_date.replace("T01", "T00")

                    // Parse tanggal ke Long
                    val startDateInMillis =
                        originalDateFormat.parse(modifiedStartDate)?.time
                    val endDateInMillis =
                        originalDateFormat.parse(ketersediaan.end_date)?.time

                    Log.d(
                        "DATE",
                        "Start Date: $modifiedStartDate, End Date: ${ketersediaan.end_date}"
                    )
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
    selectedBooking?.let { booking ->
        if (showStatusDialog) {
            booking[0]?.let {
                StatusDialog2(
                    onDismiss = { showStatusDialog = false },
                    bookingList = it,
                    onBooking = {
                        booking[0]?.let {
                            val startdate= selectedStartDate?.let { it1 -> reverseConvertDate(it1) }
                            val enddate= selectedEndDate?.let { it1 -> reverseConvertDate(it1) }
                            if (startdate != null) {
                                if (enddate != null) {
                                    bookingViewModel.updateBookingRoom(
                                        it.booking_room_id,
                                        startdate, enddate
                                    )
                                }
                            }
                        }
                    },
                    unselectableDates = unselectableDates,
                    onDateRangeSelected = { startDate, endDate ->
                        selectedStartDate = startDate
                        selectedEndDate = endDate
                    },
                    showDialog = { shouldShow ->
                        showDialog = shouldShow
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    when (updateBookingState) {
        is Resource.Loading -> {
            Loading()
        }

        is Resource.Success -> {
            showSuccessDialog = true
            if (showSuccessDialog) {
                SuccessDialog(
                    onDismiss = { showDialog = false },
                    onClick = {
                        bookingViewModel.resetUpdateBookingState()
                        bookingViewModel.getBookingListbyId(bookingRoomId)
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

    when (deleteBookingState) {
        is Resource.Loading -> {
          Loading()
        }

        is Resource.Success -> {
            if (showSuccessDialog) {
                SuccessDialog(
                    onDismiss = { showSuccessDialog = false },
                    onClick = {
                        bookingViewModel.resetUpdateBookingState()
                        bookingViewModel.getBookingListbyId(bookingRoomId)
                        onNavigateBack2()
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
            text = "Detail Ruang \n Terbooking",
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
    ondeleteClick: () -> Unit,
    bookingViewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val bookingListbyIdState by bookingViewModel.getBookingListbyIdState.collectAsStateWithLifecycle()
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(white),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .height(300.dp)
                .width(8.dp)
                .background(primary)

        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            AutoResizedText(
                text = "ID BOOKING : ${booking.nomor_pesanan}",
                color = gray2,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 10.nonScaledSp,
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                val startDate = convertDate(booking.BookingRoom.Booking.start_date)
                val endDate = convertDate(booking.BookingRoom.Booking.end_date)
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    AutoResizedText(
                        text = "Check In",
                        color = gray2,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 12.nonScaledSp,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier
                    )

                    if (startDate != null) {
                        AutoResizedText(
                            text = startDate,
                            color = green,
                            style = TextStyle(
                                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                fontSize = 12.nonScaledSp,
                            ),
                            modifier = Modifier
                        )
                    }
                }
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(green)
                    ) {
                        Icon(
                            painterResource(R.drawable.pixelarticons_check),
                            null,
                            tint = white,
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
                Column(modifier = Modifier.align(Alignment.TopEnd)) {
                    AutoResizedText(
                        text = "Check Out",
                        color = gray2,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 12.nonScaledSp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.align(Alignment.End)
                    )
                    if (endDate != null) {
                        AutoResizedText(
                            text = endDate,
                            color = primary,
                            style = TextStyle(
                                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                fontSize = 12.nonScaledSp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = Dp.Hairline, color = primary)
            Spacer(modifier = Modifier.height(10.dp))

            AutoResizedText(
                text = "Ruang Terbooking ",
                color = secondary,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 14.nonScaledSp,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(10.dp))
            when (bookingListbyIdState) {
                is Resource.Error -> {

                }
                is Resource.ErrorMessage -> {

                }
                Resource.Idle -> {

                }
                Resource.Loading -> {

                }
                is Resource.Success -> {
                    val roomData =
                        (bookingListbyIdState as Resource.Success<BookingListResponse>).data?.data
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        items(roomData.orEmpty()) { room ->
                            RoomItem(room)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = Dp.Hairline, color = primary)
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Button(
                    onClick = {
                        onClick()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = green,
                    ),
                    modifier = Modifier
                ) {
                    Box(
                        modifier = Modifier
                            .width(85.dp)
                            .height(25.dp)
                    ) {
                        Row(modifier = Modifier.align(Alignment.Center)) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = white,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .size(20.dp)
                            )
                            AutoResizedText(
                                text = "Reschedule",
                                color = white,
                                style = TextStyle(
                                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                    fontSize = 10.nonScaledSp,
                                    textAlign = TextAlign.Center,
                                ),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }

                    }

                }
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = {
                        ondeleteClick()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primary,
                    ),
                    modifier = Modifier
                ) {
                    Box(
                        modifier = Modifier
                            .width(85.dp)
                            .height(25.dp)
                    ) {
                        Row(modifier = Modifier.align(Alignment.Center)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = white,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .size(20.dp)
                            )
                            AutoResizedText(
                                text = "Delete",
                                color = white,
                                style = TextStyle(
                                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                    fontSize = 10.nonScaledSp,
                                    textAlign = TextAlign.Center,
                                ),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }

                    }

                }
            }


        }
        Spacer(modifier = Modifier.weight(1f))

    }
}

@Composable
private fun StatusDialog2(
    onDismiss: () -> Unit,
    bookingList: BookingList,
    onBooking: () -> Unit,
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
                AutoResizedText(
                    text = "Reschedule Booking ${bookingList.booking_room_id}",
                    color = secondary,
                    style = TextStyle(
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 16.nonScaledSp,
                        textAlign = TextAlign.Center,
                    ),
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
                    (selectedStartDate
                        ?: convertDate(bookingList.BookingRoom.Booking.start_date))?.let {
                        BookingDateColumn(
                            label = "Check In",
                            date = it,
                            color = green
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    (selectedEndDate
                        ?: convertDate(bookingList.BookingRoom.Booking.end_date))?.let {
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
private fun BookingDateColumn(label: String, date: String, color: Color) {
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

@Composable
private fun BookingItem2(
    booking: BookingList,jumlahKamar:Int=0,modifier: Modifier = Modifier) {

    TicketShapeCard {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AutoResizedText(
                text = "ID BOOKING : ${booking.booking_room_id}",
                color = gray,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    fontSize = 10.nonScaledSp,
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(8.dp))
            AutoResizedText(
                text = booking.Room.Building.name,
                color = secondary,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 14.nonScaledSp,
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(5.dp))
            AutoResizedText(
                text = "Pembelian Berhasil",
                color = green,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 10.nonScaledSp,
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(35.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val startDate = convertDate(booking.BookingRoom.Booking.start_date)
                val endDate = convertDate(booking.BookingRoom.Booking.end_date)
                // Left Section
                Column {

                    AutoResizedText(
                        text = "Jumlah Ruang/Kamar",
                        color = gray,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 10.nonScaledSp,
                        ),
                        modifier = Modifier
                    )
                    AutoResizedText(
                        text = jumlahKamar.toString(),
                        color = secondary,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 10.nonScaledSp,
                        ),
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AutoResizedText(
                        text = "Check In",
                        color = gray,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 10.nonScaledSp,
                        ),
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .border(
                                1.dp, primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Row(modifier=Modifier.align(Alignment.Center)){
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = primary
                            )
                            if (startDate != null) {
                                AutoResizedText(
                                    text = startDate,
                                    color = primary,
                                    style = TextStyle(
                                        fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                        fontSize = 10.nonScaledSp,
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                )
                            }
                        }

                    }
                }

                // Right Section
                Column {
                    AutoResizedText(
                        text = "Jumlah Hari Penyewaan",
                        color = gray,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 10.nonScaledSp,
                        ),
                        modifier = Modifier
                    )
                    AutoResizedText(
                        text = "${booking.days} Hari",
                        color = secondary,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 10.nonScaledSp,
                        ),
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AutoResizedText(
                        text = "Check Out",
                        color = gray,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 10.nonScaledSp,
                        ),
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .border(
                                1.dp, Color(0xFFB71C1C),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Row(modifier=Modifier
                            .align(Alignment.Center)){
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = primary
                            )
                            if (endDate != null) {
                                AutoResizedText(
                                    text = endDate,
                                    color = primary,
                                    style = TextStyle(
                                        fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                        fontSize = 10.nonScaledSp,
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun ListRoom(
    bookingViewModel: BookingViewModel,
    onClick: () -> Unit,
    ondeleteClick: () -> Unit,
    onJumlahKamar:(Int)->Unit,
    modifier: Modifier ) {
    val bookingListbyIdState by bookingViewModel.getBookingListbyIdState.collectAsStateWithLifecycle()
    Column(modifier = modifier
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White)
        .padding(10.dp)
        ) {
        when (bookingListbyIdState) {
            is Resource.Error -> {

            }
            is Resource.ErrorMessage -> {

            }
            Resource.Idle -> {

            }
            Resource.Loading -> {

            }
            is Resource.Success -> {
                val roomData =
                    (bookingListbyIdState as Resource.Success<BookingListResponse>).data?.data

                var jumlahdata = roomData?.size
                onJumlahKamar(jumlahdata!!)
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(roomData.orEmpty()) { room ->
                       RoomItem(booking = room,modifier)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Box (
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(yellow2)
                    .height(40.dp)
                    .weight(1f)
                    .padding(10.dp)
                    .clickable {
                        onClick()
                    }
            ) {
                Row(modifier=Modifier
                    .align(Alignment.Center)){
                    Icon(
                        painter = painterResource(R.drawable.ic_reschedule),
                        contentDescription = null,
                        tint = white,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(30.dp)
                    )
                        AutoResizedText(
                            text = "Reschedule",
                            color = white,
                            style = TextStyle(
                                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                fontSize = 12.nonScaledSp,
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )

                }

            }
            Spacer(Modifier.width(10.dp))
            Box (
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(primary)
                    .height(40.dp)
                    .weight(1f)
                    .padding(10.dp)
                    .clickable {
                        ondeleteClick()
                    }
            ) {
                Row(modifier=Modifier
                    .align(Alignment.Center)){
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = null,
                        tint = white,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(30.dp)
                    )
                    AutoResizedText(
                        text = "Delete",
                        color = white,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 12.nonScaledSp,
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )

                }

            }
        }
    }
}

@Composable
private fun RoomItem(
    booking: BookingList,
    modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier) {
            AutoResizedText(
                text = "Nomor Ruangan",
                color = gray,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    fontSize = 10.nonScaledSp,
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(5.dp))
            AutoResizedText(
                text = booking.Room.room_number,
                color = secondary,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 14.nonScaledSp,
                ),
                modifier = Modifier
            )
        }
        Column(modifier = Modifier) {
            AutoResizedText(
                text = "Nomor Pesanan",
                color = gray,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    fontSize = 10.nonScaledSp,
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(5.dp))
            AutoResizedText(
                text = booking.nomor_pesanan,
                color = secondary,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 14.nonScaledSp,
                ),
                modifier = Modifier
            )
        }
    }
}
@Composable
fun TicketShapeCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(8.dp)
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {

            val cornerRadius = 16.dp.toPx()
            val circleRadius = 12.dp.toPx()
            val ticketWidth = size.width
            val ticketHeight = size.height

            // Bagian atas tiket (rounded rectangle)
            drawRoundRect(
                color = Color.White,
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )

            // Garis horizontal (dash line)
            val dashWidth = 10.dp.toPx()
            val gapWidth = 10.dp.toPx()
            val lineY = ticketHeight / 2.5
            var startX = 0f

            while (startX < ticketWidth) {
                val endX = (startX + dashWidth).coerceAtMost(ticketWidth) // Batasi panjang segmen ke ticketWidth

                drawLine(
                    color = background2,
                    start = Offset(startX, lineY.toFloat()),
                    end = Offset(endX, lineY.toFloat()),
                    strokeWidth = 2f
                )

                startX += dashWidth + gapWidth
            }
            // Setengah lingkaran di sisi kiri
            drawArc(
                color = white,
                startAngle = -90f, // Mulai dari atas
                sweepAngle = 180f, // Gambar setengah lingkaran
                useCenter = false, // Jangan tutup ke tengah
                topLeft = Offset(
                    x = -circleRadius, // Posisikan setengah lingkaran keluar dari kiri
                    y = ((ticketHeight / 2.5) - circleRadius).toFloat()
                ),
                size = Size(circleRadius * 2, circleRadius * 2) // Ukuran lingkaran penuh
            )

            // Setengah lingkaran di sisi kanan
            drawArc(
                color = white,
                startAngle = 90f, // Mulai dari bawah
                sweepAngle = 180f, // Gambar setengah lingkaran
                useCenter = false, // Jangan tutup ke tengah
                topLeft = Offset(
                    x = ticketWidth - circleRadius, // Posisikan setengah lingkaran ke kanan
                    y = ((ticketHeight / 2.5) - circleRadius).toFloat()
                ),
                size = Size(circleRadius * 2, circleRadius * 2) // Ukuran lingkaran penuh
            )

        }
        Box(modifier = Modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(primary)
            .fillMaxWidth()
            .height(10.dp)

        )
        // Isi konten tiket
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun TicketItem() {
    val ticketWidth = 350.dp
    val ticketHeight = 200.dp

    Box(
        modifier = Modifier
            .width(ticketWidth)
            .wrapContentHeight()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val widthPx = size.width
            val heightPx = size.height
            val radiusPx = 12.dp.toPx()

            // Bagian atas dengan warna merah
            drawRoundRect(
                color = Color(0xFFB71C1C),
                topLeft = Offset(0f, 0f),
                size = Size(widthPx, heightPx * 0.2f),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Setengah lingkaran di sisi kiri
            drawArc(
                color = Color.LightGray,
                startAngle = -90f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(-radiusPx, heightPx / 2 - radiusPx),
                size = Size(radiusPx * 2, radiusPx * 2),
                style = Stroke(width = radiusPx)
            )

            // Setengah lingkaran di sisi kanan
            drawArc(
                color = Color.LightGray,
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(widthPx - radiusPx, heightPx / 2 - radiusPx),
                size = Size(radiusPx * 2, radiusPx * 2),
                style = Stroke(width = radiusPx)
            )

            // Garis putus-putus
            val dashWidth = 20.dp.toPx()
            val gapWidth = 10.dp.toPx()
            var startX = 0f
            val lineY = heightPx / 2
            while (startX < widthPx) {
                val endX = (startX + dashWidth).coerceAtMost(widthPx)
                drawLine(
                    color = Color.Gray,
                    start = Offset(startX, lineY),
                    end = Offset(endX, lineY),
                    strokeWidth = 2f
                )
                startX += dashWidth + gapWidth
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header section
            Text(
                text = "ID BOOKING : GAB10521124",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gedung A",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pembelian Berhasil",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Section
                Column {
                    Text(
                        text = "Jumlah Ruang/Kamar",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "3 Kamar",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Check In",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color(0xFFB71C1C), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "19 Desember 2024",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB71C1C)
                        )
                    }
                }

                // Right Section
                Column {
                    Text(
                        text = "Jumlah Hari Penyewaan",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "3 Hari",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Check In",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color(0xFFB71C1C), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "19 Desember 2024",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB71C1C)
                        )
                    }
                }
            }
        }
    }
}

