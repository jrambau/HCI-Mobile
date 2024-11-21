package com.example.lupay.ui

import HomeScreen
import RegisterScreen
import components.TopBarComponent
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import components.NavigationRail
import components.TabletLayout
import com.example.lupay.ui.screens.*
import com.example.lupay.ui.utils.DeviceType
import com.example.lupay.ui.utils.rememberDeviceType
import components.BottomBar

@Composable
private fun TabletScreenWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(600.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp)
        ) {
            content()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "login"
) {
    val deviceType = rememberDeviceType()
    val currentRoute = navController
        .currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)
        .value?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") {
            if (deviceType == DeviceType.TABLET) {
                TabletScreenWrapper {
                    LoginScreen(
                        onNavigateToRegister = { navController.navigate("register") },
                        onNavigateToMain = {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
            } else {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate("register") },
                    onNavigateToMain = {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable("register") {
            if (deviceType == DeviceType.TABLET) {
                TabletScreenWrapper {
                    RegisterScreen(
                        onNavigateToLogin = { navController.navigate("login") },
                        onNavigateToMain = {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
            } else {
                RegisterScreen(
                    onNavigateToLogin = { navController.navigate("login") },
                    onNavigateToMain = {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable("main") {
            if (deviceType == DeviceType.TABLET) {
                TabletLayout(
                    navigationRail = {
                        NavigationRail(
                            currentRoute = currentRoute,
                            onNavigate = { route -> 
                                navController.navigate(route) {
                                    popUpTo("main")
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                ) {
                    HomeScreen()
                }
            } else {
                Scaffold(
                    bottomBar = { BottomBar(navController) }
                ) { paddingValues ->
                    HomeScreen()
                }
            }
        }

        composable("wallet") {
            if (deviceType == DeviceType.TABLET) {
                TabletLayout(
                    navigationRail = {
                        NavigationRail(
                            currentRoute = currentRoute,
                            onNavigate = { route -> 
                                navController.navigate(route) {
                                    popUpTo("main")
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                ) {
                    WalletScreen(navController)
                }
            } else {
                Scaffold(
                    bottomBar = { BottomBar(navController) }
                ) { paddingValues ->
                    WalletScreen(navController)
                }
            }
        }

        composable("analytics") {
            if (deviceType == DeviceType.TABLET) {
                TabletLayout(
                    navigationRail = {
                        NavigationRail(
                            currentRoute = currentRoute,
                            onNavigate = { route -> 
                                navController.navigate(route) {
                                    popUpTo("main")
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                ) {
                    InvestmentScreen()
                }
            } else {
                Scaffold(
                    bottomBar = { BottomBar(navController) },
                    topBar = { TopBarComponent("john doe", "Inversiones", navController) }
                ) { paddingValues ->
                    InvestmentScreen()
                }
            }
        }

        composable("qr") {
            if (deviceType == DeviceType.TABLET) {
                TabletLayout(
                    navigationRail = {
                        NavigationRail(
                            currentRoute = currentRoute,
                            onNavigate = { route -> 
                                navController.navigate(route) {
                                    popUpTo("main")
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("QR Screen")
                    }
                }
            } else {
                Scaffold(
                    bottomBar = { BottomBar(navController) }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("QR Screen")
                    }
                }
            }
        }

        composable("profile") {
            if (deviceType == DeviceType.TABLET) {
                TabletLayout(
                    navigationRail = {
                        NavigationRail(
                            currentRoute = currentRoute,
                            onNavigate = { route -> 
                                navController.navigate(route) {
                                    popUpTo("main")
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                ) {
                    ProfileScreen(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController
                    )
                }
            } else {
                Scaffold(
                    bottomBar = { BottomBar(navController) },
                    topBar = { TopBarComponent("john doe", "Perfil", navController) }
                ) { paddingValues ->
                    ProfileScreen(
                        modifier = Modifier.padding(paddingValues),
                        navController = navController
                    )
                }
            }
        }

        composable("personal_info") {
            PersonalInfoScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable("account_info") {
            AccountInfoScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable("add_card") {
            AddCardScreen(navController = navController)
        }
    }
}