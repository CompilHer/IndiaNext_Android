package com.indianext.app.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianext.app.ui.theme.AgritechGreen
import com.indianext.app.ui.theme.BackgroundWhite
import com.indianext.app.ui.theme.DeepCharcoal

// --- STATE MACHINE ---
enum class MintStep { DASHBOARD, DETAILS, LOCATION, PROOF, SUCCESS }

@Composable
fun FarmerHubScreen() {
    // --- DYNAMIC STATE VARIABLES (Ready for Backend/ViewModel) ---
    var currentStep by remember { mutableStateOf(MintStep.DASHBOARD) }

    // User Data
    val farmerName by remember { mutableStateOf("Kisan") }
    val seasonTotal by remember { mutableStateOf("0.00 kg") }

    // Form Data
    var cropType by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var harvestDate by remember { mutableStateOf("") }

    // Hardware/API Data
    var currentGeohash by remember { mutableStateOf("Fetching...") }
    var generatedTxHash by remember { mutableStateOf("") }

    val lightGreenBg = Color(0xFFF0FDF4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // --- GLOBAL TOP BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, contentDescription = "Logo", tint = DeepCharcoal)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Farm Dashboard", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
            }
            Surface(
                shape = CircleShape,
                color = lightGreenBg,
                border = BorderStroke(1.dp, AgritechGreen.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).background(AgritechGreen, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SYNCING BLOCKCHAIN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AgritechGreen)
                }
            }
        }

        Divider(color = Color.LightGray.copy(alpha = 0.5f))

        // --- DYNAMIC CONTENT ROUTER ---
        Crossfade(targetState = currentStep, modifier = Modifier.weight(1f)) { step ->
            when (step) {
                MintStep.DASHBOARD -> DashboardView(
                    farmerName = farmerName,
                    seasonTotal = seasonTotal,
                    onMintClick = { currentStep = MintStep.DETAILS }
                )
                MintStep.DETAILS -> DetailsView(
                    cropType = cropType, onCropChange = { cropType = it },
                    weight = weight, onWeightChange = { weight = it },
                    date = harvestDate, onDateChange = { harvestDate = it },
                    onNext = { currentStep = MintStep.LOCATION }
                )
                MintStep.LOCATION -> LocationView(
                    geohash = currentGeohash,
                    onBack = { currentStep = MintStep.DETAILS },
                    onNext = { currentStep = MintStep.PROOF }
                )
                MintStep.PROOF -> ProofView(
                    onBack = { currentStep = MintStep.LOCATION },
                    onPublish = {
                        // Simulate API Call & Blockchain Mint
                        generatedTxHash = "0xabc123...def456"
                        currentStep = MintStep.SUCCESS
                    }
                )
                MintStep.SUCCESS -> SuccessView(
                    txHash = generatedTxHash,
                    onHome = {
                        // Reset Flow
                        cropType = ""; weight = ""; harvestDate = ""
                        currentStep = MintStep.DASHBOARD
                    }
                )
            }
        }
    }
}

// --- SUB-VIEWS ---

@Composable
fun DashboardView(farmerName: String, seasonTotal: String, onMintClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Welcome, $farmerName!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
        Text("Monitor and mint your sustainable harvests.", color = Color.Gray, modifier = Modifier.padding(bottom = 24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("SEASON TOTAL", seasonTotal, Modifier.weight(1f))
            StatCard("LAST SYNC", "Just Now", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Empty State
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("No Recent Harvests", fontWeight = FontWeight.Bold, color = DeepCharcoal)
                Text("Your recorded crops will appear here.", color = Color.Gray, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onMintClick,
            modifier = Modifier.fillMaxWidth().height(80.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgritechGreen)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("BLOCKCHAIN ENTRY", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                    Text("MINT NEW HARVEST", fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun DetailsView(cropType: String, onCropChange: (String) -> Unit, weight: String, onWeightChange: (String) -> Unit, date: String, onDateChange: (String) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        StepIndicator(currentStep = 1)
        Text("1. Enter Harvest Details", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, modifier = Modifier.padding(vertical = 16.dp))

        OutlinedTextField(value = cropType, onValueChange = onCropChange, label = { Text("Crop Type") }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))
        OutlinedTextField(value = weight, onValueChange = onWeightChange, label = { Text("Total Weight (kg)") }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))
        OutlinedTextField(value = date, onValueChange = onDateChange, label = { Text("Harvest Date") }, modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp))

        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgritechGreen)) {
            Text("Continue to Location")
        }
    }
}

@Composable
fun LocationView(geohash: String, onBack: () -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        StepIndicator(currentStep = 2)
        Text("2. Verify Your Location", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, modifier = Modifier.padding(vertical = 16.dp))

        // Mock Map Area
        Surface(modifier = Modifier.fillMaxWidth().height(250.dp).padding(bottom = 24.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFFF3F4F6)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.LocationOn, contentDescription = "Pin", tint = AgritechGreen, modifier = Modifier.size(48.dp))
            }
        }

        OutlinedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp), border = BorderStroke(1.dp, AgritechGreen)) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("LOCATION GEOHASH", fontSize = 10.sp, color = AgritechGreen, fontWeight = FontWeight.Bold)
                    Text(geohash, fontSize = 18.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = AgritechGreen)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(56.dp)) { Text("Back", color = DeepCharcoal) }
            Button(onClick = onNext, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgritechGreen)) { Text("Next") }
        }
    }
}

@Composable
fun ProofView(onBack: () -> Unit, onPublish: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        StepIndicator(currentStep = 3)
        Text("3. Capture Digital Proof", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, modifier = Modifier.padding(vertical = 16.dp))

        // Mock Camera Viewfinder
        Surface(modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 32.dp), shape = RoundedCornerShape(16.dp), color = DeepCharcoal) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("TAP TO TAKE PHOTO", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(56.dp)) { Text("Back", color = DeepCharcoal) }
            Button(onClick = onPublish, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgritechGreen)) { Text("Finalize & Publish") }
        }
    }
}

@Composable
fun SuccessView(txHash: String, onHome: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(32.dp))
        Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = AgritechGreen, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Blockchain Minting Successful!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AgritechGreen, textAlign = TextAlign.Center)
        Text("Your harvest record has been permanently secured on the ledger.", color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp, bottom = 32.dp))

        // Mock QR
        Surface(modifier = Modifier.size(200.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color.LightGray)) {
            Icon(Icons.Default.QrCode, contentDescription = "QR", modifier = Modifier.padding(32.dp).fillMaxSize(), tint = DeepCharcoal)
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(value = txHash, onValueChange = {}, readOnly = true, label = { Text("Ethereum Transaction Hash") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = AgritechGreen)) { Text("Share QR & Hash") }
        OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Back to Dashboard", color = DeepCharcoal) }
    }
}

// --- HELPER COMPONENTS ---

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier, colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("DETAILS", color = if (currentStep >= 1) AgritechGreen else Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Divider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = if (currentStep >= 2) AgritechGreen else Color.LightGray)
        Text("LOCATION", color = if (currentStep >= 2) AgritechGreen else Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Divider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = if (currentStep >= 3) AgritechGreen else Color.LightGray)
        Text("PROOF", color = if (currentStep >= 3) AgritechGreen else Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}