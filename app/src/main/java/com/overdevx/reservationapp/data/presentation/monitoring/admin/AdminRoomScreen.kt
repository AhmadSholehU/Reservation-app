package com.overdevx.reservationapp.data.presentation.monitoring.admin

import android.util.Log
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
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
    viewModel: RoomsViewModel = hiltViewModel()
) {
    Column(modifier = modifier.padding(16.dp)) {
        TopBarSection(onNavigateBack = { onNavigateBack() }, buildingName)
        Spacer(modifier = Modifier.height(10.dp))
        InfoSection(buildingName)
        Spacer(modifier = Modifier.height(10.dp))
        RoomSection(viewModel = viewModel, buildingId =buildingId)
        Spacer(modifier = Modifier.weight(1f))
        ButtonSection()
    }
}

@Composable
private fun TopBarSection(onNavigateBack: () ->Unit,buildingName:String,modifier: Modifier = Modifier) {
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
private fun InfoSection(buildingName: String,modifier: Modifier = Modifier) {
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
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
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
private fun RoomSection(modifier: Modifier = Modifier, viewModel: RoomsViewModel, buildingId:Int) {
    val roomState by viewModel.roomState.collectAsState()
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
                            RoomAdminItem(modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 20.dp),room)
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
private fun RoomAdminItem(modifier: Modifier = Modifier,room: Room) {
    // State untuk menyimpan apakah item telah diklik
    var isClicked by remember { mutableStateOf(false) }

    val color = when (room.status_name) {
        "available" -> green
        "booked" -> primary
        "not_available" ->gray
        else -> secondary.copy(0.5f)
    }
    // Jika sudah diklik, ubah warna latar belakang menjadi hitam
    val backgroundColor = if (isClicked) secondary else color
    Row(
        modifier = modifier
            .size(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .then(
                if (room.status_name == "available") Modifier.clickable {
                    isClicked = !isClicked
                } else Modifier
            )
    ) {
        Column(modifier = Modifier
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
fun ButtonSection(modifier: Modifier = Modifier) {
    val isChecked = remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
      Row (Modifier.fillMaxWidth()){
          Text(
              text = "Kamar 104 terpilih",
              fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
              fontSize = 18.sp,
              color = secondary,
              textAlign = TextAlign.Center,
              modifier = Modifier.align(Alignment.CenterVertically)
          )
          Spacer(modifier = Modifier.weight(1f))
          Text(
              text = "Pilih Semua",
              fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
              fontSize = 18.sp,
              color = secondary,
              textAlign = TextAlign.Center,
              modifier = Modifier.align(Alignment.CenterVertically)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Checkbox(
              checked = isChecked.value,
              onCheckedChange = { checked ->
                  isChecked.value = checked
              },
              colors = CheckboxDefaults.colors(
                  checkedColor = primary
              ),
              modifier = Modifier.clip(RoundedCornerShape(15.dp))
          )
      }
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
            ,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primary,)
        ){
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
