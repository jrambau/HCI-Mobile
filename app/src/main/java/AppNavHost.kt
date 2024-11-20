package com.example.lupay.ui

import BottomBar
import HomeScreen
import LoginViewModel
import RegisterScreen
import TopBarComponent
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.lupay.ui.screens.InvestmentScreen
import com.example.lupay.ui.screens.LoginScreen
import com.example.lupay.ui.screens.ProfileScreen
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
                onNavigateToMain = { navController.navigate("main") },
            )
        }
        composable("wallet") {
            Scaffold(
                bottomBar = { BottomBar(navController) },
                topBar = { TopBarComponent("john doe", "Tarjetas", navController) } // Pass navController
            ) { paddingValues -> // Ensure the padding values are passed to the WalletScreen
                WalletScreen()
            }
        }
        composable("main") {
            Scaffold(
                bottomBar = { BottomBar(navController) },
                topBar = { TopBarComponent("john doe", "General", navController) } // Pass navController
            ) { paddingValues ->
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
                bottomBar = { BottomBar(navController) },
                topBar = { TopBarComponent("john doe", "Inversiones", navController) } // Pass navController
            ) {
                InvestmentScreen()
            }
        }
        composable("profile") {
            Scaffold(
                bottomBar = { BottomBar(navController) },
                topBar = { TopBarComponent("john doe", "Perfil", navController) } // Pass navController
            ) {
                ProfileScreen(modifier = Modifier.padding(it))
            }
        }
    }
}
