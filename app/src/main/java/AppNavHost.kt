package com.example.lupay.ui

import BottomBar
import HomeScreen
import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.lupay.ui.screens.LoginScreen
import com.example.lupay.ui.screens.RegisterScreen
import com.example.lupay.ui.screens.WalletScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "login"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToMain = { navController.navigate("main") }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToMain = { navController.navigate("main") }
            )
        }
        composable("wallet") {
            Scaffold(
                bottomBar = { BottomBar(navController) }
            ) {
                WalletScreen()
            }
        }
        composable("main") {
            Scaffold(
                bottomBar = { BottomBar(navController) }
            ) {
                HomeScreen()
            }
        }

        composable("qr") {
            Scaffold(
                bottomBar = { BottomBar(navController) }
            ) {
                // Add your QRScreen here
            }
        }
        composable("analytics") {
            Scaffold(
                bottomBar = { BottomBar(navController) }
            ) {
                // Add your AnalyticsScreen here
            }
        }
        composable("profile") {
            Scaffold(
                bottomBar = { BottomBar(navController) }
            ) {
                // Add your ProfileScreen here
            }
        }
    }
}