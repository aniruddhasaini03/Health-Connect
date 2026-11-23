package com.healthconnect.app.data.repository

import com.healthconnect.app.data.model.Hospital
import com.healthconnect.app.data.remote.RetrofitInstance
import kotlin.math.*

class HospitalRepository {
    suspend fun getNearbyHospitals(latitude: Double, longitude: Double, radiusMeters: Int = 10_000): Result<List<Hospital>> = runCatching {
        val query = """
            [out:json][timeout:25];
            (
              node["amenity"="hospital"](around:$radiusMeters,$latitude,$longitude);
              node["amenity"="clinic"](around:$radiusMeters,$latitude,$longitude);
            );
            out body;
        """.trimIndent()
        RetrofitInstance.hospitalService.findNearbyHospitals(query).elements.mapNotNull { e ->
            val lat = e.lat ?: return@mapNotNull null
            val lon = e.lon ?: return@mapNotNull null
            val tags = e.tags
            val name = tags["name"] ?: tags["name:en"] ?: "Healthcare Facility"
            val address = tags["addr:full"] ?: listOfNotNull(tags["addr:street"], tags["addr:city"]).joinToString(", ").ifBlank { "Nearby Location" }
            val phone = tags["phone"] ?: tags["contact:phone"] ?: "Not provided"
            Hospital(
                id = e.id.toString(), name = name, address = address,
                latitude = lat, longitude = lon, phoneNumber = phone,
                isOpenNow = tags["opening_hours"]?.contains("24/7") == true,
                distanceKm = haversineKm(latitude, longitude, lat, lon)
            )
        }.sortedBy { it.distanceKm }
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat/2).pow(2) + cos(Math.toRadians(lat1))*cos(Math.toRadians(lat2))*sin(dLon/2).pow(2)
        return 2*r*asin(sqrt(a))
    }
}
