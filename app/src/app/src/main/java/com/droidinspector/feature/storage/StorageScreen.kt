package com.droidinspector.feature.storage

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
fun StorageScreen(
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
                text = "Storage Analyzer",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Circular Storage Gauge
        GlassCard(contentPadding = 24.dp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MetricGauge(
                    value = 62.4f,
                    size = 140.dp,
                    strokeWidth = 12.dp,
                    label = "Occupé"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "159.7 Go utilisés sur 256.0 Go (96.3 Go libres)",
                    fontSize = 13.sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionHeader("Répartition par Catégorie", "Analyse de l'espace de stockage interne")

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StorageCategoryRow("Applications & Données", "64.8 Go", "40.5%")
                StorageCategoryRow("Photos & Vidéos", "48.2 Go", "30.1%")
                StorageCategoryRow("Musique & Audio", "12.5 Go", "7.8%")
                StorageCategoryRow("Système Android 15", "18.2 Go", "11.4%")
                StorageCategoryRow("Fichiers Téléchargés", "9.6 Go", "6.0%")
                StorageCategoryRow("Fichiers Cache Temporaires", "6.4 Go", "4.0%")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionHeader("Système de Fichiers & Partitions", "Points de montage accessibles via StatFs")

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StorageCategoryRow("Partition /data", "238.4 Go (ext4 / f2fs)", "Primaire")
                StorageCategoryRow("RAM Swap / zram0", "8.0 Go (Compressé)", "Actif")
                StorageCategoryRow("Stockage Externe (MicroSD)", "Non inséré", "N/A")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StorageCategoryRow(label: String, size: String, percentage: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(text = percentage, color = TextMutedDark, fontSize = 11.sp)
        }
        Text(text = size, color = EmeraldPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
