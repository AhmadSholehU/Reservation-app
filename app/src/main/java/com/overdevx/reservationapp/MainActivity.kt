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
import com.overdevx.reservationapp.data.presentation.monitoring.MonitoringScreen
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenProvider: TokenProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            val startDestination = if (token.isNullOrEmpty()) {
                MonitoringRoute
            } else {
                HomeRoute
            }
            NavHost(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                navController = navController,
                startDestination = startDestination
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
                    val token2 by remember { mutableStateOf(tokenProvider.getToken()) }
                    if (token2.isNullOrEmpty()) {
                      Access(
                          onLoginClick = {
                              navController.navigate(LoginRoute)
                          },
                          modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()))

                    } else {
                        HomeScreen(
                            modifier = Modifier.padding(
                                end = 16.dp, start = 16.dp
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
                            onLogoutClick = {
                                navController.navigate(MonitoringRoute) {
                                    popUpTo<HomeRoute> { inclusive = true }
                                }
                            }
                        )
                    }

                    showBottomBar = true
                }
                composable<MonitoringRoute> {
                    MonitoringScreen(
                        modifier = Modifier.padding(
                            end = 16.dp,
                            start = 16.dp,
                            bottom = innerPadding.calculateBottomPadding()
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
                        modifier = Modifier,
                        buildingId,
                        onNavigateBack = {
                            navController.navigateUp()
                        },
                    )
                }
                composable<RoomsRouteAdmin>(
                    enterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            500, easing = LinearEasing
                        )
                    ) + slideIntoContainer(
                        animationSpec = tween(500, easing = EaseIn),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },
                    exitTransition = {
                        fadeOut(
                            animationSpec = tween(
                                500, easing = LinearEasing
                            )
                        ) + slideOutOfContainer(
                            animationSpec = tween(500, easing = EaseOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.End
                        )
                    }) {
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

                composable<RoomsRouteAdminC>( enterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            500, easing = LinearEasing
                        )
                    ) + slideIntoContainer(
                        animationSpec = tween(500, easing = EaseIn),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                },
                    exitTransition = {
                        fadeOut(
                            animationSpec = tween(
                                500, easing = LinearEasing
                            )
                        ) + slideOutOfContainer(
                            animationSpec = tween(500, easing = EaseOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.End
                        )
                    }) {
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
                    HistoryScreen(modifier = Modifier
                        .padding(bottom = innerPadding.calculateBottomPadding()))
                    showBottomBar = true
                }
            }
        }
    }

    @Composable
    fun AppBottomNavigation(navController: NavController) {
        val bottomScreens = remember {
            listOf(
                BottomScreens.Home,
                BottomScreens.Monitoring,
                BottomScreens.History,
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
                            fontSize = 14.sp,
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
data object MonitoringRoute

@Serializable
data object HistoryRoute

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
sealed class BottomScreens<T>(
    val name: String,
    val icon: Int,
    val route: T,
    @DrawableRes val selectedIconResId: Int,
    @DrawableRes val unselectedIconResId: Int
) {
    @Serializable
    data object Home : BottomScreens<HomeRoute>(
        name = "Control",
        icon = R.drawable.ic_control,
        route = HomeRoute,
        selectedIconResId = R.drawable.ic_control,
        unselectedIconResId = R.drawable.ic_control
    )

    @Serializable
    data object Monitoring : BottomScreens<MonitoringRoute>(
        name = "Monitoring",
        icon = R.drawable.ic_monitoring,
        route = MonitoringRoute,
        selectedIconResId = R.drawable.ic_monitoring,
        unselectedIconResId = R.drawable.ic_monitoring
    )

    @Serializable
    data object History : BottomScreens<HistoryRoute>(
        name = "History",
        icon = R.drawable.ic_history,
        route = HistoryRoute,
        selectedIconResId = R.drawable.ic_history,
        unselectedIconResId = R.drawable.ic_history
    )


}

