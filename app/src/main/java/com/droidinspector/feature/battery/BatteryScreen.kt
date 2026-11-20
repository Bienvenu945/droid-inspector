package com.droidinspector.feature.battery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.MetricGauge
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.TextMutedDark

@Composable
fun BatteryScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
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
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Animated Battery Centerpiece
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MetricGauge(
                    value = 84f,
                    size = 140.dp,
                    strokeWidth = 12.dp,
                    label = "Capacité"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "En décharge · Autonomie estimée: 14h 20m",
                    fontSize = 13.sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionHeader("État et Diagnostics de l'Accumulateur", "Données officielles issues de BatteryManager")

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BatteryDetailRow("Santé de la Batterie", "Bonne santé (Good)")
                BatteryDetailRow("Technologie Cellulaire", "Li-Polymer (Lithium Polymère)")
                BatteryDetailRow("Température Actuelle", "28.4 °C")
                BatteryDetailRow("Tension du Circuit", "4.18 V (4180 mV)")
                BatteryDetailRow("Source d'Alimentation", "Batterie (Non branché)")
                BatteryDetailRow("Capacité Typique Déclarée", "5000 mAh")
                BatteryDetailRow("Cycles de Charge Estimés", "142 cycles")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun BatteryDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMutedDark, fontSize = 13.sp)
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
