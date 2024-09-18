package com.overdevx.reservationapp.data.presentation.monitoring.admin

import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.ui.AppBarConfiguration
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.Room
import com.overdevx.reservationapp.data.presentation.RoomsViewModel
import com.overdevx.reservationapp.data.presentation.monitoring.user.RoomItem
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.gray2
import com.overdevx.reservationapp.ui.theme.green
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.utils.Resource

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

    var days by remember { mutableStateOf(0) }
    var room_id by remember { mutableStateOf(0) }
    var room_status by remember { mutableStateOf("Tersedia") }

    val bookingState by viewModelBooking.bookingState.collectAsState()
    val updateRoomState by viewModelBooking.updateRoomState.collectAsState()

    Column(modifier = modifier.padding(16.dp)) {
        TopBarSection(onNavigateBack = { onNavigateBack() }, buildingName)
        Spacer(modifier = Modifier.height(10.dp))
        InfoSection(buildingName)
        Spacer(modifier = Modifier.height(10.dp))
        // Bagian RoomSection untuk menampilkan daftar ruangan
        RoomSection(
            viewModel = viewModel,
            buildingId = buildingId,
            selectedRoomNumber = selectedRoomNumber,
            onRoomSelected = { selectedRoom, roomId ->
                selectedRoomNumber = selectedRoom
                if (roomId != null) {
                    room_id = roomId
                }
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        // Bagian ButtonSection untuk menampilkan tombol UBAH STATUS
        ButtonSection(
            selectedRoom = selectedRoomNumber,
            showDialog = showDialog,
            onShowDialog = {
                if (selectedRoomNumber != null) {
                    showDialog = true
                }
            }
        )
        // Tampilkan dialog jika showDialog bernilai true
        if (showDialog) {
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

@Composable
private fun RoomSection(
    modifier: Modifier = Modifier,
    viewModel: RoomsViewModel,
    buildingId: Int,
    selectedRoomNumber: String?,
    onRoomSelected: (String?, Int?) -> Unit
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
                    EmptyItem()
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(100.dp),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    ) {
                        items(rooms) { room ->
                            RoomAdminItem(
                                modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 20.dp),
                                room = room,
                                isSelected = selectedRoomNumber == room.room_number,
                                onClick = {
                                    onRoomSelected(
                                        if (selectedRoomNumber == room.room_number) null else room.room_number,
                                        room.room_id
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
fun ButtonSection(
    modifier: Modifier = Modifier,
    selectedRoom: String?,
    showDialog: Boolean,
    onShowDialog: () -> Unit,

    ) {
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
    modifier: Modifier
) {
    // State untuk menyimpan status dan waktu penyewaan yang dipilih
    var selectedStatus by remember { mutableStateOf("Tersedia") }
    var rentalDuration by remember { mutableStateOf("1") }

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
                    Text(
                        text = "Waktu Penyewaan",
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 16.sp,
                        color = secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = rentalDuration,
                            onValueChange = { rentalDuration = it },
                            modifier = Modifier
                                .size(50.dp)
                                .border(
                                    1.dp,
                                    secondary,
                                    RoundedCornerShape(4.dp)
                                ) // Untuk memberikan tampilan seperti TextField
                                .padding(8.dp), // Padding agar teks tidak menempel ke border
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = TextStyle(
                                color = secondary,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                fontSize = 16.sp,
                            )
                        ) { innerTextField ->
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                if (rentalDuration.isEmpty()) {
                                    Text(
                                        text = "0",
                                        color = secondary,
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                        fontSize = 16.sp,
                                    ) // Placeholder
                                }
                                innerTextField() // Menampilkan konten dari BasicTextField
                            }
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Column(modifier = Modifier) {
                            IconButton(
                                onClick = {
                                    rentalDuration =
                                        (rentalDuration.toIntOrNull() ?: 2).plus(1).toString()
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Up",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    // Logika untuk mengurangi durasi
                                    rentalDuration =
                                        (rentalDuration.toIntOrNull()?.takeIf { it > 1 }
                                            ?: 2).minus(1)
                                            .toString()
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Down",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }

                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Lakukan sesuatu dengan status dan waktu penyewaan yang dipilih
                    onBooking()
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
    Column(modifier = Modifier.fillMaxSize()) {
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
    Column(modifier = Modifier.fillMaxSize()) {
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
