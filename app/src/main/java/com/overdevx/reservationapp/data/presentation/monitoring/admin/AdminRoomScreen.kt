package com.overdevx.reservationapp.data.presentation.monitoring.admin

import android.annotation.SuppressLint
import android.os.Build
import android.os.Parcel
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.android.material.datepicker.CalendarConstraints
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.BookingRoomResponse
import com.overdevx.reservationapp.data.model.KetersediaanResponse
import com.overdevx.reservationapp.data.model.Room
import com.overdevx.reservationapp.data.presentation.RoomsViewModel
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.green
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.white2
import com.overdevx.reservationapp.utils.Resource
import com.overdevx.reservationapp.utils.convertDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AdminRoomScreen(
    modifier: Modifier = Modifier,
    buildingId: Int,
    buildingName: String,
    onNavigateBack: () -> Unit,
    viewModel: RoomsViewModel = hiltViewModel(),
    viewModelBooking: BookingViewModel = hiltViewModel()
) {
    // State untuk menyimpan ruangan yang dipilih
    var selectedRoomNumber by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    var days_change by remember { mutableStateOf(0) }
    var room_id by remember { mutableStateOf(0) }
    var room_status by remember { mutableStateOf("Tersedia") }
    var selected_date by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var onDateRangeSelected: (String, String) -> Unit = { _, _ -> }
    var current_room_status by remember { mutableStateOf("") }
    var booking_room_id by remember { mutableStateOf(0) }
    val unselectableDates = remember { mutableStateListOf<Long>() }

    val bookingState by viewModelBooking.bookingState.collectAsStateWithLifecycle()
    val updateRoomState by viewModelBooking.updateRoomState.collectAsStateWithLifecycle()
    val bookingRoomState by viewModelBooking.getBookingState.collectAsStateWithLifecycle()
    val updateBookingRoomState by viewModelBooking.updatatebookingState.collectAsStateWithLifecycle()
    val ketersediaanState by viewModelBooking.getKetersediaanState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(buildingId) {
        viewModel.fetchRooms(buildingId)
    }
    LaunchedEffect(selectedRoomNumber) {
        selectedRoomNumber?.let {
            viewModelBooking.getBookingRoom(room_id ?: return@LaunchedEffect)
            viewModelBooking.getKetersediaan(room_id ?: return@LaunchedEffect)
        }
    }
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        TopBarSection(onNavigateBack = { onNavigateBack() }, buildingName)
        Spacer(modifier = Modifier.height(10.dp))
        InfoSection(buildingName)
        Spacer(modifier = Modifier.height(10.dp))

        RoomSection(
            viewModel = viewModel,
            buildingId = buildingId,
            selectedRoomNumber = selectedRoomNumber,
            onRoomSelected = { selectedRoom, roomId, roomStatus ->
                selectedRoomNumber = selectedRoom
                if (roomStatus != null) {
                    current_room_status = roomStatus
                }
                if (roomId != null) {
                    room_id = roomId
                }
            },
            showDialog = { shouldShow ->
                // Perubahan nilai showDialog dilakukan di sini
                showDialog = shouldShow
            }

        )

        Row {
            // Menampilkan Snackbar dengan SnackbarHost
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.Bottom),
                snackbar = { snackbarData ->
                    Snackbar(
                        action = {
                            Text(
                                text = "Dismiss",
                                color = Color.White,
                                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                fontSize = 12.sp,
                                modifier = Modifier.clickable {
                                    snackbarData.dismiss()  // Menutup Snackbar saat di klik
                                }
                            )
                        },
                        modifier = Modifier.padding(16.dp),
                        containerColor = primary
                    ) {
                        Text(
                            text = snackbarData.visuals.message,
                            color = Color.White,
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 14.sp
                        )
                    }
                }
            )
        }

        // Tampilkan dialog jika showDialog bernilai true
        if (showDialog) {
            StatusDialog(
                selectedRoomNumber = selectedRoomNumber,
                onDismiss = { showDialog = false },
                buildingName = buildingName,
                onBooking = {
                    selectedRoomNumber?.let {
                        var statusId = when (room_status) {
                            "Tersedia" -> 1
                            "Tidak Tersedia" -> 2
                            "Terbooking" -> 3
                            else -> 1
                        }
                        // Differentiate between update and create booking based on initial and selected statuses
                        if (current_room_status == "booked" && room_status == "Terbooking") {
                            // Use update booking endpoint if already booked

                        } else if (current_room_status != "booked" && room_status == "Terbooking") {
                            // Use create booking endpoint if status changes to booked
                            Log.d("startDate", startDate)
                            viewModelBooking.bookRoom(room_id, startDate, endDate)
                        } else {
                            // Just update room status if not booking
                            viewModelBooking.updateRoomStatus(room_id, statusId)
                        }

                    }
                },
                onStatusSelected = { status ->
                    room_status = status  // Update selected status di parent
                },
                onDateSelected = { date ->
                    selected_date = date  // Update selected date di parent
                },
                onDaysChange = { days ->
                    days_change = days.toInt()
                },
                unselectableDates = unselectableDates,
                onDateRangeSelected = { Sd, Ed ->
                    startDate = Sd
                    endDate = Ed
                    Log.d("startDate", startDate)
                },
                modifier = modifier
            )
        }

        // Tampilkan status booking
        when (bookingState) {
            is Resource.Loading -> {
                Column(Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = primary
                    )
                }
            }

            is Resource.Success -> {
                showSuccessDialog = true
                if (showSuccessDialog) {
                    SuccessDialog(
                        onDismiss = { showDialog = false },
                        onClick = {
                            viewModelBooking.resetBookingState()
                            viewModel.fetchRooms(buildingId)
                            selectedRoomNumber = null
                        })
                }
            }

            is Resource.ErrorMessage -> {
                Text("Error: ${(bookingState as Resource.ErrorMessage).message}")
            }

            else -> {}
        }

        when (updateRoomState) {
            is Resource.Loading -> {
                Column(Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = primary
                    )
                }
            }

            is Resource.Success -> {
                showSuccessDialog = true
                SuccessDialog(
                    onDismiss = { showDialog = false },
                    onClick = {
                        viewModelBooking.resetBookingState()
                        viewModelBooking.resetUpdateState()
                        viewModel.fetchRooms(buildingId)
                        selectedRoomNumber = null
                    })
            }


            is Resource.ErrorMessage -> {
                Text("Error: ${(updateRoomState as Resource.ErrorMessage).message}")
            }

            else -> {}
        }

        when (bookingRoomState) {
            is Resource.Loading -> {
                // Show loading indicator if necessary
            }

            is Resource.Success -> {
                // Handle successful booking room data
                val bookingData = (bookingRoomState as Resource.Success<BookingRoomResponse>).data

                // Extract booking_room_id from the data
                booking_room_id = bookingData?.data?.booking_room_id ?: 0
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
                        // Ubah format SimpleDateFormat sesuai format API
                        val dateFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                        dateFormat.timeZone =
                            TimeZone.getTimeZone("UTC+8") // Pastikan waktu dalam zona UTC

                        val startDate = dateFormat.parse(ketersediaan.start_date)
                        val endDate = dateFormat.parse(ketersediaan.end_date)

                        if (startDate != null && endDate != null) {
                            val calendar = Calendar.getInstance()
                            // Normalisasi startDate
                            calendar.time = startDate
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)
                            var currentDate = calendar.timeInMillis

                            // Normalisasi endDate
                            calendar.time = endDate
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)
                            val normalizedEndDate = calendar.timeInMillis
                            // Tambahkan semua tanggal dari startDate hingga endDate
                            while (currentDate <= normalizedEndDate) {
                                unselectableDates.add(currentDate)
                                currentDate += 24 * 60 * 60 * 1000 // Tambah satu hari dalam milidetik
                            }
                            Log.d("DATE", unselectableDates.toString())
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

        when (updateBookingRoomState) {
            is Resource.Loading -> {
                Column(Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = primary
                    )
                }
            }

            is Resource.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Update Status successful",
                        duration = SnackbarDuration.Short
                    )
                    viewModelBooking.resetBookingState()
                    viewModelBooking.resetUpdateState()
                    viewModelBooking.resetUpdateBookingState()
                    viewModel.fetchRooms(buildingId)
                    selectedRoomNumber = null
                }
            }

            is Resource.ErrorMessage -> {
                Text("Error: ${(updateBookingRoomState as Resource.ErrorMessage).message}")
            }

            else -> {

            }
        }


    }

}


@Composable
private fun TopBarSection(
    onNavigateBack: () -> Unit,
    buildingName: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(16.dp))
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
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Silahkan Pilih Ruang\n $buildingName",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 22.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = secondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

    }
}

@Composable
private fun InfoSection(buildingName: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Kamar $buildingName - Lt 1",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 18.sp,
            color = secondary,
            modifier = Modifier
                .padding(start = 20.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(primary)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Terbooking",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 16.sp,
                color = secondary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(green)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Tersedia",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 16.sp,
                color = secondary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(gray)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Tidak Tersedia",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 16.sp,
                color = secondary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomSection(
    modifier: Modifier = Modifier,
    viewModel: RoomsViewModel,
    buildingId: Int,
    selectedRoomNumber: String?,
    onRoomSelected: (String?, Int?, String?) -> Unit,
    showDialog: (Boolean) -> Unit,
) {
    val roomState by viewModel.roomState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        scope.launch {
            delay(2000)
            viewModel.fetchRooms(buildingId)
            isRefreshing = false
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.weight(1f)
        ) {
            when (roomState) {
                is Resource.Loading -> {
                    LoadingShimmerEffect()
                    //RoomSkeletonGrid()
                }

                is Resource.Success -> {
                    val rooms = (roomState as Resource.Success<List<Room>>).data
                    if (rooms != null) {
                        // Filter hanya untuk room dengan status "available"
                        val availableRooms = rooms.filter { it.status_name != "booked" }

                        if (availableRooms.isEmpty()) {
                            EmptyItem()
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(100.dp),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                items(availableRooms, key = { it.room_id }) { room ->
                                    RoomAdminItem(
                                        modifier = Modifier.padding(5.dp),
                                        room = room,
                                        isSelected = selectedRoomNumber == room.room_number,
                                        onClick = {
                                            onRoomSelected(
                                                if (selectedRoomNumber == room.room_number) null else room.room_number,
                                                room.room_id,
                                                room.status_name
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                is Resource.ErrorMessage -> {
                    val errorMessage = (roomState as Resource.ErrorMessage).message
                    Text(text = "Error: $errorMessage")
                    Log.e("HomeScreen", "Error: $errorMessage")
                }

                is Resource.Error -> {
                    // Handle error dari Exception
                    val exceptionMessage =
                        (roomState as Resource.Error).exception.message ?: "Unknown error occurred"
                    ErrorItem(errorMsg = exceptionMessage)
                }

                else -> {}
            }

        }

        ButtonSection(
            selectedRoom = selectedRoomNumber,
            showDialog = showDialog,
            onShowDialog = {
                if (selectedRoomNumber != null) {
                    showDialog(true)
                }
            }
        )


    }

}

@Composable
private fun RoomAdminItem(
    modifier: Modifier = Modifier,
    room: Room,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = when (room.status_name) {
        "available" -> green
        "booked" -> primary
        "not_available" -> gray
        else -> secondary.copy(0.5f)
    }

    // Warna latar belakang tergantung apakah item sedang dipilih atau tidak
    val backgroundColor = if (isSelected) secondary else color
    Row(
        modifier = modifier
            .size(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable {
                onClick()
            }

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
                .padding(5.dp),
        ) {
            Text(
                text = "${room.room_number} ",
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 20.sp,
                color = white,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "${room.status_name} ",
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 16.sp,
                color = white,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun LoadingShimmerEffect() {
    fun Modifier.shimmerEffect(): Modifier = composed {
        val colors = listOf(
            Color.LightGray.copy(alpha = 0.3f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.3f),
        )
        val transition = rememberInfiniteTransition(label = "shimmer")
        val shimmerAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(animation = tween(1000, easing = LinearEasing)),
            label = "shimmer"
        )
        background(
            Brush.linearGradient(
                colors = colors,
                start = Offset.Zero,
                end = Offset(x = shimmerAnimation.value, y = shimmerAnimation.value * 2)
            )
        )
    }
    LazyColumn {
        items(8) {
            Row(
                Modifier
                    .fillMaxSize()
                    .background(white)
                    .clip(RoundedCornerShape(10.dp))

            ) {
                Row(Modifier.padding(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(height = 80.dp, width = 80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonSection(
    modifier: Modifier = Modifier,
    selectedRoom: String?,
    showDialog: (Boolean) -> Unit,
    onShowDialog: () -> Unit,
) {
    Spacer(modifier = Modifier.height(16.dp))
    Column(modifier = modifier.fillMaxWidth()) {
        if (selectedRoom != null) {
            Text(
                text = "Kamar $selectedRoom terpilih",
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 18.sp,
                color = secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onShowDialog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                )
            ) {
                Text(
                    text = "UBAH STATUS",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 18.sp,
                    letterSpacing = 5.sp,
                    color = white,
                )
            }
        }
    }
}

@Composable
fun StatusDialog(
    selectedRoomNumber: String?,
    onDismiss: () -> Unit,
    buildingName: String,
    onBooking: () -> Unit,
    onStatusSelected: (String) -> Unit,
    onDateSelected: (String) -> Unit,
    onDaysChange: (String) -> Unit,
    unselectableDates: MutableList<Long>,
    onDateRangeSelected: (String, String) -> Unit,
    modifier: Modifier
) {
    // State untuk menyimpan status dan waktu penyewaan yang dipilih
    var selectedStatus by remember { mutableStateOf("") }
    var rentalDuration by remember { mutableStateOf("1") }

    var showModal by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("Select date of birth") }

    var selectedStartDate by remember { mutableStateOf<String?>(null) }
    var selectedEndDate by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = "Silahkan pilih status $buildingName \n$selectedRoomNumber",
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
                Text(
                    text = "Status",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 16.sp,
                    color = secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.height(5.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf("Tersedia", "Tidak Tersedia", "Terbooking")) { status ->
                        StatusButton(
                            text = status,
                            isSelected = selectedStatus == status,
                            onClick = {
                                selectedStatus = status
                                onStatusSelected(status)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (selectedStatus == "Terbooking") {
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(secondary)
                            .padding(5.dp)
                            .clickable { showModal = true }
                    )
                    {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = null,
                            tint = white,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (selectedStartDate != null && selectedEndDate != null) {
                                "($selectedStartDate) - ($selectedEndDate)"
                            } else {
                                "Pilih tanggal penyewaan"
                            },
                            color = if (selectedDate == null) white2 else white,
                            fontFamily = FontFamily(listOf(Font(R.font.inter_regular))),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically),

                            )
                    }

                }

                if (showModal) {
//                    DatePickerModal(
//                        onDateSelected = {
//                            if (it != null) {
//                                selectedDate = convertDate(it)
//                                onDateSelected(selectedDate)
//                            }
//                            showModal = false
//                        },
//                        onDismiss = { showModal = false }
//                    )

//                    DateRangePickerSample()
                    DatePickerWithDateSelectableDatesSample(
                        unselectableDates,
                        onDateRangeSelected = { startDate, endDate ->
                            selectedStartDate = startDate
                            selectedEndDate = endDate
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
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = secondary,
                )
            ) {
                Text(
                    text = "KONFIRMASI",
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
fun SuccessDialog(
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_success),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)

                )
            }
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = "Status Ruang \n" +
                            "Berhasil Diperbarui",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 20.sp,
                    color = secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        onClick()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = secondary,
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(200.dp)
                ) {
                    Text(
                        text = "OKE",
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 16.sp,
                        color = white,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )
                }

            }

        },
        containerColor = white,
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
fun StatusButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = if (isSelected) {
            ButtonDefaults.buttonColors(containerColor = secondary, contentColor = Color.White)
        } else {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = secondary
            )
        },
        modifier = if (isSelected) Modifier.size(
            height = 40.dp,
            width = Dp.Unspecified
        ) else Modifier
            .border(1.dp, secondary, RoundedCornerShape(10.dp))
            .size(height = 40.dp, width = Dp.Unspecified)

    ) {
        Text(
            text = text,
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
        )
    }
}

@Composable
fun EmptyItem(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_empty),
            contentDescription = null,
            Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Tidak ada data yang ditampilkan !",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 18.sp,
            color = secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ErrorItem(errorMsg: String, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_error),
            contentDescription = null,
            Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "Oops, terjadi kesalahan saat memuat data ! \n${errorMsg}",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 18.sp,
            color = secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun Access(onLoginClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Image(
                painter = painterResource(id = R.drawable.img_admin),
                contentDescription = null,
                Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Anda harus login sebagai Admin",
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 18.sp,
                color = secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    onLoginClick()
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(height = Dp.Unspecified, width = 100.dp)
            ) {
                Text(
                    text = "Login",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 16.sp,
                    color = white,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

    }
}

@Composable
fun Loading() {
    Column(modifier = Modifier.fillMaxWidth()) {
        CircularProgressIndicator(
            color = primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading Data",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 20.sp,
            color = secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(
                    text = "OK", color = primary,
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 16.sp,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = white,
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 16.sp,
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = secondary,
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                todayDateBorderColor = primary,
                headlineContentColor = secondary,
                titleContentColor = secondary,
                currentYearContentColor = secondary,
                selectedDayContentColor = white,
                todayContentColor = primary,
                dividerColor = primary,
                dayContentColor = secondary,
                weekdayContentColor = primary,
                navigationContentColor = secondary,
                selectedYearContentColor = secondary,
                selectedYearContainerColor = primary,
                selectedDayContainerColor = primary,
                yearContentColor = secondary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerSample() {

    val state = rememberDateRangePickerState()
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        // Add a row with "Save" and dismiss actions.
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .background(DatePickerDefaults.colors().containerColor)
                .padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /* dismiss the UI */ }) {
                Icon(Icons.Filled.Close, contentDescription = "Localized description")
            }
            TextButton(
                onClick = {

                },
                enabled = state.selectedEndDateMillis != null
            ) {
                Text(text = "Save")
            }
        }
        DateRangePicker(state = state, modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDateSelectableDatesSample(
    unselectableDates: MutableList<Long>,
    onDateRangeSelected: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState =
        rememberDateRangePickerState(
            selectableDates =
            object : SelectableDates {
                // Cek apakah tanggal tersebut ada dalam daftar tanggal yang tidak bisa dipilih
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis !in unselectableDates
                }

                // Allow selecting dates from year 2023 forward.
                override fun isSelectableYear(year: Int): Boolean {
                    return year > 2022
                }
            }
        )
    var showAlertDialog by remember { mutableStateOf(false) }
    DatePickerDialog(
        modifier = Modifier.padding(16.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    // Hitung rentang hari yang dipilih
                    val selectedStart = datePickerState.selectedStartDateMillis
                    val selectedEnd = datePickerState.selectedEndDateMillis
                    if (selectedStart != null && selectedEnd != null) {
                        val daysDifference = (selectedEnd - selectedStart) / (24 * 60 * 60 * 1000)
                        if (daysDifference > 2) {
                            // Jika lebih dari 3 hari, tampilkan pesan
                            showAlertDialog = true
                        } else {
                            // Konversi timestamp ke format string
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val startDateStr = dateFormat.format(Date(selectedStart))
                            val endDateStr = dateFormat.format(Date(selectedEnd))

                            // Kirim nilai tanggal yang dipilih dalam format string
                            onDateRangeSelected(startDateStr, endDateStr)
                            onDismiss()
                        }
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        DateRangePicker(
            showModeToggle = false,
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                todayDateBorderColor = primary,
                headlineContentColor = secondary,
                titleContentColor = secondary,
                currentYearContentColor = secondary,
                selectedDayContentColor = white,
                todayContentColor = primary,
                dividerColor = primary,
                dayContentColor = secondary,
                weekdayContentColor = primary,
                navigationContentColor = secondary,
                selectedYearContentColor = secondary,
                selectedYearContainerColor = primary,
                selectedDayContainerColor = primary,
                yearContentColor = secondary,
                disabledDayContentColor= primary,
                dayInSelectionRangeContainerColor= primary.copy(0.3f),
            ))
    }

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_warning_date),
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                    },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Anda hanya dapat memilih rentang \n" +
                                "tanggal 3 hari",
                        fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                        fontSize = 18.sp,
                        color = secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                       onDismiss()
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(secondary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Baiklah",
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 20.sp,
                        color = white,
                    )
                }


            }
        )
    }
}


