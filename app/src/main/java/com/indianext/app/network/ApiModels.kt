package com.indianext.app.network

import com.google.gson.annotations.SerializedName

// --- FARMER API ---
data class MintRequest(
    val farmerId: String,
    val cropType: String,
    val weightKg: Int,
    val harvestDate: String,
    val location: LocationData,
    val proofImageBase64: String
)

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val geohash: String,
    val address: String
)

data class MintResponse(
    val success: Boolean,
    val batchId: String,
    val txHash: String,
    val message: String
)

// --- LOGISTICS API ---
data class TransitResponse(
    val batchId: String,
    val route: String,
    val nodeId: String,
    val isNetworkLive: Boolean,
    val telemetry: TelemetryData,
    val recentEvents: List<EventData>
)

data class TelemetryData(
    val locationName: String,
    val coordinates: String,
    val temperature: String,
    val signalStrength: String,
    val battery: String,
    val batteryLevel: Int,
    val lastSync: String
)

data class EventData(
    val title: String,
    val time: String,
    val status: String
)

// --- RETAILER API ---
data class RetailerDashboardResponse(
    val pulseMetrics: PulseMetrics,
    val incomingShipments: List<IncomingShipment>
)

data class PulseMetrics(
    val throughputIncreasePercentage: Int,
    val chartData: List<Float>
)

data class IncomingShipment(
    val batchId: String,
    val supplierName: String,
    val eta: String,
    val qualityScore: String,
    val statusText: String
)

// --- VERIFICATION API ---
data class VerificationResponse(
    val isValid: Boolean,
    val batchId: String,
    val status: String,
    val aiScore: Int,
    val gradeTitle: String,
    val gradeDesc: String,
    val network: String,
    val origin: String
)