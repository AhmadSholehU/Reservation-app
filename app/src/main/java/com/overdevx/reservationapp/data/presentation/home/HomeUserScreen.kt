package com.overdevx.reservationapp.data.presentation.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
import com.overdevx.reservationapp.ui.theme.yellow2
import com.overdevx.reservationapp.utils.AutoResizedText
import com.overdevx.reservationapp.utils.ChangeBaseUrlScreen
import com.overdevx.reservationapp.utils.Resource
import com.overdevx.reservationapp.utils.formatCurrency
import com.overdevx.reservationapp.utils.replaceDomain
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeUserScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onClick: (Int, String, String, Int, Int, String, String) -> Unit,
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
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
    BackHandler {
        // Menutup aplikasi jika berada di layar login dan menekan tombol back
        (navController.context as? Activity)?.finish()
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
                showDialog = true
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
                                    foto = it.foto,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    sharedTransitionScope =   sharedTransitionScope
                                )
                            }

                        }

                    }

                }

                else -> {}
            }
        }

        if (showDialog) {
            ChangeBaseUrlDialog(
                baseUrl = homeViewModel.getBaseUrl(),
                onDismiss = { showDialog = false },
                onSave = { newUrl ->
                    homeViewModel.saveBaseUrl(newUrl)
                    showDialog = false
                    //Toast.makeText(LocalContext.current, "Base URL updated!", Toast.LENGTH_SHORT).show()
                })
        }
    }
}

val Int.nonScaledSp
    @Composable
    get() = (this / LocalDensity.current.fontScale).sp

@Composable
private fun HeaderSection(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Column(modifier = modifier.fillMaxWidth()) {
        AutoResizedText(
            text = "ASRAMA BALAI DIKLAT",
            color = secondary,
            style = TextStyle(
                fontFamily = FontFamily(listOf(Font(R.font.inter_bold))),
                fontSize = 16.nonScaledSp,
                letterSpacing = 2.sp
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        AutoResizedText(
            text = "Jl. Fatmawati No.73a, Kedungmundu, Kec. Tembalang,\nKota Semarang, Jawa Tengah 50273",
            color = secondary,
            style = TextStyle(
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 12.nonScaledSp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .clickable { onClick() }
        )
        Spacer(modifier = Modifier.height(10.dp))
        OpenGoogleMapsButton(modifier = Modifier.align(Alignment.CenterHorizontally),
            address ="Balai Diklat Kota Semarang")

        Spacer(modifier = Modifier.height(16.dp))
        AutoResizedText(
            text = "Daftar Ruang",
            color = secondary,
            style = TextStyle(
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 14.nonScaledSp,
            ),
            modifier = Modifier
        )
        AutoResizedText(
            text = "6 Ruang Disewakan",
            color = gray,
            style = TextStyle(
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 12.nonScaledSp,
            ),
            modifier = Modifier
        )

    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Item(
    onClick: () -> Unit,
    roomName: String? = null,
    rating: Double,
    harga: Int,
    jumlah_kamar: Int,
    foto: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    val imgUrl = sharedPreferences.getString("base_url","http://192.168.123.155:3000")
    val fotoList: List<String> = remember {
        Json.decodeFromString(foto)
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)
        .shadow(elevation = 5.dp, shape = RoundedCornerShape(16.dp))
        .background(white)
        .clickable { onClick() }) {

        val ipAddress = imgUrl
            ?.split("://")?.get(1) // Menghapus "http://"
            ?.split(":")?.get(0)
        val newDomain = "192.168.123.155"
        val newfoto = ipAddress?.let { replaceDomain(fotoList[0], it) }

        with(sharedTransitionScope) {
            Box(
                modifier = Modifier

            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(newfoto)
                        .crossfade(true)
                        .build(),
                    filterQuality = FilterQuality.Low,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(100.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                AutoResizedText(
                    text = "$roomName",
                    color = secondary,
                    style = TextStyle(
                        fontFamily =FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 14.nonScaledSp,
                        color = secondary),
                )
                Spacer(modifier = Modifier.height(10.dp))
                // var rating by remember { mutableDoubleStateOf(3.5) }
                RatingBar(
                    modifier = Modifier
                        .size(20.dp),
                    rating = rating,
                    starsColor = yellow2
                )

                Spacer(modifier = Modifier.height(10.dp))
                val formatHarga = formatCurrency(harga)
                if (roomName == "Kamar") {
                    AutoResizedText(
                        text = "Rp$formatHarga /Kamar /Hari",
                        color = primary,
                        style = TextStyle(fontFamily =FontFamily(listOf(Font(R.font.inter_semibold))), fontSize = 14.nonScaledSp ),
                    )
                } else {
                    AutoResizedText(
                        text = "Rp$formatHarga /Hari",
                        color = primary,
                        style = TextStyle(fontFamily =FontFamily(listOf(Font(R.font.inter_semibold))), fontSize = 14.nonScaledSp ),
                    )
                }


            }


        }
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

@Composable
fun OpenGoogleMapsButton(modifier: Modifier,address: String) {
    val context = LocalContext.current

    Button(
        onClick = {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            }
        },
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = primary,
        ),
        modifier = modifier
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
                AutoResizedText(
                    text = "Dapatkan Lokasi",
                    color = white,
                    style = TextStyle(
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 12.nonScaledSp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

        }
    }
}

