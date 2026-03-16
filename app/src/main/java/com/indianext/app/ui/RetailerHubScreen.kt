package com.indianext.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianext.app.ui.theme.AgritechGreen
import com.indianext.app.ui.theme.BackgroundWhite
import com.indianext.app.ui.theme.DeepCharcoal

// --- DYNAMIC DATA MODEL ---
data class Shipment(
    val batchId: String,
    val supplierName: String,
    val eta: String,
    val qualityScore: String,
    val statusText: String
)

@Composable
fun RetailerHubScreen(
    onScanClick: () -> Unit = {} // Will route to your Universal Scanner
) {
    var selectedTab by remember { mutableStateOf(0) }

    // This list will eventually come from your ViewModel/Node.js Backend
    val activeShipments = listOf(
        Shipment("#0x7F3A2", "Green Valley Organics", "Estimated in 2h", "98% Quality Check", "Awaiting Scan"),
        Shipment("#0x8E1C4", "Highland Orchards", "Estimated in 5h", "98% Quality Check", "Awaiting Scan"),
        Shipment("#0x2B9D9", "Sun-Kissed Vineyards", "Arriving Tomorrow", "98% Quality Check", "Awaiting Scan")
    )

    Scaffold(
        containerColor = BackgroundWhite,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onScanClick,
                containerColor = AgritechGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(72.dp).offset(y = 36.dp) // Offsets to overlap the bottom nav nicely
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", modifier = Modifier.size(28.dp))
                    Text("SCAN", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        bottomBar = {
            RetailerBottomNavigation()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- TOP HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = DeepCharcoal)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(8.dp), color = DeepCharcoal, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.EnergySavingsLeaf, contentDescription = "Logo", tint = Color.White, modifier = Modifier.padding(4.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retailer Hub", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                }

                Box {
                    Icon(Icons.Default.NotificationsNone, contentDescription = "Alerts", tint = DeepCharcoal, modifier = Modifier.size(28.dp))
                    Box(modifier = Modifier.size(10.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd).border(1.dp, BackgroundWhite, CircleShape))
                }
            }

            // --- TABS ---
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                TabItem("Incoming Shipments", isSelected = selectedTab == 0, modifier = Modifier.weight(1f).clickable { selectedTab = 0 })
                TabItem("Verified Inventory", isSelected = selectedTab == 1, modifier = Modifier.weight(1f).clickable { selectedTab = 1 })
            }
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(16.dp))

            // --- LIST HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("IN TRANSIT (${activeShipments.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                Text("Auto-refreshing", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgritechGreen)
            }

            // --- DYNAMIC LIST CONTENT ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(activeShipments) { shipment ->
                    ShipmentCard(shipment)
                }

                // Logistics Pulse Card (Footer of the list)
                item {
                    LogisticsPulseCard()
                    Spacer(modifier = Modifier.height(40.dp)) // Extra padding for the FAB
                }
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun TabItem(title: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) AgritechGreen else Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (isSelected) {
            Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(AgritechGreen, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)))
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(Color.Transparent))
        }
    }
}

@Composable
fun ShipmentCard(shipment: Shipment) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // Left Icon Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AgritechGreen.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AgritechGreen, modifier = Modifier.padding(14.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Middle Content
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(shipment.batchId, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = AgritechGreen.copy(alpha = 0.1f)) {
                        Text(shipment.statusText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AgritechGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(shipment.supplierName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DeepCharcoal)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(shipment.eta, fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(Icons.Default.Timeline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(shipment.qualityScore, fontSize = 11.sp, color = Color.Gray)
                }
            }

            // Right Chevron
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun LogisticsPulseCard() {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFFAFAFA)),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = AgritechGreen.copy(alpha = 0.1f), modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.ShowChart, contentDescription = null, tint = AgritechGreen, modifier = Modifier.padding(6.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Logistics Pulse", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mock Bar Chart
            Row(modifier = Modifier.fillMaxWidth().height(40.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                val heights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.8f, 0.5f, 0.95f, 0.7f)
                heights.forEach { ratio ->
                    Box(modifier = Modifier.width(12.dp).fillMaxHeight(ratio).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(AgritechGreen.copy(alpha = 0.3f)))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                buildAnnotatedString {
                    append("Current supply chain throughput is ")
                    withStyle(style = SpanStyle(color = AgritechGreen, fontWeight = FontWeight.Bold)) {
                        append("12% higher")
                    }
                    append(" than last week's average.")
                },
                fontSize = 12.sp, color = Color.Gray, lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun RetailerBottomNavigation() {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Default.Home, "Home", isSelected = true)
            BottomNavItem(Icons.Default.Inventory2, "Inventory", isSelected = false)
            BottomNavItem(Icons.Default.VerifiedUser, "Verify", isSelected = false)
            Spacer(modifier = Modifier.width(48.dp)) // Space for the FAB!
            BottomNavItem(Icons.Default.PersonOutline, "Profile", isSelected = false)
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
        Icon(icon, contentDescription = label, tint = if (isSelected) AgritechGreen else Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) AgritechGreen else Color.Gray)
    }
}