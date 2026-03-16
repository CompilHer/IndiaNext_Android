package com.indianext.app.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // 1. Farmer API
    @POST("api/farmer/harvest/mint")
    suspend fun mintHarvest(@Body request: MintRequest): Response<MintResponse>

    // 2. Logistics API
    @GET("api/logistics/transit/{batchId}")
    suspend fun getTransitState(@Path("batchId") batchId: String): Response<TransitResponse>

    // 3. Retailer API
    @GET("api/retailer/dashboard/{retailerId}")
    suspend fun getRetailerDashboard(@Path("retailerId") retailerId: String): Response<RetailerDashboardResponse>

    // 4. Verification API
    @GET("api/verify/{hash}")
    suspend fun verifyHash(@Path("hash") hash: String): Response<VerificationResponse>
}