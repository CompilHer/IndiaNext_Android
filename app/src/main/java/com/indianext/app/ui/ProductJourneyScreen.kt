package com.indianext.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianext.app.ui.theme.AgritechGreen
import com.indianext.app.ui.theme.BackgroundWhite
import com.indianext.app.ui.theme.DeepCharcoal

// --- DYNAMIC DATA MODELS ---
data class ProductJourneyState(
    val brandName: String = "VedaGrow",
    val gradeTitle: String = "Grade A+ Excellent",
    val gradeSubtitle: String = "AI Quality Inspected via Gemini Pro Vision",
    val auditTitle: String = "Supply Chain Audit",
    val auditSubtitle: String = "Tracing from Seed to Shelf",
    val networkName: String = "VERIFIED ON ETHEREUM",
    val txHash: String = "Transaction Hash: 0x9b7a421f5e8ef9c00b...8e155c",
    val auditNodes: List<AuditNode> = emptyList()
)

data class AuditNode(
    val title: String,
    val timestamp: String,
    val geohash: String,
    val icon: ImageVector,
    val isLast: Boolean = false,

    // Optional node-specific extras
    val statusPillText: String? = null,
    val isStatusPositive: Boolean = true,
    val temperatureStr: String? = null,
    val transitStatus: String? = null,
    val description: String? = null,
    val documentButtonText: String? = null
)

@Composable
fun ProductJourneyScreen(onBack: () -> Unit = {}) {
    // This state will eventually be driven by your ViewModel/Node.js Backend
    val screenState by remember {
        mutableStateOf(
            ProductJourneyState(
                auditNodes = listOf(
                    AuditNode(
                        title = "Final Destination: Supermarket Shelf",
                        timestamp = "Oct 24, 2023 • 08:15 AM",
                        geohash = "geohash: w21z7px",
                        icon = Icons.Default.Storefront,
                        statusPillText = "Authentic Verified"
                    ),
                    AuditNode(
                        title = "Logistics Checkpoint: Cold Storage",
                        timestamp = "Oct 22, 2023 • 11:40 PM",
                        geohash = "geohash: w21z3qs",
                        icon = Icons.Default.LocalShipping,
                        temperatureStr = "4.1°C"
                    ),
                    AuditNode(
                        title = "Export Hub: Regional Sorting",
                        timestamp = "Oct 21, 2023 • 09:20 AM",
                        geohash = "geohash: w21y9cb",
                        icon = Icons.Default.LocalShipping,
                        transitStatus = "IN TRANSIT: SEA FREIGHT"
                    ),
                    AuditNode(
                        title = "Origin Farm: Green Valley Organics",
                        timestamp = "Oct 18, 2023 • 06:00 AM",
                        geohash = "geohash: w21u8rv",
                        icon = Icons.Default.EnergySavingsLeaf,
                        description = "Harvested at peak ripeness using sustainable regenerative practices.",
                        documentButtonText = "View Organic Certificate (IPFS)",
                        isLast = true
                    )
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .verticalScroll(rememberScrollState())
    ) {
        // --- 1. HERO IMAGE HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            // Mocking the Image with a lush yellow/orange gradient for the mangoes
            // TODO: Replace with Coil/Glide AsyncImage when networking is hooked up
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFFFFB703), Color(0xFFFB8500))))
            )

            // Top Bar (Brand + Back Button)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(40.dp).clickable { onBack() }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(24.dp)) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text(screenState.brandName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Overlapping Quality Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp)
                    .fillMaxWidth(0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = CircleShape, color = AgritechGreen.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Memory, contentDescription = null, tint = AgritechGreen, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(screenState.gradeTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AgritechGreen, modifier = Modifier.size(16.dp))
                        }
                        Text(screenState.gradeSubtitle, fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // --- 2. AUDIT TITLE SECTION ---
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(screenState.auditTitle, fontSize = 24.sp, fontWeight = FontWeight.Black, color = DeepCharcoal)
            Text(screenState.auditSubtitle, fontSize = 14.sp, color = Color.Gray, fontStyle = FontStyle.Italic)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. DYNAMIC TIMELINE ---
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            screenState.auditNodes.forEach { node ->
                TimelineNodeView(node)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 48.dp))
        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. BLOCKCHAIN FOOTER ---
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                color = Color.Transparent
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(AgritechGreen, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(screenState.networkName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, letterSpacing = 1.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(screenState.txHash, fontSize = 9.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 32.dp), textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GppGood, contentDescription = null, tint = AgritechGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("IMMUTABLE LEDGER SECURED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AgritechGreen, letterSpacing = 0.5.sp)
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun TimelineNodeView(node: AuditNode) {
    // IntrinsicSize.Min allows the vertical line to stretch exactly to the height of the dynamic content
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {

        // Left Column (Icon + Connecting Line)
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
            Surface(
                shape = CircleShape,
                border = BorderStroke(2.dp, AgritechGreen),
                color = Color.White,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(node.icon, contentDescription = null, tint = AgritechGreen, modifier = Modifier.padding(8.dp))
            }

            // Draw the line only if it's not the last node
            if (!node.isLast) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .weight(1f) // Fills the remaining height of the row
                        .background(AgritechGreen.copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Right Column (Details Content)
        Column(modifier = Modifier.weight(1f).padding(bottom = 32.dp)) {
            Text(node.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(node.timestamp, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(node.geohash, fontSize = 12.sp, color = Color.Gray)
            }

            // Optional Dynamic Extras based on Node Type
            Spacer(modifier = Modifier.height(12.dp))

            if (node.statusPillText != null) {
                Surface(shape = RoundedCornerShape(12.dp), color = AgritechGreen.copy(alpha = 0.1f)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = AgritechGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(node.statusPillText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AgritechGreen)
                    }
                }
            }

            if (node.temperatureStr != null) {
                Surface(shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)), color = Color.Transparent) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Thermostat, contentDescription = null, tint = AgritechGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AVG TEMP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(node.temperatureStr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.TrendingDown, contentDescription = null, tint = AgritechGreen, modifier = Modifier.size(14.dp))
                    }
                }
            }

            if (node.transitStatus != null) {
                Text(node.transitStatus, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AgritechGreen, letterSpacing = 1.sp)
            }

            if (node.description != null) {
                Text(node.description, fontSize = 12.sp, color = DeepCharcoal, lineHeight = 18.sp, modifier = Modifier.padding(top = 4.dp))
            }

            if (node.documentButtonText != null) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { /* TODO: Open IPFS Link */ },
                    border = BorderStroke(1.dp, AgritechGreen.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Text(node.documentButtonText, color = AgritechGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}