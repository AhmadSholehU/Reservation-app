package com.overdevx.reservationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.overdevx.reservationapp.data.presentation.HomeScreen
import com.overdevx.reservationapp.data.presentation.home.DetailHomeUserScreen
import com.overdevx.reservationapp.data.presentation.home.HomeUserScreen
import com.overdevx.reservationapp.data.presentation.monitoring.MonitoringScreen
import com.overdevx.reservationapp.data.presentation.monitoring.MonitoringScreen2
import com.overdevx.reservationapp.data.presentation.monitoring.admin.Access
import com.overdevx.reservationapp.data.presentation.monitoring.admin.AdminRoomScreen
import com.overdevx.reservationapp.data.presentation.monitoring.admin.AdminRoomScreenC
import com.overdevx.reservationapp.data.presentation.monitoring.auth.LoginScreen
import com.overdevx.reservationapp.data.presentation.monitoring.history.HistoryScreen
import com.overdevx.reservationapp.data.presentation.monitoring.user.RoomsScreen
import com.overdevx.reservationapp.ui.theme.ReservationAppTheme
import com.overdevx.reservationapp.ui.theme.background
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.white2
import com.overdevx.reservationapp.utils.TokenProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import javax.inject.Inject
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.overdevx.reservationapp.data.presentation.SplashScreen
import com.overdevx.reservationapp.data.presentation.home.nonScaledSp
import com.overdevx.reservationapp.data.presentation.monitoring.admin.BookingListScreen
import com.overdevx.reservationapp.data.presentation.monitoring.admin.BookingListScreenDetail
import com.overdevx.reservationapp.data.presentation.monitoring.admin.HomeControlScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenProvider: TokenProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { false }
        enableEdgeToEdge()
        setContent {
            ReservationAppTheme {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = Color(0x33000000).toArgb(),  // Tema terang
                        darkScrim = Color(0x66ffffff).toArgb()
                    )
                )
                ReserApp()
            }
        }
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    fun ReserApp(modifier: Modifier = Modifier) {
        val navController = rememberNavController()
        var showBottomBar by remember { mutableStateOf(true) }

        Scaffold(
            containerColor = background,
            bottomBar = {
                if (showBottomBar) {
                    AppBottomNavigation(navController = navController)
                }
            },

            ) { innerPadding ->
            // Cek apakah token sudah ada
            val token = tokenProvider.getToken()
            val startDestination = HomeRoute
            SharedTransitionLayout {
                NavHost(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                    navController = navController,
                    startDestination = startDestination,
                    enterTransition = { slideInHorizontally { it } },
                    exitTransition = { slideOutHorizontally { -it } },
                    popEnterTransition = { slideInHorizontally { -it } },
                    popExitTransition = { slideOutHorizontally { it } }
                ) {
                    composable<LoginRoute> {
                        LoginScreen(
                            onLoginClick = {
                                navController.navigate(HomeRoute) {
                                    popUpTo(LoginRoute) { inclusive = true }
                                }
                            },
                            navController = navController,
                            modifier = Modifier
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        )
                        showBottomBar = false
                    }
                    composable<HomeRoute> {
                        HomeUserScreen(
                            onClick = { id, deskripsi, roomName, harga, jumlahKamar, rating, foto ->
                                navController.navigate(
                                    DetailHomeUserRoute(
                                        id,
                                        deskripsi,
                                        roomName,
                                        harga,
                                        jumlahKamar,
                                        rating,
                                        foto
                                    )
                                )

                            },
                            navController = navController,
                            animatedVisibilityScope = this,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                        )
                        showBottomBar=true
                    }
                    composable<ControlRoute> {
                        HomeScreen(
                            modifier = Modifier.padding(
                                bottom = innerPadding.calculateBottomPadding()
                            ),
                            onClick = { buildingId, buildingName ->
                                if (buildingId == 3) {
                                    navController.navigate(
                                        RoomsRouteAdminC(
                                            id = buildingId,
                                            name = buildingName
                                        )
                                    )
                                } else {
                                    navController.navigate(
                                        RoomsRouteAdmin(
                                            id = buildingId,
                                            name = buildingName
                                        )
                                    )
                                }
                            },
                            onNavigateBack = {
                                navController.navigateUp()
                            }
                        )
                    }
                    composable<HomeControlRoute> {
                        val token2 by remember { mutableStateOf(tokenProvider.getToken()) }
                        if (token2.isNullOrEmpty()) {
                            Access(
                                onLoginClick = {
                                    navController.navigate(LoginRoute)
                                },
                                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                            )

                        } else {
                            HomeControlScreen(
                                modifier = Modifier.padding(
                                    bottom = innerPadding.calculateBottomPadding()
                                ),
                                onLogoutClick = {
                                    navController.navigate(MonitoringRoute) {
                                        popUpTo<HomeRoute> { inclusive = true }
                                    }
                                },
                                onMenu1Click = {
                                    navController.navigate(ControlRoute)
                                },
                                onMenu2Click = {
                                    navController.navigate(BookingListRoute)
                                },
                                onMenu3Click = {
                                    navController.navigate(HistoryRoute)
                                }
                            )

                        }
                        showBottomBar = true
                    }
                    composable<MonitoringRoute> {
                        MonitoringScreen2(
                            modifier = Modifier.padding(
                                bottom = innerPadding.calculateBottomPadding(),
                                top = 16.dp
                            ),
                            onClick = { buildingId ->
                                navController.navigate(RoomsRouteUser(id = buildingId))
                            },
                        )

                        showBottomBar = true
                    }

                    composable<RoomsRouteUser> {
                        val args = it.toRoute<RoomsRouteUser>()
                        val buildingId = args.id
                        RoomsScreen(
                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                            buildingId,
                            onNavigateBack = {
                                navController.navigateUp()
                            },
                        )
                    }
                    composable<RoomsRouteAdmin> {
                        val args = it.toRoute<RoomsRouteAdmin>()
                        val buildingId = args.id
                        val buildingName = args.name
                        AdminRoomScreen(
                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                            buildingId,
                            buildingName,
                            onNavigateBack = {
                                navController.navigateUp()
                            }
                        )
                    }

                    composable<RoomsRouteAdminC>
                    {
                        val args = it.toRoute<RoomsRouteAdminC>()
                        val buildingId = args.id
                        val buildingName = args.name
                        AdminRoomScreenC(
                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                            buildingId,
                            buildingName,
                            onNavigateBack = {
                                navController.navigateUp()
                            }
                        )
                    }

                    composable<HistoryRoute> {
                        HistoryScreen(
                            onNavigateBack = { navController.navigateUp() },
                            modifier = Modifier
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        )
                        showBottomBar = true
                    }

                    composable<DetailHomeUserRoute> {
                        val args = it.toRoute<DetailHomeUserRoute>()
                        val id = args.id
                        val deskripsi = args.deskripsi
                        val roomName = args.roomName
                        val harga = args.harga
                        val jumlahKamar = args.jumlah_kamar
                        val rating = args.rating
                        val foto = args.foto
                        val context = LocalContext.current
                        DetailHomeUserScreen(
                            id,
                            roomName,
                            harga,
                            rating,
                            deskripsi,
                            jumlahKamar,
                            foto,
                            onClick = {
                                context.startActivity(
                                    // on below line we are opening the intent.
                                    Intent(
                                        // on below line we are calling
                                        // uri to parse the data
                                        Intent.ACTION_VIEW,
                                        Uri.parse(
                                            // on below line we are passing uri,
                                            // message and whats app phone number.
                                            java.lang.String.format(
                                                "https://api.whatsapp.com/send?phone=%s&text=%s",
                                                "+62 81227978072",
                                                "yo wassap hooman"
                                            )
                                        )
                                    )
                                )
                            },
                            onNavigateBack = { navController.navigateUp() },
                            animatedVisibilityScope = this,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            modifier = Modifier
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        )
                    }

                    composable<BookingListRoute> {
                        BookingListScreen(
                            modifier = Modifier
                                .padding(bottom = innerPadding.calculateBottomPadding()),
                            onNavigateBack = { navController.navigateUp() },
                            onClick = { bookingId ->
                                navController.navigate(DetailBookingListRoute(bookingId))
                            }
                        )
                    }
                    composable<DetailBookingListRoute> {
                        val args = it.toRoute<DetailBookingListRoute>()
                        val bookingId = args.bookingId
                        BookingListScreenDetail(
                            onNavigateBack = { navController.navigateUp() },
                            bookingRoomId = bookingId
                        )
                    }
                    composable<SplashScreenRoute> {
                        SplashScreen(
                            onSplashComplete = {
                                val token = tokenProvider.getToken()
                                if (token.isNullOrEmpty()) {
                                    navController.navigate(LoginRoute) {
                                        popUpTo(SplashScreenRoute) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(HomeRoute) {
                                        popUpTo(SplashScreenRoute) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }


    @Composable
    fun AppBottomNavigation(navController: NavController) {
        val bottomScreens = remember {
            listOf(
                BottomScreens.Home,
                BottomScreens.Control,
                BottomScreens.Monitoring,
            )
        }

        NavigationBar(
            containerColor = secondary,
            contentColor = white,
            modifier = Modifier
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            bottomScreens.forEach { screen ->
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == screen.route::class.qualifiedName } == true
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        val icon =
                            painterResource(id = if (isSelected) screen.selectedIconResId else screen.unselectedIconResId)
                        val tint = if (isSelected) white else white2
                        Icon(
                            painter = icon,
                            contentDescription = screen.name,
                            tint = tint
                        )
                    },
                    label = {
                        Text(
                            text = screen.name,
                            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                            fontSize = 10.nonScaledSp,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) white else white2
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                    )
                )
            }
        }
    }


}

@Serializable
data object LoginRoute

@Serializable
data object HomeRoute

@Serializable
data object HomeControlRoute

@Serializable
data object ControlRoute

@Serializable
data object MonitoringRoute

@Serializable
data object HistoryRoute

@Serializable
data object SplashScreenRoute

@Serializable
data class RoomsRouteUser(
    val id: Int
)

@Serializable
data class RoomsRouteAdmin(
    val id: Int,
    val name: String
)

@Serializable
data class RoomsRouteAdminC(
    val id: Int,
    val name: String
)

@Serializable
data class DetailHomeUserRoute(
    val id: Int,
    val deskripsi: String,
    val roomName: String,
    val harga: Int,
    val jumlah_kamar: Int,
    val rating: String,
    val foto: String
)

@Serializable
data class DetailBookingListRoute(
    val bookingId: Int
)

@Serializable
data object BookingListRoute

@Serializable
sealed class BottomScreens<T>(
    val name: String,
    val icon: Int,
    val route: T,
    @DrawableRes val selectedIconResId: Int,
    @DrawableRes val unselectedIconResId: Int
) {
    @Serializable
    data object Home : BottomScreens<HomeRoute>(
        name = "Home",
        icon = R.drawable.ic_home_outline,
        route = HomeRoute,
        selectedIconResId = R.drawable.ic_home_filled,
        unselectedIconResId = R.drawable.ic_home_outline
    )

    @Serializable
    data object Control : BottomScreens<HomeControlRoute>(
        name = "Control",
        icon = R.drawable.ic_settings_outline,
        route = HomeControlRoute,
        selectedIconResId = R.drawable.ic_settings_filled,
        unselectedIconResId = R.drawable.ic_settings_outline
    )

    @Serializable
    data object Monitoring : BottomScreens<MonitoringRoute>(
        name = "Monitoring",
        icon = R.drawable.ic_analytics_outline,
        route = MonitoringRoute,
        selectedIconResId = R.drawable.ic_analytics_filled,
        unselectedIconResId = R.drawable.ic_analytics_outline
    )

    @Serializable
    data object History : BottomScreens<HistoryRoute>(
        name = "History",
        icon = R.drawable.ic_history_outline,
        route = HistoryRoute,
        selectedIconResId = R.drawable.ic_history_filled,
        unselectedIconResId = R.drawable.ic_history_outline
    )


}

