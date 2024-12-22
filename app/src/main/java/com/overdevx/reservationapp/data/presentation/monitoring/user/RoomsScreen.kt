package com.overdevx.reservationapp.data.presentation.monitoring.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.Room
import com.overdevx.reservationapp.data.presentation.RoomsViewModel
import com.overdevx.reservationapp.data.presentation.home.nonScaledSp
import com.overdevx.reservationapp.data.presentation.monitoring.admin.EmptyItem
import com.overdevx.reservationapp.data.presentation.monitoring.admin.LoadingShimmerEffect
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.gray2
import com.overdevx.reservationapp.ui.theme.green
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.utils.AutoResizedText
import com.overdevx.reservationapp.utils.Resource

@Composable
fun RoomsScreen(
    modifier: Modifier = Modifier,
    buildingId: Int,
    onNavigateBack: () -> Unit,
    viewModel: RoomsViewModel = hiltViewModel(),
) {
    var buildingName = if(buildingId==1)"Gedung A" else "Gedung B"
    Column(
        modifier = modifier

    ) {
        Spacer(modifier = Modifier.height(10.dp))
        TopBarSection(onNavigateBack={onNavigateBack()},modifier = Modifier.padding(start = 16.dp, end = 16.dp))
        Spacer(modifier = Modifier.height(10.dp))
        InfoSection(buildingName,modifier = Modifier.padding(start = 16.dp, end = 16.dp))
        Spacer(modifier = Modifier.height(10.dp))
        RoomSection(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),viewModel,buildingId)
    }


}

@Composable
private fun TopBarSection(onNavigateBack: () -> Unit,modifier: Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { onNavigateBack()},
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
        Text(
            text = "Monitoring Ruang",
            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
            fontSize = 20.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = secondary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun InfoSection(buildingName: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Kamar $buildingName",
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
                    .border(1.dp, secondary, CircleShape)
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
private fun RoomSection(modifier: Modifier = Modifier,viewModel: RoomsViewModel,buildingId:Int) {
    val roomState by viewModel.roomState.collectAsStateWithLifecycle()
    LaunchedEffect(buildingId) {
        viewModel.fetchRooms(buildingId)
    }
    when(roomState){
        is Resource.Loading ->{
           LoadingShimmerEffect()
        }
        is Resource.Success ->{
            val rooms = (roomState as Resource.Success<List<Room>>).data
            if (rooms != null) {
                if (rooms.isEmpty()) {
                    EmptyItem()
                } else {
                    // Filter hanya untuk room dengan status "available"
                    val groupedAvailableRooms = rooms
                        .groupBy { room ->
                            if (room.building_name == "Gedung A") {
                                when {
                                    room.room_number.startsWith("A1") -> "Lantai 1"
                                    room.room_number.startsWith("A2") -> "Lantai 2"
                                    room.room_number.startsWith("A3") -> "Lantai 3"
                                    else -> "Lantai Lainnya"
                                }
                            } else {
                                when {
                                    room.room_number.startsWith("B1") -> "Lantai 1"
                                    room.room_number.startsWith("B2") -> "Lantai 2"
                                    room.room_number.startsWith("B3") -> "Lantai 3"
                                    else -> "Lantai Lainnya"
                                }
                            }
                        }
                    if (groupedAvailableRooms.isEmpty()) {
                        EmptyItem()
                    } else {
                        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                            groupedAvailableRooms.forEach { (floor, rooms) ->
                                // Header untuk setiap lantai
                                item {
                                    // Header untuk setiap lantai
                                    AutoResizedText(
                                        text = floor,
                                        color = secondary,
                                        style = TextStyle(
                                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                            fontSize = 12.nonScaledSp,
                                        ),
                                        modifier = Modifier.padding(vertical = 5.dp)
                                    )
                                }

                                // Grid untuk setiap room pada lantai tersebut
                                itemsIndexed(rooms.chunked(4)) {index, rowRooms ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        rowRooms.forEach { room ->
                                            RoomItem(
                                                modifier = Modifier
                                                    .padding(5.dp)
                                                    .weight(1f)
                                                    ,
                                                 room
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        is Resource.ErrorMessage ->{
            val errorMessage = (roomState as Resource.ErrorMessage).message
            Text(text = "Error: $errorMessage")
            Log.e("HomeScreen", "Error: $errorMessage")
        }

        else -> {}
    }
}

@Composable
fun RoomItem(modifier: Modifier = Modifier, room: Room) {
    val borderColor = when (room.status_name) {
        "available" -> secondary
        "booked" -> primary
        else -> gray2
    }

    val backgroundColor = if (room.status_name == "booked") primary else Color.Transparent
    val textColor = if (room.status_name == "booked") white else borderColor

    Box(
        modifier = modifier
            .size(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .background(backgroundColor)
    ) {
        AutoResizedText(
            text = room.room_number,
            color = textColor,
            style = TextStyle(
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 16.nonScaledSp,
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
