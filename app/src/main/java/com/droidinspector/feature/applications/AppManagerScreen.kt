package com.droidinspector.feature.applications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.InfoRow
import com.droidinspector.core.theme.EmeraldLight
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.utils.FormatUtils
import com.droidinspector.domain.model.AppPackageInfo

/**
 * Module 05 : App Manager utilisant PackageManager.
 * Signature : @Bienvenu
 */
@Composable
fun AppManagerScreen(
    viewModel: AppManagerViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Retour", tint = EmeraldPrimary)
                }
                Column {
                    Text(
                        text = "App Manager",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${state.filteredApps.size} applications accessibles",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box {
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(Icons.Rounded.Sort, contentDescription = "Trier", tint = EmeraldPrimary)
                }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    AppSortOrder.entries.forEach { order ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = order.label,
                                    color = if (state.sortOrder == order) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                viewModel.onSortOrderChange(order)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Barre de recherche
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Rechercher par nom ou package...",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = EmeraldPrimary) },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Effacer")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmeraldPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filtres (Toutes / Utilisateur / Système)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppFilter.entries.forEach { filter ->
                val selected = state.filter == filter
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.onFilterChange(filter) },
                    label = { Text(filter.label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = EmeraldLight
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = EmeraldPrimary)
            }
        } else if (state.filteredApps.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aucune application ne correspond à cette recherche.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.filteredApps, key = { it.packageName }) { app ->
                    AppItemCard(app = app, onClick = { viewModel.selectApp(app) })
                }
            }
        }
    }

    // Fiche détaillée de l'application sélectionnée
    state.selectedApp?.let { app ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectApp(null) },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AppDetailsSheet(app = app, onClose = { viewModel.selectApp(null) })
        }
    }
}

@Composable
private fun AppItemCard(
    app: AppPackageInfo,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.clickable(onClick = onClick),
        contentPadding = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (app.iconBitmap != null) {
                Image(
                    bitmap = app.iconBitmap.asImageBitmap(),
                    contentDescription = app.appName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Apps, contentDescription = null, tint = EmeraldPrimary)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "v${app.versionName} · ${if (app.isSystemApp) "Système" else "Utilisateur"}",
                    fontSize = 11.sp,
                    color = if (app.isSystemApp) MaterialTheme.colorScheme.onSurfaceVariant else EmeraldPrimary
                )
            }

            Text(
                text = if (app.apkSizeBytes > 0L) FormatUtils.formatBytes(app.apkSizeBytes) else "--",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AppDetailsSheet(
    app: AppPackageInfo,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = app.appName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onClose) {
                Text("Fermer", color = EmeraldPrimary)
            }
        }

        Text(
            text = app.packageName,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = EmeraldPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        InfoRow("Version", "${app.versionName} (${app.versionCode})", monospaceValue = true)
        InfoRow("Type", if (app.isSystemApp) "Application système" else "Application utilisateur")
        InfoRow("Taille APK", if (app.apkSizeBytes > 0L) FormatUtils.formatBytes(app.apkSizeBytes) else "Non mesurable", monospaceValue = true)
        InfoRow("SDK Cible / Min", "API ${app.targetSdkVersion} / API ${app.minSdkVersion}", monospaceValue = true)
        InfoRow("Première installation", FormatUtils.formatDate(app.firstInstallTime), monospaceValue = true)
        InfoRow("Dernière mise à jour", FormatUtils.formatDate(app.lastUpdateTime), monospaceValue = true, showDivider = false)

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Permissions déclarées (${app.requestedPermissions.size})",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (app.requestedPermissions.isEmpty()) {
            Text(
                text = "Aucune permission déclarée ou inaccessible.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            app.requestedPermissions.forEach { perm ->
                Text(
                    text = "• $perm",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
