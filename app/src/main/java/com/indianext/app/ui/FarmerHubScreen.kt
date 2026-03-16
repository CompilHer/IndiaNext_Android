package com.indianext.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.indianext.app.ui.theme.AgritechGreen
import com.indianext.app.ui.theme.BackgroundWhite
import com.indianext.app.ui.theme.DeepCharcoal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.location.Geocoder
import java.util.Locale

enum class MintStep { DASHBOARD, DETAILS, LOCATION, PROOF, SUCCESS }

@Composable
fun FarmerHubScreen() {
    var currentStep by remember { mutableStateOf(MintStep.DASHBOARD) }

    val farmerName by remember { mutableStateOf("Kisan") }
    val seasonTotal by remember { mutableStateOf("0.00 kg") }

    var cropType by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var harvestDate by remember { mutableStateOf("") }

    var currentGeohash by remember { mutableStateOf("Fetching...") }
    var currentLatLon by remember { mutableStateOf("Locating Satellites...") }
    var currentAddress by remember { mutableStateOf("Determining Area...") }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var generatedTxHash by remember { mutableStateOf("") }

    val lightGreenBg = Color(0xFFF0FDF4)

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundWhite)
    ) {
        // --- GLOBAL TOP BAR ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, contentDescription = "Logo", tint = DeepCharcoal)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Farm Dashboard", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
            }
            Surface(
                shape = CircleShape, color = lightGreenBg, border = BorderStroke(1.dp, AgritechGreen.copy(alpha = 0.3f))
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

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

        Crossfade(targetState = currentStep, modifier = Modifier.weight(1f)) { step ->
            when (step) {
                MintStep.DASHBOARD -> DashboardView(
                    farmerName = farmerName, seasonTotal = seasonTotal,
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
                    latLon = currentLatLon,
                    address = currentAddress,
                    onLocationFetched = { hash, coords, addr ->
                        currentGeohash = hash
                        currentLatLon = coords
                        currentAddress = addr
                    },
                    onBack = { currentStep = MintStep.DETAILS },
                    onNext = { currentStep = MintStep.PROOF }
                )
                MintStep.PROOF -> ProofView(
                    capturedImage = capturedImage,
                    onImageCaptured = { bitmap -> capturedImage = bitmap },
                    onBack = { currentStep = MintStep.LOCATION },
                    onPublish = {
                        generatedTxHash = "0xabc123...def456"
                        currentStep = MintStep.SUCCESS
                    }
                )
                MintStep.SUCCESS -> SuccessView(
                    txHash = generatedTxHash,
                    onHome = {
                        cropType = ""; weight = ""; harvestDate = ""; capturedImage = null
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
fun LocationView(
    geohash: String,
    latLon: String,
    address: String,
    onLocationFetched: (String, String, String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope() // Needed for background geocoding

    // Hardware GPS Permission Request
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val coords = "Lat: ${String.format(Locale.getDefault(), "%.4f", location.latitude)} N\nLon: ${String.format(Locale.getDefault(), "%.4f", location.longitude)} E"
                        val mockHash = "geo" + location.latitude.toString().replace(".", "").take(4) + "x"

                        // REVERSE GEOCODING (Lat/Lon -> City Name) on a Background Thread
                        coroutineScope.launch(Dispatchers.IO) {
                            val addressName = try {
                                val geocoder = Geocoder(context, Locale.getDefault())
                                // Using the older getFromLocation for broader Android compatibility
                                @Suppress("DEPRECATION")
                                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                if (!addresses.isNullOrEmpty()) {
                                    // Grabs the city or locality (e.g., "Panvel")
                                    addresses[0].locality ?: addresses[0].subLocality ?: "Unknown Area"
                                } else "Unknown Area"
                            } catch (e: Exception) { "Location Lookup Error" }

                            // Send all 3 pieces of data back up to the screen state
                            onLocationFetched(mockHash, coords, addressName)
                        }
                    } else {
                        onLocationFetched("Unavailable", "Ensure GPS is ON", "No Signal")
                    }
                }
            } catch (e: SecurityException) {
                onLocationFetched("Error", "Permission Denied", "Restricted")
            }
        } else {
            onLocationFetched("Denied", "Permission Denied", "Restricted")
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        StepIndicator(currentStep = 2)
        Text("2. Verify Your Location", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, modifier = Modifier.padding(vertical = 16.dp))

        Surface(modifier = Modifier.fillMaxWidth().height(250.dp).padding(bottom = 16.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFFF3F4F6)) {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Pin", tint = AgritechGreen, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(latLon, fontSize = 12.sp, color = DeepCharcoal, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                }
            }
        }

        OutlinedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp), border = BorderStroke(1.dp, AgritechGreen)) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("LOCATION SECURED", fontSize = 10.sp, color = AgritechGreen, fontWeight = FontWeight.Bold)
                    Text(address, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal) // E.g., "Panvel"
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Hash: $geohash", fontSize = 12.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = Color.Gray)
                }
                Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = AgritechGreen, modifier = Modifier.size(32.dp))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(56.dp)) { Text("Back", color = DeepCharcoal) }
            Button(onClick = onNext, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = AgritechGreen)) { Text("Next") }
        }
    }
}

@Composable
fun ProofView(capturedImage: Bitmap?, onImageCaptured: (Bitmap?) -> Unit, onBack: () -> Unit, onPublish: () -> Unit) {
    val context = LocalContext.current

    // 1. The launcher that actually opens the camera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            onImageCaptured(bitmap)
        } else {
            Toast.makeText(context, "Photo capture cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. The launcher that asks for Camera Permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted! Open the camera.
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission is required to capture proof.", Toast.LENGTH_LONG).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        StepIndicator(currentStep = 3)
        Text("3. Capture Digital Proof", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal, modifier = Modifier.padding(vertical = 16.dp))

        // Active Viewfinder Area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 32.dp)
                .clickable {
                    // 3. Check if we already have permission before launching
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(null)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
            shape = RoundedCornerShape(16.dp),
            color = DeepCharcoal
        ) {
            if (capturedImage != null) {
                Image(
                    bitmap = capturedImage.asImageBitmap(),
                    contentDescription = "Captured Harvest",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                )
            } else {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("TAP TO TAKE PHOTO", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("OR SCAN CERTIFICATE", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(56.dp)) { Text("Back", color = DeepCharcoal) }
            Button(
                onClick = onPublish,
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgritechGreen),
                enabled = capturedImage != null // Prevent publishing without proof!
            ) { Text("Finalize & Publish") }
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
        HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = if (currentStep >= 2) AgritechGreen else Color.LightGray)
        Text("LOCATION", color = if (currentStep >= 2) AgritechGreen else Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = if (currentStep >= 3) AgritechGreen else Color.LightGray)
        Text("PROOF", color = if (currentStep >= 3) AgritechGreen else Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}