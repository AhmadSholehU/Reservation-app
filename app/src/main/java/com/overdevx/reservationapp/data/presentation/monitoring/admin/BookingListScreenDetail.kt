package com.overdevx.reservationapp.data.presentation.monitoring.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.overdevx.reservationapp.data.model.BookingList
import com.overdevx.reservationapp.data.model.BookingListResponse
import com.overdevx.reservationapp.data.presentation.home.nonScaledSp
import com.overdevx.reservationapp.ui.theme.gray2
import com.overdevx.reservationapp.ui.theme.green
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.utils.AutoResizedText
import com.overdevx.reservationapp.utils.Resource
import com.overdevx.reservationapp.utils.convertDate

@Composable
fun BookingListScreenDetail(
    bookingViewModel: BookingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    bookingRoomId:Int,
    modifier: Modifier = Modifier) {

    LaunchedEffect(bookingRoomId) {
        bookingViewModel.getBookingListbyId(bookingRoomId)
    }
    val bookingListbyIdState by bookingViewModel.getBookingListbyIdState.collectAsStateWithLifecycle()
    Column(modifier = Modifier.padding(10.dp)) {
        TopBarSection(onNavigateBack = { onNavigateBack() })
        when(bookingListbyIdState){
            is Resource.Error -> {

            }
            is Resource.ErrorMessage -> {

            }
            Resource.Idle -> {

            }
            Resource.Loading -> {

            }
            is Resource.Success -> {
                val bookingList = (bookingListbyIdState as Resource.Success<BookingListResponse>).data?.data
                if (bookingList != null) {
                    BookingItem(booking = bookingList[0], onClick = {}, bookingViewModel = bookingViewModel)
                }
            }
        }
    }
}

@Composable
private fun TopBarSection(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxWidth()) {
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
        Spacer(modifier = modifier.width(10.dp))

        Text(
            text = "Daftar Ruang \n Terbooking",
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 20.sp,
            color = secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )


    }


}

@Composable
private fun BookingItem(
    booking: BookingList,
    onClick: () -> Unit,
    bookingViewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val bookingListbyIdState by bookingViewModel.getBookingListbyIdState.collectAsStateWithLifecycle()
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(white),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(8.dp)
                .background(primary)

        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            AutoResizedText(
                text = "ID BOOKING : ${booking.nomor_pesanan}",
                color = gray2,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 10.nonScaledSp,
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                val startDate = convertDate(booking.BookingRoom.Booking.start_date)
                val endDate = convertDate(booking.BookingRoom.Booking.end_date)
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    AutoResizedText(
                        text = "Check In",
                        color = gray2,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 12.nonScaledSp,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier
                    )

                    if (startDate != null) {
                        AutoResizedText(
                            text = startDate,
                            color = green,
                            style = TextStyle(
                                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                fontSize = 12.nonScaledSp,
                            ),
                            modifier = Modifier
                        )
                    }
                }
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(green)
                    ) {
                        Icon(
                            painterResource(R.drawable.pixelarticons_check),
                            null,
                            tint = white,
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
                Column(modifier = Modifier.align(Alignment.TopEnd)) {
                    AutoResizedText(
                        text = "Check Out",
                        color = gray2,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 12.nonScaledSp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.align(Alignment.End)
                    )
                    if (endDate != null) {
                        AutoResizedText(
                            text = endDate,
                            color = primary,
                            style = TextStyle(
                                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                fontSize = 12.nonScaledSp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
            HorizontalDivider(thickness = 1.dp, color = primary)
            Spacer(modifier = Modifier.height(10.dp))

            Spacer(modifier = Modifier.height(10.dp))
            AutoResizedText(
                text = "Ruang Terbooking ",
                color = secondary,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 14.nonScaledSp,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(10.dp))
            when (bookingListbyIdState) {
                is Resource.Error -> {

                }
                is Resource.ErrorMessage -> {

                }
                Resource.Idle -> {

                }
                Resource.Loading -> {

                }
                is Resource.Success -> {
                    val roomData =
                        (bookingListbyIdState as Resource.Success<BookingListResponse>).data?.data
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(roomData.orEmpty()) { room ->
                            RoomItem(room)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    onClick()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                ),
                modifier = Modifier.align(Alignment.Start)
            ) {
                Box(
                    modifier = Modifier
                        .width(85.dp)
                        .height(25.dp)
                ) {
                    Row(modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = white,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(20.dp)
                        )
                        AutoResizedText(
                            text = "Reschedule",
                            color = white,
                            style = TextStyle(
                                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                fontSize = 10.nonScaledSp,
                                textAlign = TextAlign.Center,
                            ),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                }

            }

        }
        Spacer(modifier = Modifier.weight(1f))

    }
}