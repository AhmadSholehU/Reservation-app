package com.overdevx.reservationapp.data.presentation.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarHalf
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white

@Composable
fun HomeUserScreen(
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        HeaderSection(modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(16.dp))
        MainSection(onClick = onClick)
    }

}

@Composable
private fun HeaderSection(modifier: Modifier = Modifier) {
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
private fun MainSection(
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "Daftar Ruang",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 20.sp,
                    color = secondary,
                    modifier = Modifier
                )
                Text(
                    text = "6 Ruang Disewakan",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    fontSize = 16.sp,
                    color = gray,

                    modifier = Modifier
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = { },
            ) {
                Text(
                    text = "Cek Ketersediaan",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    fontSize = 16.sp,
                    color = gray,

                    modifier = Modifier
                )
            }
        }
        LazyColumn {
            items(10){
                Item(onClick = onClick)
            }
        }
    }
}

@Composable
private fun Item (
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)
        .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp))
        .background(white)
        .clickable { onClick() }){
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            Modifier
                .padding(5.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            Text(
                text = "Kamar",
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 20.sp,
                color = secondary,
                modifier = Modifier)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "10 Kamar Tersedia",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 16.sp,
                color = secondary,
                modifier = Modifier
            )
            var rating by remember { mutableDoubleStateOf(3.5) }

            RatingBar(
                modifier = Modifier
                    .size(20.dp),
                rating = rating,
                starsColor = Color.Yellow
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Rp200.000 /Kamar /Hari",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 16.sp,
                color = secondary,
                modifier = Modifier
            )

        }
        TextButton(
            onClick = { },
            modifier = Modifier.padding(5.dp)

        ) {
            Text(
                text = "Selengkapnya",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 16.sp,
                color = gray,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
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