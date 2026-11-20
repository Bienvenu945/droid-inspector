package com.droidinspector.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0L) return "0 o"
        val kb = 1024.0
        val mb = kb * 1024.0
        val gb = mb * 1024.0
        return when {
            bytes >= gb -> String.format(Locale.US, "%.2f Go", bytes / gb)
            bytes >= mb -> String.format(Locale.US, "%.1f Mo", bytes / mb)
            bytes >= kb -> String.format(Locale.US, "%.1f Ko", bytes / kb)
            else -> "$bytes o"
        }
    }

    fun formatGigabytesShort(bytes: Long): String {
        if (bytes <= 0L) return "0.0 Go"
        val gb = bytes.toDouble() / (1024.0 * 1024.0 * 1024.0)
        return String.format(Locale.US, "%.1f Go", gb)
    }

    fun formatFrequencyKhz(khz: Long): String {
        return when {
            khz <= 0L -> "N/D"
            khz >= 1_000_000L -> String.format(Locale.US, "%.2f GHz", khz / 1_000_000.0)
            else -> "${khz / 1000L} MHz"
        }
    }

    fun formatDate(timestampMillis: Long): String {
        if (timestampMillis <= 0L) return "N/D"
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formatter.format(Date(timestampMillis))
    }

    fun getDensityBucket(dpi: Int): String {
        return when {
            dpi <= 120 -> "ldpi"
            dpi <= 160 -> "mdpi"
            dpi <= 240 -> "hdpi"
            dpi <= 320 -> "xhdpi"
            dpi <= 480 -> "xxhdpi"
            else -> "xxxhdpi"
        }
    }
}
