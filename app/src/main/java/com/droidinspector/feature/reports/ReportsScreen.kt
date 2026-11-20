package com.droidinspector.feature.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Share
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
fun ReportsScreen(
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
                text = "Report Generator",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("Export de Rapport PDF Certifié", "Document officiel structuré avec signature de l'auteur")

        GlassCard(contentPadding = 20.dp) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "RAPPORT DE DIAGNOSTIC MATÉRIEL & SYSTÈME",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
                Text(
                    text = "Généré par le moteur Droid Inspector X",
                    fontSize = 12.sp,
                    color = TextMutedDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                ReportBulletItem("✔ Identification complète du terminal (Marque, Modèle, Build)")
                ReportBulletItem("✔ CPU & GPU (Architecture, Cœurs, Fréquences, ABIs)")
                ReportBulletItem("✔ Analyse de Batterie (Niveau, Santé, Tension, Température)")
                ReportBulletItem("✔ Analyse du Stockage & RAM (Partitions et mémoire)")
                ReportBulletItem("✔ Inventaire des Capteurs Matériels et plages maximales")
                ReportBulletItem("✔ Liste des Applications et autorisations déclarées")

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color.White.copy(alpha = 0.1f))

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DROID INSPECTOR X — @Bienvenu",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldPrimary
                    )
                    Text(
                        text = "ISO / PDF A4",
                        fontSize = 11.sp,
                        color = TextMutedDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Button(
            onClick = { /* PDF Generation Trigger */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmeraldPrimary,
                contentColor = Color.Black
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Générer & Télécharger le Rapport PDF", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { /* Share Report */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Rounded.Share, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Partager les diagnostics")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ReportBulletItem(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = Color.White.copy(alpha = 0.9f),
        modifier = Modifier.padding(vertical = 3.dp)
    )
}
