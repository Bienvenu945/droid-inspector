package com.droidinspector.data.pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.droidinspector.core.utils.FormatUtils
import com.droidinspector.domain.model.AppPackageInfo
import com.droidinspector.domain.model.BatteryInfo
import com.droidinspector.domain.model.DeviceSummary
import com.droidinspector.domain.model.HardwareInfo
import com.droidinspector.domain.model.ReportSectionsSelection
import com.droidinspector.domain.model.SensorInfo
import com.droidinspector.domain.model.StorageInfo
import com.droidinspector.domain.model.SystemSecurityInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Générateur de rapport PDF natif (A4) pour Droid Inspector X.
 * Signature obligatoire en pied de page : DROID INSPECTOR X — @Bienvenu
 */
@Singleton
class PdfReportGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val pageWidth = 595 // A4 width in points (72 dpi)
    private val pageHeight = 842 // A4 height in points (72 dpi)
    private val margin = 40f

    suspend fun generateToUri(
        uri: Uri,
        selection: ReportSectionsSelection,
        device: DeviceSummary,
        hardware: HardwareInfo,
        security: SystemSecurityInfo,
        battery: BatteryInfo,
        storage: StorageInfo,
        sensors: List<SensorInfo>,
        apps: List<AppPackageInfo>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val document = buildPdfDocument(
                selection, device, hardware, security, battery, storage, sensors, apps
            )
            context.contentResolver.openOutputStream(uri)?.use { out ->
                document.writeTo(out)
            } ?: error("Impossible d'ouvrir le flux de destination")
            document.close()
        }
    }

    suspend fun generateToCacheFile(
        selection: ReportSectionsSelection,
        device: DeviceSummary,
        hardware: HardwareInfo,
        security: SystemSecurityInfo,
        battery: BatteryInfo,
        storage: StorageInfo,
        sensors: List<SensorInfo>,
        apps: List<AppPackageInfo>
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val pdfFile = File(reportsDir, "DroidInspectorX_Report_$timestamp.pdf")
            val document = buildPdfDocument(
                selection, device, hardware, security, battery, storage, sensors, apps
            )
            FileOutputStream(pdfFile).use { out: OutputStream ->
                document.writeTo(out)
            }
            document.close()
            pdfFile
        }
    }

    private fun buildPdfDocument(
        selection: ReportSectionsSelection,
        device: DeviceSummary,
        hardware: HardwareInfo,
        security: SystemSecurityInfo,
        battery: BatteryInfo,
        storage: StorageInfo,
        sensors: List<SensorInfo>,
        apps: List<AppPackageInfo>
    ): PdfDocument {
        val document = PdfDocument()

        val titlePaint = Paint().apply {
            color = Color.rgb(16, 185, 129)
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subtitlePaint = Paint().apply {
            color = Color.rgb(71, 85, 105)
            textSize = 10f
        }
        val sectionPaint = Paint().apply {
            color = Color.rgb(5, 150, 105)
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val labelPaint = Paint().apply {
            color = Color.rgb(51, 65, 85)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val valuePaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 10f
        }
        val dividerPaint = Paint().apply {
            color = Color.rgb(203, 213, 225)
            strokeWidth = 1f
        }
        val footerPaint = Paint().apply {
            color = Color.rgb(5, 150, 105)
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        var y = 48f

        fun drawFooter() {
            canvas.drawLine(margin, pageHeight - 42f, pageWidth - margin, pageHeight - 42f, dividerPaint)
            canvas.drawText("DROID INSPECTOR X — @Bienvenu", margin, pageHeight - 25f, footerPaint)
            canvas.drawText("Page $pageNumber", pageWidth - margin - 40f, pageHeight - 25f, subtitlePaint)
        }

        fun ensureSpace(needed: Float) {
            if (y + needed > pageHeight - 60f) {
                drawFooter()
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 48f
            }
        }

        fun drawSectionTitle(title: String) {
            ensureSpace(32f)
            y += 10f
            canvas.drawText(title, margin, y, sectionPaint)
            y += 6f
            canvas.drawLine(margin, y, pageWidth - margin, y, dividerPaint)
            y += 14f
        }

        fun drawRow(label: String, value: String) {
            ensureSpace(16f)
            canvas.drawText(label, margin, y, labelPaint)
            val truncated = if (value.length > 62) value.take(59) + "..." else value
            canvas.drawText(truncated, margin + 170f, y, valuePaint)
            y += 15f
        }

        // Header
        canvas.drawText("DROID INSPECTOR X", margin, y, titlePaint)
        y += 16f
        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        canvas.drawText("Rapport de Diagnostic Système Android · Généré le $dateStr", margin, y, subtitlePaint)
        y += 12f
        canvas.drawLine(margin, y, pageWidth - margin, y, dividerPaint)
        y += 16f

        if (selection.includeDevice) {
            drawSectionTitle("1. IDENTIFICATION DU TERMINAL")
            drawRow("Appareil", device.deviceName)
            drawRow("Constructeur / Marque", "${device.manufacturer} (${device.brand})")
            drawRow("Modèle / Produit", "${device.model} / ${device.product}")
            drawRow("Carte mère / Matériel", "${device.board} / ${device.hardware}")
        }

        if (selection.includeSystem) {
            drawSectionTitle("2. INFORMATIONS SYSTÈME & SÉCURITÉ")
            drawRow("Version Android", "Android ${device.androidVersion} (API ${device.apiLevel})")
            drawRow("Correctif de sécurité", device.securityPatch)
            drawRow("Numéro de Build", device.buildNumber)
            drawRow("Noyau Linux", device.kernelVersion)
            drawRow("Chiffrement stockage", security.encryptionStatus)
            drawRow("SELinux / Root", "${security.seLinuxMode} · Root: ${if (security.rootDetected) "Détecté" else "Non"}")
        }

        if (selection.includeHardware) {
            drawSectionTitle("3. MATÉRIEL, CPU & AFFICHAGE")
            drawRow("Architecture CPU", hardware.cpuArchitecture)
            drawRow("Cœurs & ABIs", "${hardware.coreCount} cœurs (${hardware.abiList.joinToString(", ")})")
            drawRow(
                "Mémoire RAM",
                "${FormatUtils.formatBytes(hardware.ramTotalBytes - hardware.ramAvailableBytes)} / ${FormatUtils.formatBytes(hardware.ramTotalBytes)}"
            )
            drawRow("Écran", "${hardware.screenResolution} @ ${hardware.screenDensityDpi} dpi (${hardware.screenRefreshRateHz.toInt()} Hz)")
            drawRow("Graphismes", "${hardware.glEsVersion} · Vulkan: ${if (hardware.vulkanSupported) "Oui" else "Non"}")
        }

        if (selection.includeBattery) {
            drawSectionTitle("4. BATTERIE & ÉNERGIE")
            drawRow("Niveau de charge", "${battery.percentage}% (${battery.statusLabel})")
            drawRow("Source d'alimentation", battery.chargingSource)
            drawRow("Santé & Technologie", "${battery.health} (${battery.technology})")
            drawRow("Température / Tension", "${battery.temperatureCelsius} °C · ${battery.voltageMilliVolts} mV")
        }

        if (selection.includeStorage) {
            drawSectionTitle("5. ANALYSE DU STOCKAGE INTERNE")
            drawRow("Espace total", FormatUtils.formatBytes(storage.totalBytes))
            drawRow("Espace utilisé", FormatUtils.formatBytes(storage.usedBytes))
            drawRow("Espace disponible", FormatUtils.formatBytes(storage.availableBytes))
            drawRow("Partition", "${storage.dataPath} (Bloc: ${storage.blockSizeBytes} o)")
        }

        if (selection.includeSensors) {
            drawSectionTitle("6. CAPTEURS MATÉRIELS DÉTECTÉS (${sensors.size})")
            sensors.take(25).forEach { sensor ->
                drawRow(sensor.typeString, "${sensor.name} (${sensor.vendor})")
            }
            if (sensors.size > 25) {
                drawRow("...", "Et ${sensors.size - 25} autres capteurs détectés")
            }
        }

        if (selection.includeApps) {
            val userApps = apps.filter { !it.isSystemApp }
            drawSectionTitle("7. APPLICATIONS ACCESSIBLES (${apps.size} total · ${userApps.size} utilisateur)")
            userApps.take(30).forEach { app ->
                drawRow(app.appName, "${app.packageName} (v${app.versionName})")
            }
            if (userApps.size > 30) {
                drawRow("...", "Et ${userApps.size - 30} autres applications utilisateur")
            }
        }

        drawFooter()
        document.finishPage(page)
        return document
    }
}
