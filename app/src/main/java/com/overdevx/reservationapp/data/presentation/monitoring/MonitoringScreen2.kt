package com.overdevx.reservationapp.data.presentation.monitoring

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.Monitoring
import com.overdevx.reservationapp.data.presentation.home.nonScaledSp
import com.overdevx.reservationapp.data.presentation.monitoring.admin.BookingViewModel
import com.overdevx.reservationapp.data.presentation.monitoring.admin.ErrorItem
import com.overdevx.reservationapp.data.presentation.monitoring.admin.Loading
import com.overdevx.reservationapp.data.presentation.monitoring.admin.LoadingDialog
import com.overdevx.reservationapp.data.presentation.monitoring.admin.LoadingShimmerEffect
import com.overdevx.reservationapp.data.presentation.monitoring.auth.ErrorDialog
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.green
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.utils.AutoResizedText
import com.overdevx.reservationapp.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringScreen2(
    modifier: Modifier = Modifier,
    viewModel: MonitoringViewModel = hiltViewModel(),

    onClick: (Int) -> Unit,
) {
    val roomState by viewModel.monitoringState.collectAsStateWithLifecycle()
    val roomCount by viewModel.roomCounts.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            delay(2000)
            viewModel.fetchMonitoring()
            isRefreshing = false
        }
    }

    Column(modifier = modifier.padding(bottom = 10.dp)) {
        TopBarSection()
        Spacer(modifier = Modifier.height(16.dp))
        PullToRefreshBox(
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ) {

            when (roomState) {
                is Resource.Loading -> {
                    LoadingShimmerEffect()
                }

                is Resource.ErrorMessage -> {
                    val errorMessage = (roomState as Resource.ErrorMessage).message
                    Log.e("MONITORING", errorMessage)
                    Text(text = "Error: $errorMessage")
                }

                is Resource.Error -> {
                    // Handle error dari Exception
                    val exceptionMessage =
                        (roomState as Resource.Error).exception.message
                            ?: "Unknown error occurred"
                    ErrorItem(errorMsg = exceptionMessage)
                }

                is Resource.Success -> {
                    val monitoringList = (roomState as Resource.Success<List<Monitoring>>).data

                    LazyColumn() {
                        item {
                            roomCount
                                .filter { it.building_name != "Gedung C" }
                                .forEach { building ->
                                    ItemSection(
                                        availble = building.room_status.available.count
                                            ?: 0,        // Nilai default 0
                                        notAvailble = building.room_status.not_available.count
                                            ?: 0, // Nilai default 0
                                        booked = building.room_status.booked.count
                                            ?: 0,             // Nilai default 0
                                        buildingName =
                                        when(building.building_name){
                                            "Gedung A" -> "Kamar Gedung Asrama A"
                                            "Gedung B" -> "Kamar Gedung Asrama B"
                                            else ->  building.building_name
                                        }
                                       ,
                                        buildingImage =
                                        when (building.building_id) {
                                            1 -> R.drawable.img_gedunga
                                            2 -> R.drawable.img_gedungb
                                            else -> R.drawable.img_gedunga
                                        },
                                        onClick = {
                                            onClick(
                                                building.building_id ?: 0
                                            )
                                        }
                                    )
                                }

                            val gedungC =
                                roomCount.find { it.building_name == "Gedung C" }
                            gedungC?.let { building ->
//                                val filteredRooms = building.rooms.filterNot {
//                                    it.room_type == "ruang"
//                                }
                                building.rooms.forEach { room ->
                                    ItemSection2(
                                        roomName = room.room_name,
                                        statusRoom = room.room_status,
                                        roomImage =
                                        when (room.room_name) {
                                            "Lapangan" -> R.drawable.img_lap
                                            "R.Transit" -> R.drawable.img_transit
                                            "RRKecilA" -> R.drawable.img_meetkecil
                                            "RRKecilB" -> R.drawable.img_meetkecil
                                            "RRBesarC" -> R.drawable.img_meetbesar
                                            "RRBesarAB" -> R.drawable.img_meetbesar
                                            "GPABC" -> R.drawable.img_pertemuan
                                            else -> R.drawable.img_sample
                                        },
                                        onClick = {

                                        }
                                    )
                                }
                            }


                        }
                    }
                }

                is Resource.Idle -> {

                }

                else -> {}
            }

        }
    }
}

@Composable
private fun TopBarSection(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
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
private fun ItemSection(
    modifier: Modifier = Modifier,
    availble: Int,
    notAvailble: Int,
    booked: Int,
    buildingName: String,
    buildingImage: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { }

    ) {
        Image(
            painter = painterResource(id = buildingImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            secondary.copy(0.3f),
                            secondary.copy(0.3f),
                            secondary
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
                .height(200.dp)
                .align(Alignment.BottomCenter)
        ) {
        }
        Column(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.BottomStart), verticalArrangement = Arrangement.Bottom
        ) {
            AutoResizedText(
                text = "$buildingName",
                color = white,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 20.nonScaledSp,
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            AutoResizedText(
                text = "Status Ruang :",
                color = white,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 12.nonScaledSp,
                ),
                modifier = Modifier
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(green)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(5.dp))
                AutoResizedText(
                    text = "$availble Tersedia",
                    color = white,
                    style = TextStyle(
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 10.nonScaledSp,
                    ),
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
                AutoResizedText(
                    text = "$notAvailble Tidak Tersedia",
                    color = white,
                    style = TextStyle(
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 10.nonScaledSp,
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(primary)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(5.dp))
                AutoResizedText(
                    text = "$booked Booked",
                    color = white,
                    style = TextStyle(
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 10.nonScaledSp,
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    onClick()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                ),
                modifier = Modifier
                    .height(45.dp)
                    .fillMaxWidth()
            ) {
                AutoResizedText(
                    text = "Lihat Ketersediaan Kamar",
                    color = white,
                    style = TextStyle(
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 12.nonScaledSp,
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }


        }

    }

}

@Composable
private fun ItemSection2(
    modifier: Modifier = Modifier,
    statusRoom: String,
    roomName: String,
    roomImage:Int,
    onClick: () -> Unit
) {
    val boxcolor = if(statusRoom=="available") green else if(statusRoom=="not_available") gray else primary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }

    ) {
        Image(
            painter = painterResource(id = roomImage), // Replace with actual image
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            secondary.copy(0.3f),
                            secondary.copy(0.3f),
                            secondary
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
                .height(200.dp)
                .align(Alignment.BottomCenter)
        ) {
        }
        Column(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.BottomStart), verticalArrangement = Arrangement.Bottom
        ) {
            AutoResizedText(
                text = "$roomName",
                color = white,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 20.nonScaledSp,
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            AutoResizedText(
                text = "Status Ruang :",
                color = white,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 12.nonScaledSp,
                ),
                modifier = Modifier
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(boxcolor)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(5.dp))
                AutoResizedText(
                    text = "$statusRoom",
                    color = white,
                    style = TextStyle(
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 10.nonScaledSp,
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

            }

        }

    }

}
