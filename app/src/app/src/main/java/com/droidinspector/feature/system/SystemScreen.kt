package com.droidinspector.feature.system

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
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.TextMutedDark

@Composable
fun SystemScreen(
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
                text = "System Information",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("Version & Niveau Système", "Détails de distribution Android et runtime")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SystemInfoRow("Version Android", "Android 15 (Vanilla Ice Cream)")
                SystemInfoRow("Niveau API SDK", "API 35")
                SystemInfoRow("Correctif de Sécurité", "Novembre 2026")
                SystemInfoRow("Version du Noyau Linux", "Linux 6.1.75-android15-ge84a #1 SMP PREEMPT")
                SystemInfoRow("Environnement d'Exécution", "Android Runtime (ART) 2.1.0")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("Identification Constructeur", "Signatures matérielles et build ROM")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SystemInfoRow("Marque Constructeur", "Google / Samsung / OEM")
                SystemInfoRow("Modèle Commercial", "Pixel 9 Pro / Galaxy S24 Ultra")
                SystemInfoRow("Nom de Carte (Board)", "tensor-g4 / kalama")
                SystemInfoRow("Version Bootloader", "g4-boot-241101")
                SystemInfoRow("Numéro de Build", "AP3A.241105.008")
                SystemInfoRow("Empreinte (Fingerprint)", "google/komodo/komodo:15/AP3A:user/release-keys")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("Sécurité & Intégrité", "Normes et cryptographie")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SystemInfoRow("Chiffrement Matériel", "FBE (File-Based Encryption) Activé")
                SystemInfoRow("SELinux", "Enforcing (Strict)")
                SystemInfoRow("Accès Root", "Non détecté (Système d'origine)")
                SystemInfoRow("Google Play Protect", "Certifié conforme")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SystemInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMutedDark, fontSize = 13.sp)
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
