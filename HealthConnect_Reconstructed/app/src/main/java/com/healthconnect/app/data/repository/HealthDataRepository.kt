package com.healthconnect.app.data.repository

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.healthconnect.app.data.model.HealthConnectAvailability
import com.healthconnect.app.data.model.HealthMetricValue
import com.healthconnect.app.data.model.MetricType
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

class HealthDataRepository(private val context: Context) {
    companion object {
        val REQUIRED_PERMISSIONS = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(HydrationRecord::class)
        )
    }

    private fun client(): HealthConnectClient = HealthConnectClient.getOrCreate(context)
    fun isHealthConnectAvailable(): Boolean = HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
    fun getPermissionRequestContract() = androidx.health.connect.client.PermissionController.createRequestPermissionResultContract()

    suspend fun hasAllPermissions(): Boolean = isHealthConnectAvailable() &&
        client().permissionController.getGrantedPermissions().containsAll(REQUIRED_PERMISSIONS)

    suspend fun checkAvailability() = HealthConnectAvailability(isHealthConnectAvailable(), hasAllPermissions())

    suspend fun getTodayMetrics(): Result<List<HealthMetricValue>> = runCatching {
        if (!isHealthConnectAvailable()) error("Health Connect is not available on this device.")
        if (!hasAllPermissions()) error("Health data permissions have not been granted yet.")

        val zone = ZoneId.systemDefault()
        val start = java.time.LocalDate.now(zone).atStartOfDay(zone).toInstant()
        val end = Instant.now()
        val range = TimeRangeFilter.between(start, end)
        val now = System.currentTimeMillis()

        val steps = client().aggregate(AggregateRequest(setOf(StepsRecord.COUNT_TOTAL), range))[StepsRecord.COUNT_TOTAL]?.toDouble()

        val hrRecords = client().readRecords(ReadRecordsRequest(HeartRateRecord::class, range)).records
        val heartSamples = hrRecords.flatMap { it.samples }.map { it.beatsPerMinute.toDouble() }
        val avgHeartRate = heartSamples.takeIf { it.isNotEmpty() }?.average()

        val sleep = client().readRecords(ReadRecordsRequest(SleepSessionRecord::class, range)).records
            .sumOf { Duration.between(it.startTime, it.endTime).toMinutes() }.toDouble() / 60.0

        val water = client().aggregate(AggregateRequest(setOf(HydrationRecord.VOLUME_TOTAL), range))[HydrationRecord.VOLUME_TOTAL]?.inLiters

        listOf(
            HealthMetricValue(MetricType.STEPS, steps, "steps", true, now),
            HealthMetricValue(MetricType.HEART_RATE, avgHeartRate, "bpm", true, now),
            HealthMetricValue(MetricType.SLEEP, sleep, "hrs", true, now),
            HealthMetricValue(MetricType.WATER, water, "L", true, now)
        )
    }
}
