package com.droidinspector.feature.reports

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.EmeraldLight
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.StatusDanger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Module 08 : Report Generator PDF natif.
 * Pied de page officiel : DROID INSPECTOR X — @Bienvenu
 */
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            viewModel.exportPdfToUri(uri)
        }
    }

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
                text = "Report Generator",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        SectionHeader(
            title = "Contenu du Rapport PDF",
            subtitle = "Sélectionnez les modules à inclure dans le document A4"
        )

        val sel = state.selection
        GlassCard {
            Column {
                ReportCheckboxRow(
                    label = "Identification du périphérique",
                    checked = sel.includeDevice,
                    onCheckedChange = { checked ->
                        viewModel.updateSelection { it.copy(includeDevice = checked) }
                    }
                )
                ReportCheckboxRow(
                    label = "Informations matérielles (CPU, RAM, GPU, Écran)",
                    checked = sel.includeHardware,
                    onCheckedChange = { checked ->
                        viewModel.updateSelection { it.copy(includeHardware = checked) }
                    }
                )
                ReportCheckboxRow(
                    label = "Informations système & sécurité",
                    checked = sel.includeSystem,
                    onCheckedChange = { checked ->
                        viewModel.updateSelection { it.copy(includeSystem = checked) }
                    }
                )
                ReportCheckboxRow(
                    label = "Diagnostic batterie",
                    checked = sel.includeBattery,
                    onCheckedChange = { checked ->
                        viewModel.updateSelection { it.copy(includeBattery = checked) }
                    }
                )
                ReportCheckboxRow(
                    label = "Analyse du stockage interne",
                    checked = sel.includeStorage,
                    onCheckedChange = { checked ->
                        viewModel.updateSelection { it.copy(includeStorage = checked) }
                    }
                )
                ReportCheckboxRow(
                    label = "Capteurs détectés (${state.sensorsCount})",
                    checked = sel.includeSensors,
                    onCheckedChange = { checked ->
                        viewModel.updateSelection { it.copy(includeSensors = checked) }
                    }
                )
                ReportCheckboxRow(
                    label = "Applications accessibles (${state.appsCount})",
                    checked = sel.includeApps,
                    onCheckedChange = { checked ->
                        viewModel.updateSelection { it.copy(includeApps = checked) }
                    }
                )
            }
        }

        // Aperçu du certificat PDF
        GlassCard(contentPadding = 18.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PictureAsPdf,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "APERÇU DU RAPPORT PDF",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = state.deviceTitle.ifEmpty { "Terminal Android" },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DROID INSPECTOR X — @Bienvenu",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = EmeraldLight
                    )
                    Text(
                        text = "Format A4",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        state.statusMessage?.let { msg ->
            GlassCard(borderColor = EmeraldPrimary) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(msg, fontSize = 12.sp, color = EmeraldLight)
                }
            }
        }

        state.errorMessage?.let { err ->
            GlassCard(borderColor = StatusDanger) {
                Text(err, fontSize = 12.sp, color = StatusDanger)
            }
        }

        Button(
            onClick = {
                val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
                createPdfLauncher.launch("DroidInspectorX_Report_$stamp.pdf")
            },
            enabled = !state.isGenerating,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmeraldPrimary,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (state.isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
            } else {
                Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enregistrer le Rapport PDF", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = {
                viewModel.generateForSharing { file ->
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra(Intent.EXTRA_SUBJECT, "Rapport Droid Inspector X — @Bienvenu")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Partager le rapport PDF"))
                }
            },
            enabled = !state.isGenerating,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Rounded.Share, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Partager le Rapport PDF", color = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ReportCheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = EmeraldPrimary,
                checkmarkColor = Color.Black
            )
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
