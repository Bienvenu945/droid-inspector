package com.droidinspector

import com.droidinspector.core.utils.FormatUtils
import com.droidinspector.domain.model.ReportSectionsSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Droid Inspector X (Phases 1 to 9).
 * Author: @Bienvenu
 */
class DroidInspectorFoundationTest {

    @Test
    fun testAppMetadataAndSignature() {
        val appName = "DROID INSPECTOR X"
        val authorSignature = "@Bienvenu"

        assertEquals("DROID INSPECTOR X", appName)
        assertEquals("@Bienvenu", authorSignature)
    }

    @Test
    fun testBatteryPercentageCalculation() {
        val level = 42
        val scale = 100
        val percentage = (level * 100) / scale
        assertEquals(42, percentage)
        assertTrue(percentage in 0..100)
    }

    @Test
    fun testFormatBytes() {
        assertEquals("0 o", FormatUtils.formatBytes(0L))
        assertEquals("1.0 Ko", FormatUtils.formatBytes(1024L))
        assertEquals("1.0 Mo", FormatUtils.formatBytes(1024L * 1024L))
        assertEquals("2.00 Go", FormatUtils.formatBytes(2L * 1024L * 1024L * 1024L))
    }

    @Test
    fun testFormatFrequency() {
        assertEquals("N/D", FormatUtils.formatFrequencyKhz(0L))
        assertEquals("800 MHz", FormatUtils.formatFrequencyKhz(800_000L))
        assertEquals("2.40 GHz", FormatUtils.formatFrequencyKhz(2_400_000L))
    }

    @Test
    fun testDensityBucket() {
        assertEquals("mdpi", FormatUtils.getDensityBucket(160))
        assertEquals("xxhdpi", FormatUtils.getDensityBucket(480))
        assertEquals("xxxhdpi", FormatUtils.getDensityBucket(560))
    }

    @Test
    fun testDefaultReportSelectionIncludesAllModules() {
        val selection = ReportSectionsSelection()
        assertTrue(selection.includeDevice)
        assertTrue(selection.includeHardware)
        assertTrue(selection.includeSystem)
        assertTrue(selection.includeBattery)
        assertTrue(selection.includeStorage)
        assertTrue(selection.includeSensors)
        assertTrue(selection.includeApps)
    }
}
