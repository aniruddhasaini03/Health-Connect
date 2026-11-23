package com.healthconnect.app.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController

class HealthConnectManager(private val context: Context) {
    val client: HealthConnectClient? get() = if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) HealthConnectClient.getOrCreate(context) else null
    fun permissionContract() = PermissionController.createRequestPermissionResultContract()
}
