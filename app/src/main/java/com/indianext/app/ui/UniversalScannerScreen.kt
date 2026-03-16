package com.indianext.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.indianext.app.ui.theme.AgritechGreen

@Composable
fun UniversalScannerScreen(
    isFlashlightOn: Boolean = false,
    isBatchMode: Boolean = false,
    onToggleFlashlight: () -> Unit = {},
    onToggleBatchMode: () -> Unit = {},
    onManualEntry: () -> Unit = {},
    onScanSuccess: (String) -> Unit = {}
) {
    val vibrantGreen = Color(0xFF10B981)
    val darkOverlay = Color.Black.copy(alpha = 0.7f)
    val panelColor = Color(0xFF2C2C2C).copy(alpha = 0.9f)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // --- State & Permissions ---
    var hasCamPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        hasCamPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCamPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Scanner Laser Animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "laser_anim"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ==========================================================
        // 1. LIVE CAMERA PREVIEW LAYER (CameraX + ML Kit)
        // ==========================================================
        if (hasCamPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        // Set up the viewfinder
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        // Set up the ML Kit Analyzer
                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(
                                    ContextCompat.getMainExecutor(ctx),
                                    BarcodeAnalyzer { barcodeValue ->
                                        // A barcode was found! Pass it to the callback.
                                        Log.d("Scanner", "Scanned: $barcodeValue")
                                        onScanSuccess(barcodeValue)
                                    }
                                )
                            }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalyzer
                            )
                        } catch (exc: Exception) {
                            Log.e("Scanner", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )
        }

        // ==========================================================
        // 2. SCANNER OVERLAY & CUTOUT LAYER
        // ==========================================================
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val rectSize = canvasWidth * 0.7f
            val rectTopLeftX = (canvasWidth - rectSize) / 2
            val rectTopLeftY = (canvasHeight - rectSize) / 2.5f

            val path = Path().apply {
                addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                addRoundRect(RoundRect(
                    left = rectTopLeftX, top = rectTopLeftY, right = rectTopLeftX + rectSize, bottom = rectTopLeftY + rectSize,
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                ))
                fillType = PathFillType.EvenOdd
            }
            drawPath(path, color = darkOverlay)

            val cornerLength = 40.dp.toPx()
            val strokeWidth = 4.dp.toPx()

            // Corners
            drawLine(vibrantGreen, Offset(rectTopLeftX, rectTopLeftY), Offset(rectTopLeftX + cornerLength, rectTopLeftY), strokeWidth)
            drawLine(vibrantGreen, Offset(rectTopLeftX, rectTopLeftY), Offset(rectTopLeftX, rectTopLeftY + cornerLength), strokeWidth)
            drawLine(vibrantGreen, Offset(rectTopLeftX + rectSize, rectTopLeftY), Offset(rectTopLeftX + rectSize - cornerLength, rectTopLeftY), strokeWidth)
            drawLine(vibrantGreen, Offset(rectTopLeftX + rectSize, rectTopLeftY), Offset(rectTopLeftX + rectSize, rectTopLeftY + cornerLength), strokeWidth)
            drawLine(vibrantGreen, Offset(rectTopLeftX, rectTopLeftY + rectSize), Offset(rectTopLeftX + cornerLength, rectTopLeftY + rectSize), strokeWidth)
            drawLine(vibrantGreen, Offset(rectTopLeftX, rectTopLeftY + rectSize), Offset(rectTopLeftX, rectTopLeftY + rectSize - cornerLength), strokeWidth)
            drawLine(vibrantGreen, Offset(rectTopLeftX + rectSize, rectTopLeftY + rectSize), Offset(rectTopLeftX + rectSize - cornerLength, rectTopLeftY + rectSize), strokeWidth)
            drawLine(vibrantGreen, Offset(rectTopLeftX + rectSize, rectTopLeftY + rectSize), Offset(rectTopLeftX + rectSize, rectTopLeftY + rectSize - cornerLength), strokeWidth)

            // Animated Laser
            val currentLaserY = rectTopLeftY + (rectSize * laserPosition)
            drawLine(
                color = vibrantGreen.copy(alpha = 0.8f),
                start = Offset(rectTopLeftX + 10.dp.toPx(), currentLaserY),
                end = Offset(rectTopLeftX + rectSize - 10.dp.toPx(), currentLaserY),
                strokeWidth = 2.dp.toPx()
            )
        }

        // ==========================================================
        // 3. UI CONTROLS LAYER (Identical to before)
        // ==========================================================
        Column(modifier = Modifier.fillMaxSize().padding(top = 48.dp, bottom = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Bolt, contentDescription = "Logo", tint = vibrantGreen, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(24.dp), color = panelColor, modifier = Modifier.padding(horizontal = 32.dp)) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = vibrantGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Align QR code within frame", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Box(modifier = Modifier.fillMaxWidth().padding(end = 24.dp, top = 24.dp), contentAlignment = Alignment.CenterEnd) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(onClick = { }, modifier = Modifier.background(panelColor, CircleShape).size(40.dp)) {
                        Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { }, modifier = Modifier.background(panelColor, CircleShape).size(40.dp)) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text("Scanning for GS1-Digital Link or Standard\nQR", color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 32.dp))

            Surface(shape = RoundedCornerShape(24.dp), color = panelColor, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(shape = CircleShape, color = if (isFlashlightOn) vibrantGreen else Color.White.copy(alpha = 0.1f), modifier = Modifier.size(56.dp).clickable { onToggleFlashlight() }) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.FlashlightOn, contentDescription = "Flashlight", tint = Color.White)
                        }
                    }
                    OutlinedButton(onClick = onManualEntry, modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))) {
                        Icon(Icons.Default.Keyboard, contentDescription = null, tint = vibrantGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Manual Entry", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(16.dp), color = Color.Black.copy(alpha = 0.5f), modifier = Modifier.clickable { onToggleBatchMode() }) {
                Text(text = if (isBatchMode) "Batch Mode: ON" else "Batch Mode: Off", color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}