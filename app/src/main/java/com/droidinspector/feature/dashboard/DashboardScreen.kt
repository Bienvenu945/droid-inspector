package com.droidinspector.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.InfoRow
import com.droidinspector.core.components.MetricGauge
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.navigation.Screen
import com.droidinspector.core.theme.EmeraldDark
import com.droidinspector.core.theme.EmeraldLight
import com.droidinspector.core.theme.EmeraldPrimary

/**
 * Module 01 : Dashboard principal de Droid Inspector X.
 * Signature : @Bienvenu
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
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
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // En-tête officiel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DROID INSPECTOR X",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldPrimary,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Par @Bienvenu · Diagnostics Système",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .clickable { viewModel.refreshNow() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Actualiser",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "LIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = EmeraldLight
                    )
                }

                IconButton(onClick = { navController.navigate(Screen.About.route) }) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "À propos",
                        tint = EmeraldPrimary
                    )
                }
            }
        }

        // Carte Identité Appareil
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                .clickable { navController.navigate(Screen.System.route) }
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldDark.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Smartphone,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (state.isLoaded) state.deviceName else "Analyse en cours...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Android ${state.androidVersion.ifEmpty { "--" }} · API ${state.apiLevel}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Build: ${state.buildNumber.ifEmpty { "--" }}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Jauges Temps Réel (Batterie, RAM, Stockage)
        SectionHeader(
            title = "Métriques en Temps Réel",
            subtitle = "Batterie, RAM et Stockage interne"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Batterie
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigate(Screen.Battery.route) },
                contentPadding = 12.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MetricGauge(
                        value = state.batteryLevel.toFloat(),
                        size = 78.dp,
                        strokeWidth = 7.dp,
                        label = "Batterie"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.batteryStatusText.ifEmpty { "--" },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (state.isCharging) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "${state.batteryTemperature}°C",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // RAM
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigate(Screen.Hardware.route) },
                contentPadding = 12.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MetricGauge(
                        value = state.ramUsagePercentage,
                        size = 78.dp,
                        strokeWidth = 7.dp,
                        label = "RAM"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.ramUsedFormatted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "/ ${state.ramTotalFormatted}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Stockage
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { navController.navigate(Screen.Storage.route) },
                contentPadding = 12.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MetricGauge(
                        value = state.storageUsagePercentage,
                        size = 78.dp,
                        strokeWidth = 7.dp,
                        label = "Stockage"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.storageUsedFormatted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "/ ${state.storageTotalFormatted}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Résumé Processeur & Capteurs
        SectionHeader(
            title = "Processeur & Capteurs",
            subtitle = "Architecture matérielle détectée"
        )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                InfoRow(
                    label = "Architecture CPU",
                    value = state.cpuArchitecture.ifEmpty { "--" },
                    monospaceValue = true
                )
                InfoRow(
                    label = "Cœurs Actifs",
                    value = "${state.cpuCoreCount} cœurs",
                    valueColor = EmeraldPrimary,
                    monospaceValue = true
                )
                InfoRow(
                    label = "Écran",
                    value = "${state.screenResolution.ifEmpty { "--" }} · ${state.screenRefreshRateHz.toInt()} Hz",
                    monospaceValue = true
                )
                InfoRow(
                    label = "Capteurs Matériels",
                    value = "${state.sensorsCount} détectés",
                    valueColor = EmeraldPrimary,
                    monospaceValue = true,
                    showDivider = false
                )
            }
        }

        // Grille des modules
        SectionHeader(
            title = "Modules de Diagnostic",
            subtitle = "Accès complet aux 8 outils spécialisés"
        )

        val modules = listOf(
            Triple(Screen.Hardware, "Hardware Explorer", Icons.Rounded.Memory),
            Triple(Screen.Battery, "Battery Monitor", Icons.Rounded.BatteryChargingFull),
            Triple(Screen.Sensors, "Sensor Lab", Icons.Rounded.Sensors),
            Triple(Screen.Applications, "App Manager", Icons.Rounded.Widgets),
            Triple(Screen.Storage, "Storage Analyzer", Icons.Rounded.Storage),
            Triple(Screen.System, "System Info", Icons.Rounded.Smartphone),
            Triple(Screen.Reports, "Report Generator", Icons.Rounded.Description),
            Triple(Screen.About, "À propos · @Bienvenu", Icons.Rounded.Verified)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            modules.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pair.forEach { (screen, title, icon) ->
                        ModuleShortcutCard(
                            title = title,
                            icon = icon,
                            onClick = { navController.navigate(screen.route) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "DROID INSPECTOR X — @Bienvenu",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModuleShortcutCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.clickable(onClick = onClick),
        contentPadding = 14.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(EmeraldPrimary.copy(alpha = 0.14f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
