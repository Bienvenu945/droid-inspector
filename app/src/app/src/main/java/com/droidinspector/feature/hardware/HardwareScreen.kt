package com.droidinspector.feature.hardware

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Memory
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
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.TextMutedDark

@Composable
fun HardwareScreen(
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
                text = "Hardware Explorer",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("Processeur (SoC & CPU)", "Cœurs, fréquences et jeux d'instructions")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HardwareItemRow("Architecture", "ARM64-v8a (64-bit)")
                HardwareItemRow("Cœurs Actifs", "8 cœurs (1x Cortex-X4 + 5x Cortex-A720 + 2x Cortex-A520)")
                HardwareItemRow("Fréquence Maximale", "3.30 GHz")
                HardwareItemRow("Fréquence Minimale", "680 MHz")
                HardwareItemRow("Gouverneur CPU", "schedutil")
                HardwareItemRow("Jeu d'Instructions ABIs", "arm64-v8a, armeabi-v7a, armeabi")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("Graphismes & Rendu GPU", "Pilote OpenGL ES et accélérateur")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HardwareItemRow("Vendeur GPU", "Qualcomm Technologies / ARM")
                HardwareItemRow("Processeur Graphique", "Adreno 750 / Immortalis-G720")
                HardwareItemRow("Version OpenGL ES", "OpenGL ES 3.2 v1.r38p0")
                HardwareItemRow("Support Vulkan API", "Version 1.3 (Hardware Accelerated)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("Affichage & Dalle Tactile", "Résolution physique, densité et taux")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HardwareItemRow("Résolution Réelle", "1440 x 3120 pixels")
                HardwareItemRow("Densité de Pixels", "512 dpi (xxxhdpi)")
                HardwareItemRow("Fréquence de Rafraîchissement", "120.0 Hz (LTPO Dynamique)")
                HardwareItemRow("Prise en charge HDR", "HDR10+, Dolby Vision")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun HardwareItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMutedDark, fontSize = 13.sp)
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
