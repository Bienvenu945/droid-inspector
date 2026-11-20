package com.droidinspector.feature.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.InfoRow
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.EmeraldLight
import com.droidinspector.core.theme.EmeraldPrimary

/**
 * Écran À propos, Paramètres & Signature officielle @Bienvenu.
 */
@Composable
fun AboutScreen(
    viewModel: AboutViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val refreshInterval by viewModel.refreshIntervalMs.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Retour", tint = EmeraldPrimary)
            }
            Text(
                text = "À propos & Paramètres",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Signature Officielle
        GlassCard(contentPadding = 24.dp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(46.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "DROID INSPECTOR X",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Android System Intelligence & Diagnostics",
                    fontSize = 13.sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Developed by @Bienvenu",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Version 1.0.0 · com.droidinspector.app",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SectionHeader("Préférences de l'Application", "Thème et fréquence d'actualisation")

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.DarkMode, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Mode Sombre (Défaut)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Verre dépoli vert émeraude", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = viewModel::toggleDarkTheme,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = EmeraldPrimary
                        )
                    )
                }

                Column {
                    Text(
                        text = "Intervalle de rafraîchissement Dashboard",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1000L to "1s", 2000L to "2s", 5000L to "5s").forEach { (ms, label) ->
                            FilterChip(
                                selected = refreshInterval == ms,
                                onClick = { viewModel.setRefreshInterval(ms) },
                                label = { Text(label, fontFamily = FontFamily.Monospace) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = EmeraldLight
                                )
                            )
                        }
                    }
                }
            }
        }

        SectionHeader("Confidentialité & Transparence", "Aucune transmission de données")

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Security, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("100% Local & Hors-Ligne", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Text(
                    text = "Toutes les inspections matérielles, mesures de capteurs, inventaires d'applications et exports PDF sont exécutés exclusivement sur votre smartphone. Aucune information n'est collectée ou envoyée à l'extérieur.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SectionHeader("Architecture & Crédits", "Pile technologique Android")
        GlassCard {
            Column {
                InfoRow("Langage", "Kotlin 2.0.21")
                InfoRow("Interface", "Jetpack Compose + Material 3")
                InfoRow("Architecture", "MVVM Clean (Presentation / Domain / Data)")
                InfoRow("Injection", "Dagger Hilt 2.52")
                InfoRow("Stockage", "DataStore Preferences & SAF")
                InfoRow("Auteur Officiel", "@Bienvenu", valueColor = EmeraldPrimary, showDivider = false)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
