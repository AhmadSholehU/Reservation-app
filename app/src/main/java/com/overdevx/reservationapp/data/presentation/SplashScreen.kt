package com.overdevx.reservationapp.data.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit,modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) {
        delay(3000) // 3 detik
        onSplashComplete()
    }
    Box(modifier = Modifier.background(primary)) {
        Column(modifier = Modifier) {
            Column {
                Icon(
                    painter = painterResource(id = R.drawable.logo_v1),
                    contentDescription = null,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "DIKLATKU",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_bold))),
                    fontSize = 20.sp,
                    color = secondary,
                    letterSpacing = 3.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(modifier = modifier) {
                Text(
                    text = "ASRAMA BALAI DIKLAT",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_bold))),
                    fontSize = 20.sp,
                    color = secondary,
                    letterSpacing = 3.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Kota Semarang",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    fontSize = 16.sp,
                    color = secondary,
                    textAlign = TextAlign.Center,
                )
            }

        }
    }
}