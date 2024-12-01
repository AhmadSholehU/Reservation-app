package com.overdevx.reservationapp.data.presentation.home

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarHalf
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.overdevx.reservationapp.BuildConfig
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.DetailService
import com.overdevx.reservationapp.data.presentation.monitoring.admin.ErrorItem
import com.overdevx.reservationapp.data.presentation.monitoring.admin.Loading
import com.overdevx.reservationapp.data.presentation.monitoring.admin.LoadingShimmerEffect
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.yellow
import com.overdevx.reservationapp.utils.ChangeBaseUrlScreen
import com.overdevx.reservationapp.utils.Resource
import com.overdevx.reservationapp.utils.formatCurrency
import com.overdevx.reservationapp.utils.replaceDomain
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUserScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onClick: (Int, String, String, Int, Int,String,String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val detailState by homeViewModel.detailServiceState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            delay(2000)
            homeViewModel.fetchDetailService()
            isRefreshing = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        HeaderSection(homeViewModel,
            Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                showDialog=true
            })
        Spacer(modifier = Modifier.height(16.dp))
        PullToRefreshBox(
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ) {
            when (detailState) {
                is Resource.Loading -> {
                    LoadingShimmerEffect()
                }

                is Resource.Error -> {
                    // Handle error dari Exception
                    val exceptionMessage =
                        (detailState as Resource.Error).exception.message
                            ?: "Unknown error occurred"
                    ErrorItem(errorMsg = exceptionMessage)
                }

                is Resource.ErrorMessage -> {

                }

                is Resource.Success -> {
                    val detailServiceList =
                        (detailState as Resource.Success<List<DetailService>>).data
                    //MainSection(onClick = { onClick() }, detailService = detailServiceList)

                    if (detailServiceList != null) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(detailServiceList, key = { it.detail_service_id }) {
                                Item(
                                    onClick = {
                                        onClick(
                                            it.detail_service_id,
                                            it.deskripsi,
                                            it.nama,
                                            it.harga,
                                            it.jumlah_kamar,
                                            it.rating.toString(),
                                            it.foto
                                        )
                                    },
                                    roomName = it.nama,
                                    rating = it.rating,
                                    harga = it.harga,
                                    jumlah_kamar = it.jumlah_kamar,
                                    foto = it.foto
                                )
                            }

                        }

                    }

                }

                else -> {}
            }
        }
        
        if(showDialog){
            ChangeBaseUrlDialog(
                baseUrl = homeViewModel.getBaseUrl(),
                onDismiss = {showDialog=false},
                onSave = { newUrl->
                    homeViewModel.saveBaseUrl(newUrl)
                    showDialog=false
                    //Toast.makeText(LocalContext.current, "Base URL updated!", Toast.LENGTH_SHORT).show()
                })
        }
    }
}


@Composable
private fun HeaderSection(homeViewModel: HomeViewModel, modifier: Modifier = Modifier,onClick: () -> Unit) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "ASRAMA BALAI DIKLAT",
            fontFamily = FontFamily(listOf(Font(R.font.inter_bold))),
            fontSize = 20.sp,
            color = secondary,
            letterSpacing = 3.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Jl. Fatmawati No.73a, Kedungmundu, Kec. Tembalang,Kota Semarang, Jawa Tengah 50273",
            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
            fontSize = 16.sp,
            color = secondary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
               onClick()
            },
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primary,
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(30.dp)
            ) {
                Row(modifier = Modifier.align(Alignment.Center)) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = white,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Text(
                        text = "Dapatkan Lokasi",
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
}
@Composable
private fun Item(
    onClick: () -> Unit,
    roomName: String? = null,
    rating: Double,
    harga: Int,
    jumlah_kamar: Int,
    foto:String,
    modifier: Modifier = Modifier
) {
    val fotoList: List<String> = remember {
        Json.decodeFromString(foto)
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)
        .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp))
        .background(white)
        .clickable { onClick() }) {

        val newDomain = "192.168.123.155"
        val newfoto = replaceDomain(fotoList[0],newDomain)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(newfoto)
                .crossfade(true)
                .build(),
            modifier = Modifier
                .padding(5.dp)
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp)),
//            placeholder = painterResource(id = R.drawable.img_placeholder),
            contentDescription = null,

            )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            Text(
                text = "$roomName",
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 20.sp,
                color = secondary,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(10.dp))
            // var rating by remember { mutableDoubleStateOf(3.5) }
            RatingBar(
                modifier = Modifier
                    .size(20.dp),
                rating = rating,
                starsColor = yellow
            )

            Spacer(modifier = Modifier.height(10.dp))
            val formatHarga = formatCurrency(harga)
            if(roomName=="Kamar"){
                Text(
                    text = "Rp$formatHarga /Kamar /Hari",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 16.sp,
                    color = primary,
                    modifier = Modifier
                )
            }else{
                Text(
                    text = "Rp$formatHarga /Hari",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 16.sp,
                    color = primary,
                    modifier = Modifier
                )
            }


        }

//        TextButton(
//            onClick = { },
//            modifier = Modifier
//                .padding(5.dp)
//                .widthIn(min = 120.dp, max = 120.dp) // Set fixed width for the button
//        ) {
//            Text(
//                text = "Selengkapnya",
//                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
//                fontSize = 16.sp,
//                color = gray,
//                maxLines = 1, // Ensure the text stays on one line
//                overflow = TextOverflow.Ellipsis, // Show ellipsis if text overflows
//                modifier = Modifier.align(Alignment.CenterVertically)
//            )
//        }


    }
}



@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Double = 0.0,
    stars: Int = 5,
    starsColor: Color = Color.Yellow
) {

    var isHalfStar = (rating % 1) != 0.0

    Row {
        for (index in 1..stars) {
            Icon(
                imageVector =
                if (index <= rating) {
                    Icons.Rounded.Star
                } else {
                    if (isHalfStar) {
                        isHalfStar = false
                        Icons.Rounded.StarHalf
                    } else {
                        Icons.Rounded.StarOutline
                    }
                },
                contentDescription = null,
                tint = starsColor,
                modifier = modifier
//                    .clickable { onRatingChanged(index.toDouble()) }
            )
        }
    }


}

@Composable
fun ChangeBaseUrlDialog(
    baseUrl: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newBaseUrl by remember { mutableStateOf(baseUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Change Base URL") },
        text = {
            TextField(
                value = newBaseUrl,
                onValueChange = { newBaseUrl = it },
                label = { Text("Base URL") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(newBaseUrl) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

