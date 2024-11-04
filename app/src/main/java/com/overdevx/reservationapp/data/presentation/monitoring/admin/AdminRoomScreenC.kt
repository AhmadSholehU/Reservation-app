package com.overdevx.reservationapp.data.presentation.monitoring.admin

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.overdevx.reservationapp.data.model.BookingRoomResponse
import com.overdevx.reservationapp.data.model.Room
import com.overdevx.reservationapp.data.presentation.RoomsViewModel
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.green
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AdminRoomScreenC(
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

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedRoomNumber) {
        selectedRoomNumber?.let {
            viewModelBooking.getBookingRoom(room_id ?: return@LaunchedEffect)
        }
    }
    Column(modifier = modifier.padding(16.dp)) {
        TopBarSection(onNavigateBack = { onNavigateBack() }, buildingName = buildingName)

        Spacer(modifier = Modifier.height(16.dp))

        RoomSection(viewModel = viewModel, buildingId = buildingId, selectedRoomNumber = selectedRoomNumber) {roomNumber,roomId,roomStatus->
            selectedRoomNumber = roomNumber
            showDialog = roomNumber != null
            if (roomStatus != null) {
                current_room_status = roomStatus
            }
            if (roomId != null) {
                room_id = roomId
            }
        }

        if (showDialog && selectedRoomNumber != null) {
            StatusDialog(
                selectedRoomNumber = selectedRoomNumber,
                onDismiss = { showDialog = false },
                buildingName = buildingName,
                onBooking = {
                    selectedRoomNumber?.let {
                        var statusId = when(room_status){
                            "Tersedia" -> 1
                            "Tidak Tersedia" -> 2
                            "Terbooking" -> 3
                            else -> 1
                        }
                        // Differentiate between update and create booking based on initial and selected statuses
                        if (current_room_status == "booked" && room_status == "Terbooking") {
                            // Use update booking endpoint if already booked
                            viewModelBooking.updateBookingRoom(booking_room_id, days_change, selected_date)
                        } else if (current_room_status != "booked" && room_status == "Terbooking") {
                            // Use create booking endpoint if status changes to booked
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
                    Log.d("startDate",startDate)
                },
                modifier = modifier
            )
        }

        when (bookingState) {
            is Resource.Loading -> {
                Column(Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = primary)
                }

            }
            is Resource.Success -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Update Status successful",
                            duration = SnackbarDuration.Short)
                        viewModelBooking.resetBookingState()
                        viewModel.fetchRooms(buildingId)
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
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Update Status successful",
                        duration = SnackbarDuration.Short)
                    viewModelBooking.resetBookingState()
                    viewModelBooking.resetUpdateState()
                    viewModel.fetchRooms(buildingId)
                }
            }

            is Resource.ErrorMessage -> {
                Text("Error: ${(updateRoomState as Resource.ErrorMessage).message}")
            }

            else -> {}
        }

        when (bookingRoomState) {
            is Resource.Loading -> {
               LoadingShimmerEffect()
            }
            is Resource.Success -> {
                // Handle successful booking room data
                val bookingData = (bookingRoomState as Resource.Success<BookingRoomResponse>).data

                // Extract booking_room_id from the data
                booking_room_id = bookingData?.data?.booking_room_id?:0
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
                }
            }

            is Resource.ErrorMessage -> {
                Text("Error: ${(updateBookingRoomState as Resource.ErrorMessage).message}")
            }

            else -> {}
        }

        Row{
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
                                fontFamily = FontFamily(listOf(Font(R.font.inter_regular))),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomSection(
    modifier: Modifier = Modifier,
    viewModel: RoomsViewModel,
    buildingId: Int,
    selectedRoomNumber: String?,
    onRoomSelected: (String?,Int?,String?) -> Unit
) {

    val roomState by viewModel.roomState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            delay(2000)
            viewModel.fetchRooms(buildingId)
            isRefreshing = false
        }
    }
    LaunchedEffect(buildingId) {
        delay(500)
        viewModel.fetchRooms(buildingId)
    }
    PullToRefreshBox(
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,

        ) {
        when (roomState) {
            is Resource.Loading -> {
                Loading()
            }

            is Resource.Success -> {
                val rooms = (roomState as Resource.Success<List<Room>>).data
                if (rooms != null) {
                    if (rooms.isEmpty()) {
                        EmptyItem()
                    } else {
                        Column(modifier = Modifier) {
                            // LazyColumn untuk ruangan dengan typeId selain 2 dan 3
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(rooms.filter { it.room_type_id != 3 }) { room ->
                                    RoomAdminItemC(
                                        modifier = Modifier.padding(
                                            start = 5.dp,
                                            end = 5.dp,
                                            top = 5.dp
                                        ),
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
                                item {
                                    val filteredRooms = rooms.filter { it.room_type_id == 3 }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 5.dp)
                                    ) {
                                        filteredRooms.forEach { room ->
                                            RoomAdminItemC2(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(horizontal = 5.dp),
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
                                item {
                                    InfoSection()
                                }
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
}

@Composable
private fun RoomAdminItemC(
    modifier: Modifier = Modifier,
    room: Room,
    isSelected: Boolean,
    onClick: () -> Unit
) {
   var shapeColor = when (room.status_name) {
        "available"-> green
       "booked"-> primary
       "not_available"-> gray
        else -> gray
    }
    Row(modifier = modifier
        .fillMaxWidth()
        .size(height = Dp.Unspecified, width = Dp.Unspecified)
        .clip(RoundedCornerShape(10.dp))
        .background(secondary)
        .clickable { onClick() }
        .padding(20.dp)
        ) {

        Column(modifier = Modifier
            .align(Alignment.CenterVertically)) {
            Text(
                text = room.room_number,
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 20.sp,
                color = white,
                textAlign = TextAlign.Start,
                modifier = Modifier
            )

            Text(
                text = "Kapasitas Maksimal 100 Orang",
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 14.sp,
                color = white.copy(0.5f),
                textAlign = TextAlign.Start,
                modifier = Modifier
            )
            Row (modifier = Modifier){
                Text(
                    text = "Status Ruang:",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 14.sp,
                    color = white,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.width(5.dp))
                Box(modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(shapeColor)
                    .align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = room.status_name,
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 14.sp,
                    color = white,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically))

                
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.ic_class),
            contentDescription = null,
            Modifier
                .size(90.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun RoomAdminItemC2(
    modifier: Modifier = Modifier,
    room: Room,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var shapeColor = when (room.status_name) {
        "available"-> green
        "booked"-> primary
        "not_available"-> gray
        else -> gray
    }
    Row(modifier = modifier
        .fillMaxWidth()
        .size(height = Dp.Unspecified, width = Dp.Unspecified)
        .clip(RoundedCornerShape(10.dp))
        .background(secondary)
        .clickable { onClick() }
        .padding(20.dp)
    ) {

        Column(modifier = Modifier
            .align(Alignment.CenterVertically)) {
            Text(
                text = room.room_number,
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 20.sp,
                color = white,
                textAlign = TextAlign.Start,
                modifier = Modifier
            )


                Text(
                    text = "Status Ruang:",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 14.sp,
                    color = white,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.width(5.dp))
            Row (modifier = Modifier){
                Box(modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(shapeColor)
                    .align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = room.status_name,
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 14.sp,
                    color = white,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically))


            }
        }
    }
}

@Composable
private fun InfoSection(modifier: Modifier = Modifier) {
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