package com.droidinspector.feature.hardware

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.InfoRow
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.utils.FormatUtils

/**
 * Module 02 : Hardware Explorer.
 * Signature : @Bienvenu
 */
@Composable
fun HardwareScreen(
    viewModel: HardwareViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Retour", tint = EmeraldPrimary)
            }
            Text(
                text = "Hardware Explorer",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        val hw = state.hardwareInfo
        if (state.isLoading || hw == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = EmeraldPrimary)
            }
            return@Column
        }

        SectionHeader("Identification Matérielle", "Constructeur, modèle et nom du produit")
        GlassCard {
            Column {
                InfoRow("Fabricant", hw.manufacturer)
                InfoRow("Modèle", hw.model, monospaceValue = true)
                InfoRow("Nom du Produit", hw.product, monospaceValue = true, showDivider = false)
            }
        }

        SectionHeader("Processeur (CPU)", "Architecture, cœurs et fréquences accessibles")
        GlassCard {
            Column {
                InfoRow("Architecture CPU", hw.cpuArchitecture, monospaceValue = true)
                InfoRow("ABIs Supportées", hw.abiList.joinToString(", "), monospaceValue = true)
                InfoRow("Nombre de Cœurs", "${hw.coreCount} cœurs", valueColor = EmeraldPrimary, monospaceValue = true)

                hw.coreFrequencies.forEachIndexed { idx, core ->
                    val currentStr = core.currentKhz?.let { FormatUtils.formatFrequencyKhz(it) } ?: "Restreint OS"
                    val rangeStr = if (core.minKhz != null && core.maxKhz != null) {
                        " (${FormatUtils.formatFrequencyKhz(core.minKhz)} - ${FormatUtils.formatFrequencyKhz(core.maxKhz)})"
                    } else ""
                    InfoRow(
                        label = "Cœur #${core.coreIndex}",
                        value = "$currentStr$rangeStr",
                        monospaceValue = true,
                        showDivider = idx < hw.coreFrequencies.lastIndex
                    )
                }
            }
        }

        SectionHeader("Mémoire Vive (RAM)", "Capacité totale et disponibilité temps réel")
        GlassCard {
            val usedRam = (hw.ramTotalBytes - hw.ramAvailableBytes).coerceAtLeast(0L)
            Column {
                InfoRow("RAM Totale", FormatUtils.formatBytes(hw.ramTotalBytes), monospaceValue = true)
                InfoRow("RAM Utilisée", FormatUtils.formatBytes(usedRam), valueColor = EmeraldPrimary, monospaceValue = true)
                InfoRow("RAM Disponible", FormatUtils.formatBytes(hw.ramAvailableBytes), monospaceValue = true)
                InfoRow("Seuil Mémoire Faible", FormatUtils.formatBytes(hw.ramThresholdBytes), monospaceValue = true)
                InfoRow("État Mémoire Critique", if (hw.isLowMemory) "Oui" else "Normal", showDivider = false)
            }
        }

        SectionHeader("Écran & Graphismes GPU", "Résolution, densité, rafraîchissement et APIs 3D")
        GlassCard {
            Column {
                InfoRow("Résolution Écran", hw.screenResolution, monospaceValue = true)
                InfoRow("Densité de Pixels", "${hw.screenDensityDpi} dpi (${hw.screenDensityBucket})", monospaceValue = true)
                InfoRow("Rafraîchissement", "${hw.screenRefreshRateHz.toInt()} Hz", valueColor = EmeraldPrimary, monospaceValue = true)
                InfoRow("Support HDR", if (hw.hdrSupported) "Pris en charge" else "Standard SDR")
                InfoRow("Version OpenGL ES", hw.glEsVersion, monospaceValue = true)
                InfoRow("Support Vulkan", if (hw.vulkanSupported) "Disponible (Matériel)" else "Non détecté", showDivider = false)
            }
        }

        if (hw.hardwareFeatures.isNotEmpty()) {
            SectionHeader("Caractéristiques Matérielles", "Modules détectés via PackageManager")
            GlassCard {
                Column {
                    hw.hardwareFeatures.forEachIndexed { idx, feature ->
                        InfoRow(
                            label = feature,
                            value = "Disponible",
                            valueColor = EmeraldPrimary,
                            showDivider = idx < hw.hardwareFeatures.lastIndex
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
