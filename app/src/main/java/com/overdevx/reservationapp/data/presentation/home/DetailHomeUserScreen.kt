package com.overdevx.reservationapp.data.presentation.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.model.Room
import com.overdevx.reservationapp.data.presentation.RoomsViewModel
import com.overdevx.reservationapp.data.presentation.monitoring.admin.EmptyItem
import com.overdevx.reservationapp.data.presentation.monitoring.admin.ErrorItem
import com.overdevx.reservationapp.data.presentation.monitoring.admin.LoadingShimmerEffect
import com.overdevx.reservationapp.data.presentation.monitoring.admin.LoadingShimmerEffectPesanDialog
import com.overdevx.reservationapp.ui.theme.gray
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.utils.AutoResizedText
import com.overdevx.reservationapp.utils.Resource
import com.overdevx.reservationapp.utils.formatCurrency
import com.overdevx.reservationapp.utils.replaceDomain
import kotlinx.serialization.json.Json
import kotlin.math.absoluteValue

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailHomeUserScreen(
    id: Int,
    roomName: String,
    harga: Int,
    rating: String,
    deskripsi: String,
    jumlahKamar: Int,
    foto: String,
    onClick: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RoomsViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedRoomNumbers by remember { mutableStateOf(listOf<String>()) }
    var buildingId by remember { mutableStateOf(1) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.fetchRooms(buildingId)
    }
    Column(modifier = modifier.padding(start = 16.dp, end = 16.dp)) {
        TopBarSection(
            onNavigateBack = { onNavigateBack() },
            modifier = Modifier.padding(start = 10.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(modifier = Modifier) {
            item {
                MainSection(
                    id,
                    roomName,
                    harga,
                    rating,
                    deskripsi,
                    jumlahKamar,
                    foto,
                    animatedVisibilityScope,
                    sharedTransitionScope
                )
            }
            item {

                Box(
                    modifier = Modifier
                        .fillMaxSize(),// Tambahkan padding jika diperlukan
                    contentAlignment = Alignment.BottomCenter // Posisikan tombol di bagian bawah
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)) {
                        Spacer(modifier = Modifier.weight(1f)) // Berikan ruang fleksibel
                        Text(
                            text = "$jumlahKamar Kamar Tersedia",
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 12.nonScaledSp,
                            color = primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.End)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (roomName == "Kamar") {
                                    showDialog = true
                                } else {
                                    val formatedHarga = formatCurrency(harga)
                                    val message =
                                        "Saya ingin memesan $roomName dengan tarif Rp. $formatedHarga per hari. Mohon konfirmasikan apakah gedung tersebut masih tersedia untuk tanggal yang saya inginkan."
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(
                                            String.format(
                                                "https://api.whatsapp.com/send?phone=%s&text=%s",
                                                "+62 8987472054",
                                                message
                                            )
                                        )
                                    )
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primary,
                            )
                        ) {
                            Text(
                                text = "PESAN SEKARANG",
                                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                fontSize = 14.nonScaledSp,
                                letterSpacing = 5.sp,
                                color = white,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

            }
        }
        if (showDialog) {
            //viewModel.fetchRooms(1)
            PesanDialog(
                selectedRoomNumbers = selectedRoomNumbers,
                onDismiss = {
                    showDialog = false
                },
                onDialogAction = { roomNumbers ->
                    val buildingName = if (buildingId == 1) "Asrama A" else "Asrama B"
                    val formattedHarga = formatCurrency(harga)

                    // Format daftar kamar dengan bullet point
                    val roomList = roomNumbers.joinToString(separator = "\n") { "- $it" }
                    val message = """
    Saya ingin memesan kamar nomor:
    $roomList
    di Gedung $buildingName dengan tarif Rp. $formattedHarga per malam. Apakah kamar-kamar tersebut masih tersedia untuk tanggal yang saya inginkan?
    """.trimIndent()

                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            String.format(
                                "https://api.whatsapp.com/send?phone=%s&text=%s",
                                "+62 8987472054", // Ganti dengan nomor WhatsApp
                                Uri.encode(message)
                            )
                        )
                    )
                    context.startActivity(intent)
                },
                onBuildingSelected = { buildingName ->
                    buildingId = if (buildingName == "Gedung Asrama A") 1 else 2
                    viewModel.fetchRooms(buildingId)
                },
                onRoomSelected = { roomNumber, roomId, roomStatus ->
                    selectedRoomNumbers = if (selectedRoomNumbers.contains(roomNumber)) {
                        selectedRoomNumbers - roomNumber // Hapus jika sudah dipilih
                    } else {
                        selectedRoomNumbers + roomNumber // Tambahkan jika belum dipilih
                    }
                },
                showDialog = { false },
                modifier = Modifier
            )

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
        AutoResizedText(
            text = "Detail",
            color = secondary,
            style = TextStyle(
                fontFamily = FontFamily(listOf(Font(R.font.inter_bold))),
                fontSize = 16.nonScaledSp,
            ),
            modifier = Modifier.align(Alignment.CenterVertically)
        )


    }


}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun MainSection(
    id: Int,
    roomName: String,
    harga: Int,
    rating: String,
    deskripsi: String,
    jumlahKamar: Int,
    foto: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier
) {
    var selectedRoomNumber by remember { mutableStateOf<String?>(null) }
    // Parsing JSON string menjadi List<String> menggunakan kotlinx.serialization
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    val imgUrl = sharedPreferences.getString("base_url", "192.168.123.155")
    val fotoList: List<String> = remember {
        Json.decodeFromString(foto)
    }
    var showDialog by remember { mutableStateOf(false) }
    val state = rememberPagerState { fotoList.size }
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxWidth()

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
                                start = 0.3f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                ) {
                    val ipAddress = imgUrl
                        ?.split("://")?.get(1) // Menghapus "http://"
                        ?.split(":")?.get(0)
                    val newDomain = "192.168.123.155"
                    val newfoto = ipAddress?.let { replaceDomain(fotoList[page], it) }
                    AsyncImage(
                        model = newfoto,
                        contentDescription = null,
                        Modifier
//                            .sharedElement(
//                                state = rememberSharedContentState(key = "image/$newfoto"),
//                                animatedVisibilityScope = animatedVisibilityScope,
//                                boundsTransform = { _, _ ->
//                                    tween(durationMillis = 1000)
//                                }
//                            )
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
                    val color =
                        if (state.currentPage == iteration) Color.DarkGray else Color.LightGray
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
                    AutoResizedText(
                        text = "$roomName",
                        color = secondary,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 20.nonScaledSp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                    )

                    Row {
                        RatingBar(
                            modifier = Modifier
                                .size(20.dp),
                            rating = rating.toDouble(),
                            starsColor = Color.Yellow
                        )
                        AutoResizedText(
                            text = rating,
                            color = primary,
                            style = TextStyle(
                                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                fontSize = 12.nonScaledSp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                        )
                    }

                }
                Spacer(modifier = Modifier.weight(1f))
                Column(modifier = Modifier) {
                    val formatHarga = formatCurrency(harga)
                    AutoResizedText(
                        text = "Rp$formatHarga",
                        color = secondary,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 16.nonScaledSp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                    )
                    AutoResizedText(
                        text = "/Hari/Kamar",
                        color = gray,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 12.nonScaledSp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            FasilitasSection(id)
            Spacer(modifier = Modifier.height(16.dp))
            AutoResizedText(
                text = "$deskripsi",
                color = gray,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                    fontSize = 14.nonScaledSp,
                    textAlign = TextAlign.Justify
                ),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(16.dp))

        }
    }


}

@Composable
private fun FasilitasSection(id: Int, modifier: Modifier = Modifier) {
    // Pemetaan ID ke daftar fasilitas
    val fasilitasMap = mapOf(
        1 to listOf(
            "AC" to R.drawable.ic_ac,
            "Wifi" to R.drawable.ic_wifi,
            "Parkir" to R.drawable.ic_parkir,
            "Keamanan 24 Jam" to R.drawable.ic_call
        ),
        2 to listOf(
            "AC" to R.drawable.ic_ac,
            "Wifi" to R.drawable.ic_wifi,
            "Parkir" to R.drawable.ic_parkir,
            "Keamanan 24 Jam" to R.drawable.ic_call
        ),
        3 to listOf(
            "AC" to R.drawable.ic_ac,
            "Wifi" to R.drawable.ic_wifi,
            "Parkir" to R.drawable.ic_parkir,
            "Fasilitas Rapat" to R.drawable.ic_rapat
        ),
        4 to listOf(
            "AC" to R.drawable.ic_ac,
            "Wifi" to R.drawable.ic_wifi,
            "Parkir" to R.drawable.ic_parkir,
            "Fasilitas Rapat" to R.drawable.ic_rapat
        ),
        5 to listOf(
            "AC" to R.drawable.ic_ac,
            "Wifi" to R.drawable.ic_wifi,
            "Parkir" to R.drawable.ic_parkir,
            "Fasilitas Rapat" to R.drawable.ic_rapat
        ),
        6 to listOf(
            "AC" to R.drawable.ic_ac,
            "Wifi" to R.drawable.ic_wifi,
            "Parkir" to R.drawable.ic_parkir,
            "Fasilitas Rapat" to R.drawable.ic_rapat
        ),
        7 to listOf("CCTV 24 Jam" to R.drawable.ic_cctv)
    )

    // Ambil daftar fasilitas berdasarkan ID
    val fasilitasList = fasilitasMap[id] ?: emptyList()

    Column(modifier = Modifier) {
        AutoResizedText(
            text = "Fasilitas Kamar :",
            color = gray,
            style = TextStyle(
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 12.nonScaledSp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Jarak antar item
        ) {
            items(fasilitasList) { fasilitas ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = fasilitas.second), // Ikon fasilitas
                        contentDescription = null,
                        tint = gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AutoResizedText(
                        text = fasilitas.first, // Nama fasilitas
                        color = gray,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 12.nonScaledSp,
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier
                    )
                }
            }
        }


    }
}

@SuppressLint("RememberReturnType")
@Composable
private fun PesanDialog(
    selectedRoomNumbers: List<String>,
    onDismiss: () -> Unit,
    onDialogAction: (List<String>) -> Unit,
    viewModel: RoomsViewModel = hiltViewModel(),
    onBuildingSelected: (String) -> Unit,
    onRoomSelected: (String, Boolean, Any) -> Unit,
    showDialog: (Boolean) -> Unit,
    modifier: Modifier
) {
    // State untuk menyimpan status dan waktu penyewaan yang dipilih
    var selectedStatus by remember { mutableStateOf("Gedung Asrama A") }
    val roomState by viewModel.roomState.collectAsStateWithLifecycle()

    // Daftar kamar yang dipilih
    val selectedRooms = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Column(Modifier.fillMaxWidth()) {
                AutoResizedText(
                    text = "DAFTAR RUANGAN",
                    color = secondary,
                    style = TextStyle(
                        fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                        fontSize = 14.nonScaledSp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf("Gedung Asrama A", "Gedung Asrama B")) { status ->
                        AsramaButton(
                            text = status,
                            isSelected = selectedStatus == status,
                            onClick = {
                                selectedRooms.clear()
                                selectedStatus = status
                                onBuildingSelected(status)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                when (roomState) {
                    is Resource.Loading -> {
                        LoadingShimmerEffectPesanDialog()
                    }

                    is Resource.Success -> {
                        val rooms = (roomState as Resource.Success<List<Room>>).data
                        if (rooms != null) {
                            if (rooms.isEmpty()) {
                                EmptyItem()
                            } else {
                                val groupedRooms = rooms.groupBy { room ->
                                    if (room.building_name == "Gedung A") {
                                        when {
                                            room.room_number.startsWith("A1") -> "Lantai 1"
                                            room.room_number.startsWith("A2") -> "Lantai 2"
                                            room.room_number.startsWith("A3") -> "Lantai 3"
                                            else -> "Lantai Lainnya"
                                        }
                                    } else {
                                        when {
                                            room.room_number.startsWith("B1") -> "Lantai 1"
                                            room.room_number.startsWith("B2") -> "Lantai 2"
                                            room.room_number.startsWith("B3") -> "Lantai 3"
                                            else -> "Lantai Lainnya"
                                        }
                                    }
                                }
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(16.dp)
                                ) {
                                    groupedRooms.forEach { (floor, rooms) ->
                                        item {
                                            // Header untuk setiap lantai
                                            AutoResizedText(
                                                text = floor,
                                                color = secondary,
                                                style = TextStyle(
                                                    fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                                                    fontSize = 12.nonScaledSp,
                                                ),
                                                modifier = Modifier.padding(vertical = 5.dp)
                                            )
                                        }

                                        // Menampilkan ruangan dalam grid untuk lantai ini
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .heightIn(max = 500.dp)
                                            ) {
                                                LazyVerticalGrid(
                                                    columns = GridCells.Fixed(4),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    items(rooms, key = { it.room_id }) { room ->
                                                        RoomItem(
                                                            modifier = Modifier.padding(5.dp),
                                                            room = room,
                                                            isSelected = selectedRooms.contains(room.room_number),
                                                            onClick = {
                                                                if (selectedRooms.contains(room.room_number)) {
                                                                    selectedRooms.remove(room.room_number)
                                                                    onRoomSelected(
                                                                        room.room_number,
                                                                        false,
                                                                        0
                                                                    )
                                                                } else {
                                                                    selectedRooms.add(room.room_number)
                                                                    onRoomSelected(
                                                                        room.room_number,
                                                                        true,
                                                                        0
                                                                    )
                                                                }
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is Resource.ErrorMessage -> {
                        val errorMessage = (roomState as Resource.ErrorMessage).message
                        Text(text = "Error: $errorMessage")
                        Log.e("PesanDialog", "Error: $errorMessage")
                    }

                    is Resource.Error -> {
                        val exceptionMessage =
                            (roomState as Resource.Error).exception.message
                                ?: "Unknown error occurred"
                        ErrorItem(errorMsg = exceptionMessage)
                    }

                    else -> {}
                }
            }
        },
        confirmButton = {
            if (selectedRooms.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AutoResizedText(
                        text = "Kamar terpilih: ${selectedRooms.joinToString(", ")}",
                        color = secondary,
                        style = TextStyle(
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 14.nonScaledSp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onDialogAction(selectedRooms)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primary),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                    ) {
                        AutoResizedText(
                            text = "KONFIRMASI PESANAN",
                            color = white,
                            style = TextStyle(
                                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                                fontSize = 12.nonScaledSp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                        )
                    }
                }
            }

        },
        dismissButton = {

        },
        containerColor = white,
        shape = RoundedCornerShape(10.dp),

        )
}

@Composable
private fun RoomItem(
    modifier: Modifier = Modifier,
    room: Room,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when (room.status_name) {
        "available" -> secondary
        "booked" -> Color.Transparent
        else -> secondary.copy(0.5f) // Untuk "not available"
    }

    // Warna latar belakang tergantung apakah item sedang dipilih atau tidak
    val backgroundColor = when {
        isSelected -> primary // Warna merah jika dipilih
        room.status_name == "booked" -> secondary
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> Color.White // Teks putih jika dipilih
        room.status_name == "booked" -> Color.White
        else -> borderColor
    }

    // Hanya dapat diklik jika statusnya "available"
    val isClickable = room.status_name == "available"

    Row(
        modifier = modifier
            .size(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (!isSelected) {
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(8.dp))
                } else Modifier // Tidak ada border jika dipilih
            )
            .background(backgroundColor)
            .then(
                if (isClickable) Modifier.clickable { onClick() }
                else Modifier // Tidak ada aksi jika tidak clickable
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
                .padding(5.dp),
        ) {
            AutoResizedText(
                text = "${room.room_number}",
                color = textColor,
                style = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 12.nonScaledSp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun AsramaButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = if (isSelected) {
            ButtonDefaults.buttonColors(containerColor = primary, contentColor = Color.White)
        } else {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = primary
            )
        },
        modifier = if (isSelected) Modifier.size(
            height = 40.dp,
            width = Dp.Unspecified
        ) else Modifier
            .border(1.dp, primary, RoundedCornerShape(10.dp))
            .size(height = 40.dp, width = Dp.Unspecified)

    ) {
        AutoResizedText(
            text = text,
            color = if (isSelected) white else secondary,
            style = TextStyle(
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 14.nonScaledSp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
        )
    }
}

@Composable
private fun ButtonSection(
    modifier: Modifier = Modifier,
    selectedRoom: String?,
    showDialog: (Boolean) -> Unit,
    onShowDialog: () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (selectedRoom != null) {
            Text(
                text = "Kamar $selectedRoom terpilih",
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 18.sp,
                color = secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onShowDialog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                )
            ) {
                Text(
                    text = "KONFIRMASI PESANAN",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 18.sp,
                    letterSpacing = 5.sp,
                    color = white,
                )
            }
        }
    }
}



