package com.example.lupay.ui

import HomeScreen
import ProfileScreen
import QRScreen
import RegisterScreen
import SecurityScreen
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
import androidx.compose.ui.res.stringResource
import com.example.lupay.R
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

@Composable
private fun TopBarScaffoldWrapper(
    title: String,
    navController: NavHostController,
    deviceType: DeviceType,
    currentRoute: String,
    content: @Composable (PaddingValues) -> Unit
) {
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
            Scaffold(
                topBar = { TopBarComponent(title = title, navController = navController) }
            ) { paddingValues ->
                content(paddingValues)
            }
        }
    } else {
        Scaffold(
            topBar = { TopBarComponent(title = title, navController = navController) },
            bottomBar = { BottomBar(navController) }
        ) { paddingValues ->
            content(paddingValues)
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
                        navController = navController,
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
                    navController = navController,
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
            TopBarScaffoldWrapper(
                title = stringResource(id = R.string.general),
                navController = navController,
                deviceType = deviceType,
                currentRoute = currentRoute
            ) { paddingValues ->
                HomeScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        composable("wallet") {
            TopBarScaffoldWrapper(
                title = stringResource(id = R.string.cards),
                navController = navController,
                deviceType = deviceType,
                currentRoute = currentRoute
            ) { paddingValues ->
                WalletScreen(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        composable("analytics") {
            TopBarScaffoldWrapper(
                title = stringResource(id = R.string.investment),
                navController = navController,
                deviceType = deviceType,
                currentRoute = currentRoute
            ) { paddingValues ->
                InvestmentScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        composable("qr") {
            QRScreen(
                navController = navController);
        }




        composable("profile") {
            TopBarScaffoldWrapper(
                title = stringResource(id = R.string.profile),
                navController = navController,
                deviceType = deviceType,
                currentRoute = currentRoute
            ) { paddingValues ->
                ProfileScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    navController = navController
                )
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

        composable("security_screen") {
            SecurityScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable("add_card") {
            AddCardScreen(navController = navController)
        }
    }
}
