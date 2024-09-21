package com.overdevx.reservationapp.data.presentation.monitoring

import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.Monitoring
import com.overdevx.reservationapp.data.model.Room
import com.overdevx.reservationapp.data.presentation.monitoring.admin.ErrorItem
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.green
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.white2
import com.overdevx.reservationapp.ui.theme.yellow
import com.overdevx.reservationapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun MonitoringScreen(
    modifier: Modifier = Modifier,
    viewModel: MonitoringViewModel = hiltViewModel(),
    onClick: (Int) -> Unit,

) {
    val roomState by viewModel.monitoringState.collectAsStateWithLifecycle()
    val roomCount by viewModel.roomCounts.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        TopBarSection()
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(16.dp))
                        .background(white, shape = RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    when (roomState) {
                        is Resource.Loading -> {
                            CircularProgressIndicator()
                        }

                        is Resource.ErrorMessage -> {
                            val errorMessage = (roomState as Resource.ErrorMessage).message
                            Log.e("MONITORING", errorMessage)
                            Text(text = "Error: $errorMessage")
                        }

                        is Resource.Success -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                roomCount
                                    .filter { it.building_name != "Gedung C" }
                                    .forEach { building ->
                                        GedungSection(
                                            modifier = Modifier.weight(1f),
                                            availble = building.room_status.available.count
                                                ?: 0,        // Nilai default 0
                                            notAvailble = building.room_status.not_available.count
                                                ?: 0, // Nilai default 0
                                            booked = building.room_status.booked.count
                                                ?: 0,             // Nilai default 0
                                            buildingName = building.building_name
                                                ?: "Unknown Building",  // Nilai default "Unknown Building"
                                            onClick = {
                                                onClick(
                                                    building.building_id ?: 0
                                                )
                                            }             // Nilai default 0
                                        )

                                    }


                            }
                            val gedungC = roomCount.find { it.building_name == "Gedung C" }
                            gedungC?.let { building ->
                                KelasSection(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp),
                                    viewModel = viewModel
                                )
                            }


                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                roomCount.filter { it.building_name == "Gedung C" }
                                    .forEach { building ->
                                        val filteredRooms = building.rooms.filterNot {
                                            it.room_type == "ruang"
                                        }
                                        filteredRooms.forEach { room ->
                                            TransitSection(
                                                modifier = Modifier.weight(1f),
                                                room_name = room.room_name,
                                                room_status = room.room_status,
                                            )
                                        }
                                    }

                            }
                        }

                        is Resource.Error -> {
                            // Handle error dari Exception
                            val exceptionMessage = (roomState as Resource.Error).exception.message ?: "Unknown error occurred"
                            ErrorItem(errorMsg = exceptionMessage)
                        }

                        is Resource.Idle -> {
                            LaunchedEffect(Unit) {
                                viewModel.fetchMonitoring()
                            }
                        }

                        else -> {}
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
            }

        }

    }
}
@Composable
private fun TopBarSection(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()
            .align(Alignment.Center)) {
            Text(
                text = "Monitoring Ruang",
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
private fun GedungSection(
    modifier: Modifier = Modifier,
    availble: Int,
    notAvailble: Int,
    booked: Int,
    buildingName: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .width(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(primary)
            .clickable { onClick() }
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "$buildingName",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 20.sp,
            color = white,
            modifier = Modifier.padding(start = 10.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Status Ruang :",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 16.sp,
            color = white,
            modifier = Modifier.padding(start = 10.dp)
        )
        Spacer(modifier = Modifier.height(1.dp))
        val roomStatusList = listOf(
            Triple("Terbooking", booked, yellow),
            Triple("Tidak Tersedia", notAvailble, green),
            Triple("Tersedia", availble, gray)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            roomStatusList.forEach { (statusText, count, color) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "$count $statusText",
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 14.sp,
                        color = white,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp)) // Spacer untuk jarak antar row
            }
        }


    }
}

@Composable
private fun KelasSection(modifier: Modifier = Modifier, viewModel: MonitoringViewModel) {
    val roomCount by viewModel.roomCounts.collectAsStateWithLifecycle()


    roomCount.filter { it.building_name == "Gedung C" }
        .forEach { building ->
            val filteredRooms = building.rooms.filterNot {
                it.room_type == "lapangan"
            }
            var availble = building.room_status.available.count
            var notAvailble = building.room_status.not_available.count
            var booked = building.room_status.booked.count
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primary)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Ruang Kelas",
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 20.sp,
                        color = white,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Kapasitas Maksimal per Ruang 100 Orang",
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 16.sp,
                        color = white2,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(yellow)
                                .align(Alignment.CenterVertically)

                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$booked Terbooking",
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 14.sp,
                            color = white,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(green)
                                .align(Alignment.CenterVertically)

                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$availble Tersedia",
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 14.sp,
                            color = white,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(5.dp))

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(gray)
                                .align(Alignment.CenterVertically)

                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$notAvailble Tidak Tersedia",
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 14.sp,
                            color = white,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                    }

                }

                filteredRooms.forEach { room ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(100.dp)
                            .background(secondary)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = room.room_name,
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            color = white,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                            Text(
                                text = "Kapasitas Maksimal 100 Orang",
                                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                fontSize = 16.sp,
                                color = white2,
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Row {

                                Text(
                                    text = "Status Ruang: ",
                                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                    fontSize = 14.sp,
                                    color = white,
                                )

                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(green)
                                        .align(Alignment.CenterVertically)

                                )

                                Spacer(modifier = Modifier.width(5.dp))

                                Text(
                                    text = room.room_status,
                                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                    fontSize = 14.sp,
                                    color = white,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }

                        }

                        Spacer(modifier = Modifier.width(10.dp))


                    }
                    HorizontalDivider(
                        modifier = Modifier,
                        thickness = 1.dp,
                        color = white
                    )
                }
            }


        }
}


@Composable
private fun TransitSection(modifier: Modifier = Modifier, room_name: String, room_status: String) {

    Column(
        modifier = modifier
            .width(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(primary)
            .padding(10.dp)
    ) {

        Text(
            text = room_name,
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 20.sp,
            color = white
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Status Ruang :",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 16.sp,
            color = white
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(yellow)
                    .align(Alignment.CenterVertically)

            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = room_status,
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 14.sp,
                color = white
            )
        }


    }



}


