package com.overdevx.reservationapp.data.presentation.monitoring.admin

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.presentation.BuildingViewModel
import com.overdevx.reservationapp.data.presentation.monitoring.auth.AuthViewModel
import com.overdevx.reservationapp.ui.theme.blue
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.white2
import com.overdevx.reservationapp.ui.theme.yellow
import com.overdevx.reservationapp.ui.theme.yellow2

@Composable
fun HomeControlScreen(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit,
    onMenu1Click: () -> Unit,
    onMenu2Click: () -> Unit,
    onMenu3Click: () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()){
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            TopBarSection()
            Spacer(modifier = Modifier.height(20.dp))
            HeaderSection(modifier = Modifier.padding(start = 10.dp, end = 10.dp), onLogoutClick = { onLogoutClick() })
            Spacer(modifier = Modifier.height(10.dp))
            ContentSection(onMenu1Click, onMenu2Click, onMenu3Click)

        }
        Image(
            painter = painterResource(id = R.drawable.img_line),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }

}

@Composable
private fun TopBarSection(
    modifier: Modifier = Modifier,
    ) {

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
                fontFamily = FontFamily(listOf(Font(R.font.inter_bold))),
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

    }
}

@Composable
private fun HeaderSection(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
    ) {
        Column(modifier = Modifier) {
            Text(
                text = "Hello",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 16.sp,
                color = secondary,
                modifier = Modifier
            )
            Text(
                text = "Admin",
                fontFamily = FontFamily(listOf(Font(R.font.inter_bold))),
                fontSize = 30.sp,
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
private fun ContentSection(
    menu1:()-> Unit,
    menu2:()-> Unit,
    menu3:()-> Unit,
    modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .height(400.dp)
            .padding(start = 10.dp, end = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DashboardItem(
                title = "Control",
                title2 = "Ruang",
                backgroundImage = R.drawable.ic_menu_control,
                onClick = {menu1()},
                modifier = Modifier.weight(1.1f).height(400.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                DashboardItem2(
                    title = "Daftar",
                    title2 = "Ruang Terbooking",
                    backgroundImage = R.drawable.ic_menu_booking,
                    onClick = {menu2()},
                    modifier = Modifier.height(250.dp)
                )
                DashboardItem3(
                    title = "Riwayat",
                    title2 = "Transaksi",
                    backgroundImage = R.drawable.ic_menu_riwayat,
                    onClick = {menu3()},
                    modifier = Modifier.height(150.dp)
                )
            }

        }


    }
}

@Composable
fun DashboardItem(
    title: String,
    title2: String,
    backgroundImage: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(primary)

            .clickable { onClick() }

    ) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.align(Alignment.BottomCenter)

        )
        Column(modifier = Modifier.align(Alignment.TopStart)
            .padding(16.dp)) {
            Text(
                  text = title,
                  fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                  fontSize = 16.sp,
                  color = white,
                  modifier = Modifier
              )
            Text(
                  text = title2,
                  fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                  fontSize = 30.sp,
                  color = white,
                  modifier = Modifier
              )

        }


    }
}

@Composable
fun DashboardItem2(
    title: String,
    title2: String,
    backgroundImage: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(blue)
            .clickable { onClick() }

    ) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(-15.dp,40.dp)

        )
        Column(modifier = Modifier.align(Alignment.TopStart)
            .padding(16.dp)) {
            Text(
                text = title,
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 16.sp,
                color = white,
                modifier = Modifier
            )
            Text(
                text = title2,
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 30.sp,
                color = white,
                modifier = Modifier
            )

        }


    }
}

@Composable
fun DashboardItem3(
    title: String,
    title2: String,
    backgroundImage: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(yellow2)
            .clickable {
                onClick()
            }


    ) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(5.dp)
                .size(70.dp)
                .align(Alignment.CenterEnd)

        )
        Column(modifier = Modifier.align(Alignment.TopStart)
            .padding(16.dp)) {
            Text(
                text = title,
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 16.sp,
                color = white,
                modifier = Modifier
            )
            Text(
                text = title2,
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 30.sp,
                color = white,
                modifier = Modifier
            )

        }


    }
}