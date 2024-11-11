package com.overdevx.reservationapp.data.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import com.overdevx.reservationapp.BuildConfig
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.utils.replaceDomain
import kotlinx.serialization.json.Json
import kotlin.math.absoluteValue

@Composable
fun DetailHomeUserScreen(
    roomName: String,
    harga: Int,
    rating: String,
    deskripsi: String,
    jumlahKamar: Int,
    foto:String,
    onClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(start = 16.dp, end = 16.dp)) {
        TopBarSection(onNavigateBack = { onNavigateBack() })
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                MainSection(roomName, harga, rating, deskripsi, jumlahKamar,foto, onClick = {onClick()})
            }
        }

    }
}

@Composable
private fun TopBarSection(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = { onNavigateBack() },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(40.dp),
            colors = IconButtonDefaults.iconButtonColors(Color.Transparent)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint = secondary
            )
        }
        Spacer(modifier = modifier.width(10.dp))

        Text(
            text = "Detail",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 20.sp,
            color = secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterVertically)
        )


    }


}

@Composable
private fun MainSection(
    roomName: String,
    harga: Int,
    rating: String,
    deskripsi: String,
    jumlahKamar: Int,
    foto:String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Parsing JSON string menjadi List<String> menggunakan kotlinx.serialization
    val fotoList: List<String> = remember {
        Json.decodeFromString(foto)
    }
    val state = rememberPagerState { fotoList.size}

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        HorizontalPager(
            state = state,
            contentPadding = PaddingValues(end = 64.dp),
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Box(
                Modifier
                    .size(300.dp)
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = (
                                (state.currentPage - page) + state
                                    .currentPageOffsetFraction
                                ).absoluteValue

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                val newDomain = "192.168.39.85"
                val newfoto = replaceDomain(fotoList[page],newDomain)
                AsyncImage(
                    model = newfoto,
                    contentDescription = null,
                    Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            }

        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(state.pageCount) { iteration ->
                val color = if (state.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "$roomName",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 20.sp,
                    color = secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )

                Row {
                    RatingBar(
                        modifier = Modifier
                            .size(20.dp),
                        rating = rating.toDouble(),
                        starsColor = Color.Yellow
                    )
                    Text(
                        text = rating,
                        fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                        fontSize = 16.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )
                }
                Text(
                    text = "$jumlahKamar Kamar Tersedia",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    fontSize = 16.sp,
                    color = gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(modifier = Modifier) {
                Text(
                    text = "$harga",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 20.sp,
                    color = secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
                Text(
                    text = "/Hari/Kamar",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    fontSize = 16.sp,
                    color = gray,
                )
            }
        }

        Text(
            text = "$deskripsi",
            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
            fontSize = 20.sp,
            color = secondary,
            textAlign = TextAlign.Justify,
            modifier = Modifier
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 10.dp)
        ) {
            Button(
                onClick = {onClick()},
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                )
            ) {
                Text(
                    text = "PESAN SEKARANG",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 18.sp,
                    letterSpacing = 5.sp,
                    color = white,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { },
                modifier = modifier
                    .border(
                        width = 1.dp, // ketebalan border
                        color = primary, // warna border
                        shape = RoundedCornerShape(16.dp) // corner radius
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(Color.Transparent), // Warna latar tombol

            ) {
                Text(
                    text = "CEK KETERSEDIAAN",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 16.sp,
                    letterSpacing = 5.sp,
                    color = primary,
                    textAlign = TextAlign.Center,
                )
            }
        }


    }
}

@Composable
private fun FasilitasSection (modifier: Modifier = Modifier) {
    Column(modifier = Modifier) {
        Text(
            text = "Fasilitas Kamar :",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 16.sp,
            letterSpacing = 5.sp,
            color = secondary,
            textAlign = TextAlign.Center,
        )


    }
}

