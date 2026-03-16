package com.indianext.app.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianext.app.ui.theme.AgritechGreen
import com.indianext.app.ui.theme.BackgroundWhite
import com.indianext.app.ui.theme.DeepCharcoal
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

// --- DYNAMIC DATA MODEL ---
data class VerificationData(
    val batchId: String,
    val status: String,
    val aiScore: Int,
    val gradeTitle: String,
    val gradeDesc: String,
    val network: String,
    val origin: String
)

enum class VerifyState { LOADING, SUCCESS }

@Composable
fun VerificationScreen(
    scannedHash: String = "0X7F3...4A9", // This will be passed from the Scanner
    onBack: () -> Unit = {}
) {
    var currentState by remember { mutableStateOf(VerifyState.LOADING) }

    // This data will be populated by your Node.js backend based on the scannedHash
    val liveVerificationData = remember {
        VerificationData(
            batchId = "#0x7F3",
            status = "Authentic",
            aiScore = 94,
            gradeTitle = "Grade A+ Excellent",
            gradeDesc = "Sensor nodes confirm optimal freshness and organic integrity.",
            network = "Mainnet 2.0",
            origin = "Farm 082"
        )
    }

    // Simulate API Network Call & Blockchain Sync
    LaunchedEffect(Unit) {
        delay(3500) // 3.5 seconds of "verifying"
        currentState = VerifyState.SUCCESS
    }

    Crossfade(targetState = currentState, label = "verification_transition") { state ->
        when (state) {
            VerifyState.LOADING -> VerificationLoadingView(scannedHash)
            VerifyState.SUCCESS -> VerificationSuccessView(liveVerificationData, onBack)
        }
    }
}

// ==========================================================
// 1. DARK LOADING STATE
// ==========================================================
@Composable
fun VerificationLoadingView(hash: String) {
    val vibrantGreen = Color(0xFF10B981)
    val darkBg = Color(0xFF121418)

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(4000, easing = LinearEasing)),
        label = "rotation"
    )

    var progress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        animate(0f, 1f, animationSpec = tween(3500, easing = FastOutSlowInEasing)) { value, _ ->
            progress = value
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(darkBg).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        // --- Animated Hexagon Core ---
        Box(modifier = Modifier.size(240.dp).rotate(rotation), contentAlignment = Alignment.Center) {
            // Background Orbit Rings
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color = vibrantGreen.copy(alpha = 0.1f), radius = size.width / 2, style = Stroke(width = 2.dp.toPx()))
                drawCircle(color = vibrantGreen.copy(alpha = 0.2f), radius = size.width / 3, style = Stroke(width = 1.dp.toPx()))
            }

            // Center Hexagon Icon Placeholder
            Icon(Icons.Default.Hexagon, contentDescription = null, tint = vibrantGreen, modifier = Modifier.size(64.dp))

            // Orbiting Hexagons
            Box(modifier = Modifier.fillMaxSize()) {
                Icon(Icons.Default.Hexagon, contentDescription = null, tint = vibrantGreen.copy(alpha = 0.7f), modifier = Modifier.align(Alignment.TopCenter).offset(y = (-12).dp).size(32.dp))
                Icon(Icons.Default.Hexagon, contentDescription = null, tint = vibrantGreen.copy(alpha = 0.7f), modifier = Modifier.align(Alignment.BottomCenter).offset(y = 12.dp).size(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = vibrantGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("SECURE PROTOCOL ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = vibrantGreen, letterSpacing = 1.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Checking Immutable\nLedger...", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center, lineHeight = 36.sp)

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Synchronizing batch data with distributed agricultural nodes. This ensures product authenticity and quality standards.",
            fontSize = 14.sp, color = Color.LightGray, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Progress Bar
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("VERIFYING HASH", fontSize = 10.sp, color = vibrantGreen, fontWeight = FontWeight.Bold)
            Text(hash.uppercase(), fontSize = 10.sp, color = vibrantGreen, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress, modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = vibrantGreen, trackColor = vibrantGreen.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            shape = RoundedCornerShape(24.dp), color = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Processing Results", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ==========================================================
// 2. LIGHT SUCCESS STATE
// ==========================================================
@Composable
fun VerificationSuccessView(data: VerificationData, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundWhite).verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = DeepCharcoal) }
            Text("Verification Result", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
        }

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))

            // Big Green Checkmark
            Surface(shape = CircleShape, color = AgritechGreen, modifier = Modifier.size(80.dp)) {
                Icon(Icons.Default.Check, contentDescription = "Success", tint = Color.White, modifier = Modifier.padding(16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Verification Success", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(data.batchId, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AgritechGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Surface(shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color.LightGray), color = Color.Transparent) {
                    Text(data.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- GEMINI AI QUALITY REPORT CARD ---
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AgritechGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("GEMINI AI QUALITY REPORT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Circular Progress Score
                    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = data.aiScore / 100f,
                            modifier = Modifier.fillMaxSize(),
                            color = AgritechGreen,
                            trackColor = AgritechGreen.copy(alpha = 0.1f),
                            strokeWidth = 12.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(data.aiScore.toString(), fontSize = 36.sp, fontWeight = FontWeight.Black, color = DeepCharcoal)
                            Text("/ 100", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).background(AgritechGreen, CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(data.gradeTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(data.gradeDesc, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTTOM INFO CARDS ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedCard(modifier = Modifier.weight(1f), colors = CardDefaults.outlinedCardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("NETWORK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(data.network, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    }
                }

                OutlinedCard(modifier = Modifier.weight(1f), colors = CardDefaults.outlinedCardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EnergySavingsLeaf, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ORIGIN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(data.origin, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}