package com.droidinspector.feature.storage

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.droidinspector.core.theme.EmeraldLight
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.utils.FormatUtils

/**
 * Module 06 : Storage Analyzer & Explorateur SAF.
 * Signature : @Bienvenu
 */
@Composable
fun StorageScreen(
    viewModel: StorageViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            viewModel.onSafTreeSelected(uri)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Retour", tint = EmeraldPrimary)
            }
            Text(
                text = "Storage Analyzer",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        val storage = state.storageInfo
        if (state.isLoading || storage == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = EmeraldPrimary)
            }
            return@Column
        }

        val usedPct = if (storage.totalBytes > 0L) {
            ((storage.usedBytes.toDouble() / storage.totalBytes.toDouble()) * 100).toFloat().coerceIn(0f, 100f)
        } else 0f

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                GlassCard(contentPadding = 20.dp) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MetricGauge(
                            value = usedPct,
                            size = 136.dp,
                            strokeWidth = 11.dp,
                            label = "Occupé"
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "${FormatUtils.formatBytes(storage.usedBytes)} utilisés / ${FormatUtils.formatBytes(storage.totalBytes)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = EmeraldLight
                        )
                        Text(
                            text = "${FormatUtils.formatBytes(storage.availableBytes)} disponibles",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            item {
                SectionHeader("Détails du Système de Fichiers", "Partition interne mesurée par StatFs")
                GlassCard {
                    Column {
                        InfoRow("Espace Total", FormatUtils.formatBytes(storage.totalBytes), monospaceValue = true)
                        InfoRow("Espace Utilisé", FormatUtils.formatBytes(storage.usedBytes), valueColor = EmeraldPrimary, monospaceValue = true)
                        InfoRow("Espace Disponible", FormatUtils.formatBytes(storage.availableBytes), monospaceValue = true)
                        InfoRow("Point de montage", storage.dataPath, monospaceValue = true)
                        InfoRow("Taille de bloc", "${storage.blockSizeBytes} octets", monospaceValue = true, showDivider = false)
                    }
                }
            }

            item {
                SectionHeader(
                    title = "Explorateur de Stockage (SAF)",
                    subtitle = "Sélectionnez un répertoire via Storage Access Framework"
                )

                Button(
                    onClick = { folderPickerLauncher.launch(null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.selectedTreeUri == null) "Choisir un dossier à explorer" else "Changer de répertoire racine",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (state.selectedTreeUri != null) {
                item {
                    // Fil d'Ariane cliquable
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        state.breadcrumbs.forEachIndexed { index, crumb ->
                            if (index > 0) {
                                Icon(
                                    imageVector = Icons.Rounded.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = crumb.name,
                                fontSize = 12.sp,
                                fontWeight = if (index == state.breadcrumbs.lastIndex) FontWeight.Bold else FontWeight.Normal,
                                color = if (index == state.breadcrumbs.lastIndex) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable { viewModel.navigateToBreadcrumb(index) }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        placeholder = { Text("Filtrer les fichiers du dossier...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = EmeraldPrimary) },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Effacer")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                if (state.isLoadingFolder) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = EmeraldPrimary)
                        }
                    }
                } else if (state.filteredFiles.isEmpty()) {
                    item {
                        GlassCard {
                            Text(
                                text = "Ce dossier ne contient aucun fichier accessible correspondant au filtre.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(state.filteredFiles, key = { it.uriString }) { file ->
                        GlassCard(
                            modifier = if (file.isDirectory) {
                                Modifier.clickable { viewModel.openSubFolder(file) }
                            } else Modifier,
                            contentPadding = 12.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (file.isDirectory) Icons.Rounded.Folder else Icons.Rounded.InsertDriveFile,
                                    contentDescription = null,
                                    tint = if (file.isDirectory) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = file.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = FormatUtils.formatDate(file.lastModifiedMillis),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (!file.isDirectory) {
                                    Text(
                                        text = FormatUtils.formatBytes(file.sizeBytes),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = EmeraldLight
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = null,
                                        tint = EmeraldPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
