package com.droidinspector.feature.battery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bolt
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.InfoRow
import com.droidinspector.core.components.MetricGauge
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.StatusWarning
import java.util.Locale

/**
 * Module 03 : Battery Monitor en temps réel.
 * Signature : @Bienvenu
 */
@Composable
fun BatteryScreen(
    viewModel: BatteryViewModel,
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
                text = "Battery Monitor",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        val battery = state.batteryInfo
        if (state.isLoading || battery == null) {
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

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MetricGauge(
                    value = battery.percentage.toFloat(),
                    size = 150.dp,
                    strokeWidth = 12.dp,
                    label = "Niveau"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = if (battery.isCharging) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${battery.statusLabel} · ${battery.chargingSource}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (battery.isCharging) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        SectionHeader(
            title = "Paramètres Électro-Chimiques",
            subtitle = "Mesures en direct issues de BatteryManager"
        )

        GlassCard {
            Column {
                InfoRow("Pourcentage", "${battery.percentage}%", valueColor = EmeraldPrimary, monospaceValue = true)
                InfoRow("État de charge", battery.statusLabel)
                InfoRow("Recharge active", if (battery.isCharging) "Oui" else "Non")
                InfoRow("Source d'alimentation", battery.chargingSource)
                InfoRow("État de santé", battery.health, valueColor = EmeraldPrimary)
                InfoRow("Technologie", battery.technology, monospaceValue = true)
                InfoRow(
                    label = "Température",
                    value = String.format(Locale.US, "%.1f °C", battery.temperatureCelsius),
                    valueColor = if (battery.temperatureCelsius > 42f) StatusWarning else MaterialTheme.colorScheme.onSurface,
                    monospaceValue = true
                )
                InfoRow(
                    label = "Tension",
                    value = String.format(Locale.US, "%.2f V (%d mV)", battery.voltageMilliVolts / 1000f, battery.voltageMilliVolts),
                    monospaceValue = true,
                    showDivider = battery.currentMicroAmps != null || battery.chargeCounterMicroAh != null
                )
                battery.currentMicroAmps?.let { microAmps ->
                    InfoRow(
                        label = "Courant instantané",
                        value = "${microAmps / 1000} mA",
                        monospaceValue = true,
                        showDivider = battery.chargeCounterMicroAh != null
                    )
                }
                battery.chargeCounterMicroAh?.let { counter ->
                    InfoRow(
                        label = "Charge stockée",
                        value = "${counter / 1000} mAh",
                        monospaceValue = true,
                        showDivider = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
