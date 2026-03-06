package com.example.store.utils

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import java.util.Locale

/** Утилиты геолокации. Дата: 06.03.2026, Автор: Бубнов Никита */
object LocationUtils {

    fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun tryGetAddress(context: Context): String? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: return null

        return try {
            val geo = Geocoder(context, Locale.getDefault())
            val list = geo.getFromLocation(loc.latitude, loc.longitude, 1)
            val a = list?.firstOrNull() ?: return null
            listOfNotNull(a.getAddressLine(0)).firstOrNull()
        } catch (_: Exception) {
            null
        }
    }
}