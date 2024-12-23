package com.overdevx.reservationapp.data.presentation.monitoring.history

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.History
import com.overdevx.reservationapp.data.presentation.home.nonScaledSp
import com.overdevx.reservationapp.data.presentation.monitoring.admin.ErrorItem
import com.overdevx.reservationapp.data.presentation.monitoring.admin.Loading
import com.overdevx.reservationapp.data.presentation.monitoring.admin.LoadingShimmerEffect
import com.overdevx.reservationapp.data.presentation.monitoring.admin.LoadingShimmerEffect2
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.gray3
import com.overdevx.reservationapp.ui.theme.gray4
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.white2
import com.overdevx.reservationapp.utils.AutoResizedText
import com.overdevx.reservationapp.utils.Resource
import com.overdevx.reservationapp.utils.formatDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val historyState by historyViewModel.historyState.collectAsStateWithLifecycle()
    val historyList = historyViewModel.historyList.collectAsLazyPagingItems()

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            delay(2000)
            historyViewModel.fetchHistory()
            isRefreshing = false
        }
    }
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
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
            Text(
                text = "Riwayat",
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 22.sp,
                color = secondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        Spacer(modifier = Modifier.height(10.dp))
        // Transform LazyPagingItems to grouped data
        val groupedData = historyList.itemSnapshotList.items
            .groupBy { formatDate(it.changed_at) } // Group by formatted date
            .mapValues { entry ->
                entry.value.sortedBy { it.id } // Sort by ID within each group
            }
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(gray4)
                    .padding(10.dp)

            ) {
                if (historyList.itemCount == 0 && historyList.loadState.refresh is LoadState.NotLoading) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Tidak ada data yang tersedia",
                                style = TextStyle(
                                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                    fontSize = 16.sp,
                                    color = secondary
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(historyList) { booking ->
                        if (booking != null) {
                            BoxWithConstraints {
                                if (maxWidth < 320.dp) {
                                    BookingItemSmall(booking)
                                } else {
                                    BookingItem(booking)
                                }
                            }
                        }
                    }
                }




                historyList.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                LoadingShimmerEffect2()
                            }
                        }

                        loadState.refresh is LoadState.Error -> {
                            val e = loadState.refresh as LoadState.Error
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AutoResizedText(
                                        text = "Gagal memuat data awal: ${e.error.localizedMessage}",
                                        color = primary,
                                        style = TextStyle(
                                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                            fontSize = 12.nonScaledSp,
                                        ),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Button(
                                        onClick = {
                                            retry()
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = primary,
                                        ),
                                        modifier = Modifier
                                            .width(200.dp)
                                            .align(Alignment.CenterHorizontally)

                                    ) {
                                        Text(
                                            text = "Muat Ulang",
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

                        // Tambahkan kondisi untuk footer jika terjadi kesalahan pada append (footer)
                        loadState.append is LoadState.Error -> {
                            val e = loadState.append as LoadState.Error
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Gagal memuat data tambahan: ${e.error.localizedMessage}",
                                        color = primary,
                                        style = TextStyle(
                                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                            fontSize = 14.sp
                                        ),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Button(
                                        onClick = {
                                            retry() // Fungsi untuk mencoba memuat ulang
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = primary,
                                        ),
                                        modifier = Modifier
                                            .width(200.dp)
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        Text(
                                            text = "Coba Lagi",
                                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                            fontSize = 16.sp,
                                            color = white,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                        // Footer untuk menampilkan loading jika data tambahan sedang dimuat
                        loadState.append is LoadState.Loading -> {
                            item {
                                CircularProgressIndicator(
                                    color = primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp)
                                        .wrapContentSize(Alignment.Center)
                                )
                            }
                        }

                    }
                }


            }

            // Tombol "Kembali ke Atas"
            val isAtTop = lazyListState.firstVisibleItemIndex == 0
            if (!isAtTop) {
                FloatingActionButton(
                    containerColor = secondary,
                    onClick = {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(0)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        tint = white,
                        contentDescription = "Kembali ke atas"
                    )
                }
            }
        }
    }


}

@Composable
fun BookingItem(history: History) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
            ) {
                AutoResizedText(
                    text = "${history.Room.Building.name} - ${history.Room.room_number}",
                    color = secondary,
                    style = TextStyle(
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 12.nonScaledSp,
                    ),
                    modifier = Modifier
                )
                Spacer(Modifier.height(5.dp))
                AutoResizedText(
                    text = "${formatDate(history.changed_at)}",
                    color = gray,
                    style = TextStyle(
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 10.nonScaledSp,
                    ),
                    modifier = Modifier
                )
                AutoResizedText(
                    text = "${history.days} Hari",
                    color = gray,
                    style = TextStyle(
                        fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                        fontSize = 8.nonScaledSp,
                    ),
                    modifier = Modifier
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            AutoResizedText(
                text = "ID BOOKING : ${history.nomor_pesanan}",
                color = gray,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 8.nonScaledSp,
                ),
                modifier = Modifier
            )
        }
    }
}

@Composable
fun BookingItemSmall(history: History) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
            ) {
                Text(
                    text = "ID BOOKING : ${history.nomor_pesanan}",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    color = gray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "${history.Room.Building.name} - ${history.Room.room_number}",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    color = secondary,
                    fontSize = 16.sp
                )
                Text(
                    text = "${history.days} Hari",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    color = gray,
                    fontSize = 12.sp
                )
            }


        }
    }
}


