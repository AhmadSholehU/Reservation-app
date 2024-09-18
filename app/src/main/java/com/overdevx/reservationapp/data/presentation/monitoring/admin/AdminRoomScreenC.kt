package com.overdevx.reservationapp.data.presentation.monitoring.admin

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.Room
import com.overdevx.reservationapp.data.presentation.RoomsViewModel
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.green
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.utils.Resource

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

    var days by remember { mutableStateOf(0) }
    var room_id by remember { mutableStateOf(0) }
    var room_status by remember { mutableStateOf("Tersedia") }

    val bookingState by viewModelBooking.bookingState.collectAsState()
    val updateRoomState by viewModelBooking.updateRoomState.collectAsState()
    Column(modifier = modifier.padding(16.dp)) {
        TopBarSection(onNavigateBack = { onNavigateBack() }, buildingName = buildingName)
        Spacer(modifier = Modifier.height(16.dp))
        RoomSection(viewModel = viewModel, buildingId = buildingId, selectedRoomNumber = selectedRoomNumber) {roomNumber,roomId->
            selectedRoomNumber = roomNumber
            showDialog = roomNumber != null
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
                        if(room_status == "Terbooking"){
                            viewModelBooking.bookRoom(room_id, days)
                        }else{
                            viewModelBooking.updateRoomStatus(room_id, statusId)
                        }
                    }
                },
                onStatusSelected = { status ->
                    room_status = status  // Update selected status di parent
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
                        color = primary)
                }

            }
            is Resource.Success -> {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text="Update Status successful for room $selectedRoomNumber",
                        fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                        fontSize = 22.sp,
                        color = secondary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
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
                Text(
                    text = "Update Status successful for room $selectedRoomNumber",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    fontSize = 22.sp,
                    color = secondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            is Resource.ErrorMessage -> {
                Text("Error: ${(updateRoomState as Resource.ErrorMessage).message}")
            }

            else -> {}
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
private fun RoomSection(
    modifier: Modifier = Modifier,
    viewModel: RoomsViewModel,
    buildingId: Int,
    selectedRoomNumber: String?,
    onRoomSelected: (String?,Int?) -> Unit
) {

    val roomState by viewModel.roomState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRooms(buildingId)
    }

    when (roomState) {
        is Resource.Loading -> {
            CircularProgressIndicator()
        }

        is Resource.Success -> {
            val rooms = (roomState as Resource.Success<List<Room>>).data
            if (rooms != null) {
                if (rooms.isEmpty()) {
                    Text(text = "No rooms available")
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // LazyColumn untuk ruangan dengan typeId selain 2 dan 3
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(rooms.filter {  it.room_type_id != 3 }) { room ->
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
                                            if (selectedRoomNumber == room.room_number) null else room.room_number,room.room_id
                                        )
                                    }
                                )
                            }
                            item{
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
                                                    if (selectedRoomNumber == room.room_number) null else room.room_number,room.room_id
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
            val exceptionMessage = (roomState as Resource.Error).exception.message ?: "Unknown error occurred"
            ErrorItem(errorMsg = exceptionMessage)
        }

        else -> {}
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