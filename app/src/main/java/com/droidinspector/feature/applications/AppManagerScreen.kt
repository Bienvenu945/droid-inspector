package com.droidinspector.feature.applications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.TextMutedDark

data class DemoAppItem(
    val name: String,
    val packageName: String,
    val version: String,
    val isSystem: Boolean,
    val sizeMb: Float
)

@Composable
fun AppManagerScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterSystem by remember { mutableStateOf(false) }

    val sampleApps = remember {
        listOf(
            DemoAppItem("DROID INSPECTOR X", "com.droidinspector.app", "1.0.0", false, 18.4f),
            DemoAppItem("Google Chrome", "com.android.chrome", "131.0.6778.85", true, 192.1f),
            DemoAppItem("Services Google Play", "com.google.android.gms", "24.45.33", true, 240.5f),
            DemoAppItem("Android System Webview", "com.google.android.webview", "131.0.6778.85", true, 89.2f),
            DemoAppItem("WhatsApp", "com.whatsapp", "2.24.23.78", false, 74.8f),
            DemoAppItem("Spotify", "com.spotify.music", "8.9.84.594", false, 88.0f),
            DemoAppItem("Appareil Photo", "com.google.android.GoogleCamera", "9.4.103", true, 142.3f)
        )
    }

    val filteredApps = sampleApps.filter {
        (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true)) &&
                (!filterSystem || !it.isSystem)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
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
                text = "App Manager",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher une application ou package...", color = TextMutedDark, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = EmeraldPrimary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmeraldPrimary,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${filteredApps.size} applications répertoriées",
            fontSize = 12.sp,
            color = TextMutedDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredApps) { app ->
                GlassCard(contentPadding = 12.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = app.name,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = app.packageName,
                                color = TextMutedDark,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "v${app.version} · ${if (app.isSystem) "Système" else "Utilisateur"}",
                                color = if (app.isSystem) EmeraldPrimary else Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "${app.sizeMb} Mo",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
