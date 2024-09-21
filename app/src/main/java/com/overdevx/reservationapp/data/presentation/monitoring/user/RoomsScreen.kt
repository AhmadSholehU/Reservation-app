package com.overdevx.reservationapp.data.presentation.monitoring.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.Room
import com.overdevx.reservationapp.data.presentation.RoomsViewModel
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.utils.Resource

@Composable
fun RoomsScreen(
    modifier: Modifier = Modifier,
    buildingId: Int,
    onNavigateBack: () -> Unit,
    viewModel: RoomsViewModel = hiltViewModel(),

) {

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        TopBarSection(onNavigateBack={onNavigateBack()},modifier = modifier)
        Spacer(modifier = Modifier.height(10.dp))
        InfoSection(modifier = modifier.padding(start = 16.dp, end = 16.dp))
        Spacer(modifier = Modifier.height(10.dp))
        RoomSection(modifier = modifier.padding(start = 16.dp, end = 16.dp),viewModel,buildingId)
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
            fontSize = 24.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = secondary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun InfoSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Kamar Gedung A- Lt 1",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 24.sp,
            color = primary,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(1.dp, secondary, RoundedCornerShape(5.dp))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Available",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 20.sp,
                color = secondary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(primary)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Booked",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 20.sp,
                color = primary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(1.dp, secondary.copy(0.5f), RoundedCornerShape(5.dp))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Taken",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 20.sp,
                color = secondary.copy(0.5f),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

    }
}

@Composable
private fun RoomSection(modifier: Modifier = Modifier,viewModel: RoomsViewModel,buildingId:Int) {
    val roomState by viewModel.roomState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
      viewModel.fetchRooms(buildingId)
    }
    when(roomState){
        is Resource.Loading ->{
            CircularProgressIndicator()
        }
        is Resource.Success ->{
            val rooms = (roomState as Resource.Success<List<Room>>).data
            if (rooms != null) {
                if (rooms.isEmpty()) {
                    Text(text = "No rooms available")
                } else {
                    LazyVerticalGrid(columns = GridCells.Adaptive(100.dp)
                    , modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                        items(rooms) { room ->
                            RoomItem(modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 20.dp),room)
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
        else -> secondary.copy(0.5f)
    }

    val backgroundColor = if (room.status_name == "booked") primary else Color.Transparent
    val textColor = if (room.status_name == "booked") white else borderColor

    Box(
        modifier = modifier
            .size(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable { }
    ) {
        Text(
            text = "${room.room_number} ",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 20.sp,
            color = textColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
