package com.indianext.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Define all our routes securely
sealed class Screen(val route: String) {
    object Gateway : Screen("gateway")
    object FarmerHub : Screen("farmer_hub")
    object LogisticsHub : Screen("logistics_hub")
    object RetailerHub : Screen("retailer_hub")
    object Scanner : Screen("universal_scanner")
    // Consumer trace needs to pass the scanned batch ID
    object ConsumerTrace : Screen("consumer_trace/{batchId}") {
        fun createRoute(batchId: String) = "consumer_trace/$batchId"
    }
}

@Composable
fun IndiaNextNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Gateway.route) {

        composable(Screen.Gateway.route) {
            GatewayScreen(
                onRoleSelected = { role ->
                    // In a real app, save to DataStore here before navigating
                    when (role) {
                        "Farmer" -> navController.navigate(Screen.FarmerHub.route)
                        "Transporter" -> navController.navigate(Screen.LogisticsHub.route)
                        "Retailer" -> navController.navigate(Screen.RetailerHub.route)
                        "Consumer" -> navController.navigate(Screen.Scanner.route)
                    }
                }
            )
        }

        // --- HUB PLACEHOLDERS ---
        composable(Screen.FarmerHub.route) { FarmerHubScreen() }
        composable(Screen.LogisticsHub.route) { /* TODO: Screen D */ }
        composable(Screen.RetailerHub.route) { /* TODO: Screen E */ }
        composable(Screen.Scanner.route) { /* TODO: Screen C */ }
    }
}