package com.indianext.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.compose.runtime.*

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
                onRoleSelected = { roleId ->
                    when (roleId) {
                        "Farmer" -> navController.navigate(Screen.FarmerHub.route)
                        "Transporter" -> navController.navigate(Screen.LogisticsHub.route)
                        "Retailer" -> navController.navigate(Screen.RetailerHub.route)
                        "Consumer" -> navController.navigate("product_journey") // Pointing consumer here!
                    }
                }
            )
        }

        // --- HUB PLACEHOLDERS ---
        composable(Screen.FarmerHub.route) { FarmerHubScreen() }
        composable(Screen.LogisticsHub.route) { LogisticsHubScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.RetailerHub.route) {
            RetailerHubScreen(
                onScanClick = { navController.navigate(Screen.Scanner.route) }
            )
        }
        composable(Screen.Scanner.route) {
            // 1. The Machine Gun Fix: A flag to ensure we only navigate ONCE
            var hasNavigated by remember { mutableStateOf(false) }

            UniversalScannerScreen(
                onScanSuccess = { scannedHash ->
                    if (!hasNavigated) {
                        hasNavigated = true

                        // 2. The URL Fix: Encode the raw string so slashes don't break the router
                        val safeHash = URLEncoder.encode(scannedHash, StandardCharsets.UTF_8.toString())

                        navController.navigate("verification_screen/$safeHash") {
                            popUpTo(Screen.Scanner.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("verification_screen/{hash}") { backStackEntry ->
            // Grab the safe string from the route
            val safeHash = backStackEntry.arguments?.getString("hash") ?: "Unknown"

            // Decode it back to its original form (e.g., bringing the slashes back)
            val decodedHash = URLDecoder.decode(safeHash, StandardCharsets.UTF_8.toString())

            VerificationScreen(
                scannedHash = decodedHash,
                onBack = { navController.popBackStack() }
            )
        }
        composable("product_journey") {
            ProductJourneyScreen(onBack = { navController.popBackStack() })
        }
    }
}