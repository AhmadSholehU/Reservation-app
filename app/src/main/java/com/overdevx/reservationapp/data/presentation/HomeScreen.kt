package com.overdevx.reservationapp.data.presentation

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.Building
import com.overdevx.reservationapp.data.presentation.monitoring.admin.ErrorItem
import com.overdevx.reservationapp.data.presentation.monitoring.admin.Loading
import com.overdevx.reservationapp.data.presentation.monitoring.admin.LoadingShimmerEffect
import com.overdevx.reservationapp.data.presentation.monitoring.auth.AuthViewModel
import com.overdevx.reservationapp.ui.theme.gray2
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.red2
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.white2
import com.overdevx.reservationapp.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onClick: (Int, String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: BuildingViewModel = hiltViewModel()
) {
    val buildingState by viewModel.buildingState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            delay(2000)
            viewModel.fetchBuilding()
            isRefreshing = false
        }
    }
    val buildingImages = mapOf(
        1 to R.drawable.img_a, // building_id to image resource
        2 to R.drawable.img_b,
        3 to R.drawable.img_lainya
    )

    val buildingRoomCounts = mapOf(
        1 to "12 Kamar",
        2 to "15 Kamar",
        3 to "10 Kamar"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopBarSection(
            onNavigateBack = { onNavigateBack() },
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        PullToRefreshBox(
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ) {
            when (buildingState) {
                is Resource.Loading -> {
                    LoadingShimmerEffect()
                }

                is Resource.Success -> {
                    val buildings = (buildingState as Resource.Success<List<Building>>).data
                    if (buildings != null) {
                        if (buildings.isEmpty()) {
                            Text(text = "No building available")
                        } else {
                            LazyColumn(
                                modifier = modifier
                                    .fillMaxSize()
                                    .padding(10.dp)

                            ) {
                                items(buildings, key = { it.building_id }) { building ->
                                    val imageResId = buildingImages[building.building_id] ?: R.drawable.img_placeholder
                                    val roomCount = buildingRoomCounts[building.building_id] ?: "Unknown Rooms"

                                    BuildingItem(
                                        onClick = {
                                            onClick(
                                                building.building_id,
                                                building.name
                                            )
                                        },
                                        building = building,
                                        imageResId = imageResId,
                                        roomCount = roomCount
                                    )
                                }
                            }

                        }


                    }
                }

                is Resource.ErrorMessage -> {
                    val errorMessage = (buildingState as Resource.ErrorMessage).message
                    Text(text = "Error: $errorMessage")
                    Log.e("HomeScreen", "Error: $errorMessage")
                }

                is Resource.Error -> {

                    // Handle error dari Exception
                    val exceptionMessage =
                        (buildingState as Resource.Error).exception.message
                            ?: "Unknown error occurred"
                    ErrorItem(errorMsg = exceptionMessage)


                }


                is Resource.Idle -> {
//                        LaunchedEffect(Unit) {
//                            viewModel.fetchBuilding()
//                        }
                }

                else -> {}

            }
        }
    }

}

@Composable
private fun TopBarSection(
    onNavigateBack: () -> Unit,
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
        Row(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "Control Ruang",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 22.sp,
                color = secondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

    }
}

@Composable
fun HeaderSection(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    buildingViewModel: BuildingViewModel,
    onLogoutClick: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    Row(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.img_smg),
            contentDescription = "Logo",
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            Text(
                text = "ASRAMA BALAI DIKLAT",
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 20.sp,
                color = secondary,
                letterSpacing = 3.sp,
                modifier = Modifier
            )
            Text(
                text = "Kota Semarang",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 16.sp,
                color = secondary,
                modifier = Modifier
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(40.dp),
            colors = IconButtonDefaults.iconButtonColors(Color.Transparent)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = null,
                tint = secondary
            )
        }

        if (showDialog) {
            Column(modifier = Modifier) {
                AlertDialog(
                    onDismissRequest = {

                    }, // Menutup dialog saat di luar dialog ditekan
                    title = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logout),
                                contentDescription = null,
                                tint = primary,
                                modifier = Modifier
                                    .size(55.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }

                    },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Log Out",
                                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                fontSize = 24.sp,
                                color = primary,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Are you sure you want to log out?",
                                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                fontSize = 16.sp,
                                color = white2,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDialog = false
                            },
                            modifier = Modifier
                                .height(55.dp)
                                .fillMaxWidth()
                                .align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(primary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "No, continue to app",
                                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                fontSize = 20.sp,
                                color = white,
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.logoutUser()
                                buildingViewModel.resetBuildingState()
                                onLogoutClick()
                                showDialog = false
                            },
                            modifier = Modifier
                                .height(55.dp)
                                .fillMaxWidth()
                                .align(Alignment.End),
                            border = BorderStroke(1.dp, primary),
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Yes, log me out",
                                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                fontSize = 20.sp,
                                color = white,
                            )
                        }

                    },
                    containerColor = secondary
                )
            }

        }
    }
}

@Composable
fun BuildingItem(
    onClick: (Int) -> Unit,
    building: Building,
    imageResId: Int,
    roomCount: String ) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(brush = Brush.verticalGradient(
            colors = listOf(
                primary,
                red2
            )
        ))
        .clickable { onClick(building.building_id) }) {
        Image(
            painter = painterResource(id = R.drawable.ic_wave),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
            alignment = Alignment.BottomEnd
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center)) {
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(imageResId),
                contentDescription = null,
                alignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(100.dp)

            )
            Spacer(modifier = Modifier.width(10.dp))
            Spacer(modifier = Modifier.weight(1f))
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    text = "Gedung",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 20.sp,
                    color = white,
                    modifier = Modifier
                )
                Text(
                    text = building.name,
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 30.sp,
                    color = white,
                    modifier = Modifier
                )

                Row(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(white.copy(0.5f))
                    .padding(start = 5.dp, end = 5.dp)){
                    Text(
                        text = roomCount,
                        fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                        fontSize = 14.sp,
                        color = white,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                }
            }
            Spacer(modifier = Modifier.width(50.dp))
        }


    }
}