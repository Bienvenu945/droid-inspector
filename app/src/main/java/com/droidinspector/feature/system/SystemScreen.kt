package com.droidinspector.feature.system

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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
import com.droidinspector.core.theme.StatusWarning
import com.droidinspector.core.utils.FormatUtils

/**
 * Module 07 : System Information.
 * Signature : @Bienvenu
 */
@Composable
fun SystemScreen(
    viewModel: SystemViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val s = state.summary
    val sec = state.security

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
                text = "System Information",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        SectionHeader("Système d'Exploitation & Noyau", "Informations officielles Android & Linux")
        GlassCard {
            Column {
                InfoRow("Version Android", "Android ${s.androidVersion}", valueColor = EmeraldPrimary)
                InfoRow("Niveau API", "API ${s.apiLevel}", monospaceValue = true)
                InfoRow("Correctif de Sécurité", s.securityPatch, monospaceValue = true)
                InfoRow("Version du Noyau Linux", s.kernelVersion, monospaceValue = true)
                InfoRow("Runtime Java / ART", "ART ${s.javaVmVersion}", monospaceValue = true)
                InfoRow("Date de compilation", FormatUtils.formatDate(s.buildTimeMillis), monospaceValue = true, showDivider = false)
            }
        }

        SectionHeader("Identification Constructeur & Build", "Propriétés android.os.Build")
        GlassCard {
            Column {
                InfoRow("Fabricant", s.manufacturer)
                InfoRow("Marque", s.brand)
                InfoRow("Modèle", s.model, monospaceValue = true)
                InfoRow("Produit", s.product, monospaceValue = true)
                InfoRow("Carte mère (Board)", s.board, monospaceValue = true)
                InfoRow("Matériel (Hardware)", s.hardware, monospaceValue = true)
                InfoRow("Bootloader", s.bootloader, monospaceValue = true)
                InfoRow("Numéro de Build", s.buildNumber, monospaceValue = true)
                InfoRow("Empreinte (Fingerprint)", s.fingerprint, monospaceValue = true, showDivider = false)
            }
        }

        SectionHeader("Sécurité & Intégrité", "Chiffrement et état de la plateforme")
        GlassCard {
            Column {
                InfoRow("Chiffrement stockage", sec.encryptionStatus, valueColor = EmeraldPrimary)
                InfoRow("SELinux", sec.seLinuxMode)
                InfoRow(
                    label = "Indicateurs Root",
                    value = if (sec.rootDetected) "Binaire su détecté" else "Non détecté",
                    valueColor = if (sec.rootDetected) StatusWarning else EmeraldPrimary
                )
                InfoRow(
                    label = "Signature Build",
                    value = if (sec.isDebugBuild) "test-keys" else "release-keys",
                    monospaceValue = true,
                    showDivider = false
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
