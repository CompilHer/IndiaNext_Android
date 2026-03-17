package com.indianext.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianext.app.ui.theme.AgritechGreen
import com.indianext.app.ui.theme.BackgroundWhite
import com.indianext.app.ui.theme.DeepCharcoal
import kotlin.math.roundToInt

// --- DYNAMIC DATA MODELS ---
data class TransitState(
    val isNetworkLive: Boolean = true,
    val batchId: String = "Batch #0x4A2...13b",
    val route: String = "In Transit: Farm -> Distribution Hub",
    val nodeId: String = "NODE_ID: LGS-882",
    val locationName: String = "Mumbai, MH",
    val coordinates: String = "(73.921, 18.512)",
    val temperature: String = "4.2°C",
    val signalStrength: String = "88%",
    val lastSync: String = "12m ago",
    val battery: String = "External (98%)"
)

data class ActivityEvent(val title: String, val time: String, val status: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun LogisticsHubScreen(
    onBack: () -> Unit = {},
    onScanClick: () -> Unit = {}
) {
    var transitState by remember { mutableStateOf(TransitState()) }

    val dynamicEvents = listOf(
        ActivityEvent("Cold Chain Verified", "14:20", "NODE_SYNC_OK", Icons.Default.CheckCircleOutline),
        ActivityEvent("Mumbai Outskirts Entry", "13:55", "NODE_SYNC_OK", Icons.Default.LocationOn),
        ActivityEvent("Manifest Uploaded", "12:30", "NODE_SYNC_OK", Icons.Default.Inventory2)
    )

    Scaffold(
        containerColor = BackgroundWhite,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onScanClick,
                containerColor = AgritechGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", modifier = Modifier.size(28.dp))
                    Text("SCAN", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWhite)
                .padding(innerPadding)
                .padding(top = 16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = DeepCharcoal) }
                    Column {
                        Text(transitState.batchId, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepCharcoal)
                        Text(transitState.route, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.Gray)
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color.LightGray), color = Color.Transparent) {
                    Text(transitState.nodeId, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (transitState.isNetworkLive) Icons.Default.Bluetooth else Icons.Default.BluetoothDisabled, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color.LightGray), color = Color.Transparent) {
                        Text(if (transitState.isNetworkLive) "NETWORK LIVE" else "OFFLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }

            Column(modifier = Modifier.weight(1f).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.outlinedCardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
                    Column {
                        if (!transitState.isNetworkLive) {
                            Surface(color = Color(0xFFFAFAFA), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WarningAmber, contentDescription = "Warning", tint = DeepCharcoal)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text("SENSORS CACHED LOCALLY - NO NETWORK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                        Text("Data will sync automatically upon reconnection", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                            }
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        }

                        Text("CURRENT TRANSIT CONDITIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgritechGreen, modifier = Modifier.padding(16.dp))

                        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(horizontal = 16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = if(transitState.isNetworkLive) AgritechGreen else Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("LIVE GPS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(transitState.locationName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                Text(transitState.coordinates, fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(24.dp))
                                if (transitState.isNetworkLive) Text("Signal: ${transitState.signalStrength}", fontSize = 10.sp, color = Color.Gray)
                                else Text("CACHING ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                            }
                            VerticalDivider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Thermostat, contentDescription = null, tint = if(transitState.isNetworkLive) AgritechGreen else Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("CURRENT TEMP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(transitState.temperature, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(12.dp), tint = DeepCharcoal)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (transitState.isNetworkLive) "Within Range" else "STABLE RANGE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom, modifier = Modifier.height(16.dp)) {
                                    val bars = listOf(0.4f, 0.6f, 0.5f, 0.8f, 0.9f, 0.8f, 0.7f, 0.8f, 0.9f)
                                    val barColor = if(transitState.isNetworkLive) AgritechGreen.copy(alpha = 0.4f) else Color.LightGray.copy(alpha=0.3f)
                                    bars.forEach { heightRatio ->
                                        Box(modifier = Modifier.width(8.dp).fillMaxHeight(heightRatio).background(barColor, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)))
                                    }
                                }
                            }
                        }
                        Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                                Icon(if (transitState.isNetworkLive) Icons.Default.Info else Icons.Default.SignalWifiOff, contentDescription = null, tint = DeepCharcoal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(if (transitState.isNetworkLive) "Data pulled instantly from connected Bluetooth sensors." else "Connectivity lost. Data is being queued for local storage.", fontSize = 10.sp, color = DeepCharcoal, lineHeight = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedContent(targetState = transitState.isNetworkLive, label = "middle_section") { isLive ->
                    if (isLive) {
                        Column {
                            Text("RECENT ACTIVITY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            dynamicEvents.forEach { event ->
                                OutlinedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.outlinedCardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
                                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(event.icon, contentDescription = null, tint = AgritechGreen, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(event.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                            Text("${event.time} | ${event.status}", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                            OutlinedCard(modifier = Modifier.fillMaxWidth().clickable { transitState = transitState.copy(isNetworkLive = false) }, colors = CardDefaults.outlinedCardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
                                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WifiOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("SIMULATE SIGNAL LOSS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                        Text("Switch to Offline Mode", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedCard(modifier = Modifier.weight(1f), colors = CardDefaults.outlinedCardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Icon(Icons.Default.History, contentDescription = null, tint = DeepCharcoal, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("LAST SYNC", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                    Text(transitState.lastSync, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                }
                            }
                            OutlinedCard(modifier = Modifier.weight(1f), colors = CardDefaults.outlinedCardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Icon(Icons.Default.ShowChart, contentDescription = null, tint = DeepCharcoal, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("BATTERY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                    Text(transitState.battery, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                SwipeToConfirmAction(
                    isLive = transitState.isNetworkLive,
                    onConfirm = {
                        if (!transitState.isNetworkLive) { transitState = transitState.copy(isNetworkLive = true) }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (!transitState.isNetworkLive) {
                    Text("SECURE LOCAL CACHING ENABLED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, letterSpacing = 0.5.sp)
                }
            }
        }
    }
}

@Composable
fun SwipeToConfirmAction(isLive: Boolean, onConfirm: () -> Unit) {
    val trackColor = if (isLive) Color(0xFFF0FDF4) else Color(0xFFF5F5F5)
    val thumbColor by animateColorAsState(if (isLive) AgritechGreen else Color.Transparent)
    val textColor = if (isLive) DeepCharcoal else Color.Gray

    val trackWidthDp = 300.dp
    val thumbSizeDp = 56.dp
    val trackWidthPx = with(LocalDensity.current) { trackWidthDp.toPx() }
    val thumbSizePx = with(LocalDensity.current) { thumbSizeDp.toPx() }
    val maxDragPx = trackWidthPx - thumbSizePx

    var dragOffset by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .width(trackWidthDp)
            .height(thumbSizeDp)
            .background(trackColor, RoundedCornerShape(percent = 50))
            .border(1.dp, if(isLive) AgritechGreen.copy(alpha=0.3f) else Color.LightGray, RoundedCornerShape(percent = 50)),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = if (isLive) "SWIPE TO LOG CHECKPOINT" else ">>> SWIPE TO CACHE LOCALLY >>>",
            color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .offset { IntOffset(dragOffset.roundToInt(), 0) }
                .size(thumbSizeDp)
                .clip(CircleShape)
                .background(thumbColor)
                .border(1.dp, if(isLive) Color.Transparent else Color.Gray, CircleShape)
                .pointerInput(isLive) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragOffset > maxDragPx * 0.8f) {
                                dragOffset = maxDragPx; onConfirm(); dragOffset = 0f
                            } else dragOffset = 0f
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        dragOffset = (dragOffset + dragAmount).coerceIn(0f, maxDragPx)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = if (isLive) Color.White else DeepCharcoal, modifier = Modifier.size(32.dp))
        }
    }
}