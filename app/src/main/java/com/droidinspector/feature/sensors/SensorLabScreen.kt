package com.droidinspector.feature.sensors

import android.hardware.Sensor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.EmeraldLight
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.StatusWarning
import com.droidinspector.domain.model.SensorInfo
import java.util.Locale

/**
 * Module 04 : Sensor Lab avec lecture temps réel et niveau à bulle interactif.
 * Signature : @Bienvenu
 */
@Composable
fun SensorLabScreen(
    viewModel: SensorLabViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

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
            Column {
                Text(
                    text = "Sensor Lab",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${state.sensors.size} capteurs détectés via SensorManager",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Visualiseur interactif d'inclinaison (Accéléromètre)
            item {
                GlassCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Inclinomètre en Temps Réel",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (state.hasAccelerometer) "ACTIF" else "ABSENT",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (state.hasAccelerometer) EmeraldLight else StatusWarning
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (state.hasAccelerometer) {
                            val ax = state.accelerometerValues.getOrElse(0) { 0f }
                            val ay = state.accelerometerValues.getOrElse(1) { 0f }
                            val az = state.accelerometerValues.getOrElse(2) { 0f }

                            TiltBubbleVisualizer(ax = ax, ay = ay)

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = String.format(
                                    Locale.US,
                                    "X: %+.2f  Y: %+.2f  Z: %+.2f m/s²",
                                    ax, ay, az
                                ),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = EmeraldPrimary
                            )
                        } else {
                            Text(
                                text = "Aucun accéléromètre matériel détecté sur cet appareil.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                }
            }

            // Capteurs principaux absents (information claire et transparente)
            if (state.missingCoreSensors.isNotEmpty()) {
                item {
                    GlassCard(borderColor = StatusWarning.copy(alpha = 0.45f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Warning,
                                contentDescription = null,
                                tint = StatusWarning,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Capteurs absents sur ce matériel",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = state.missingCoreSensors.joinToString(" · "),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Capteur sélectionné : valeurs temps réel
            item {
                val activeSensor = state.sensors.find { it.type == state.selectedSensorType }
                if (activeSensor != null) {
                    SectionHeader(
                        title = "Sonde Active : ${activeSensor.typeString}",
                        subtitle = "Appuyez sur un capteur ci-dessous pour suivre son flux"
                    )
                    GlassCard(borderColor = EmeraldPrimary.copy(alpha = 0.5f)) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = activeSensor.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Fabricant : ${activeSensor.vendor}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (state.selectedSensorValues.isNotEmpty()) {
                                val unit = sensorUnitFor(activeSensor.type)
                                val formatted = state.selectedSensorValues.mapIndexed { idx, value ->
                                    val axis = if (state.selectedSensorValues.size > 1) listOf("X", "Y", "Z", "W").getOrElse(idx) { "V$idx" } + ": " else ""
                                    String.format(Locale.US, "%s%.3f %s", axis, value, unit)
                                }
                                Text(
                                    text = formatted.joinToString("  |  "),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = EmeraldLight,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            } else {
                                Text(
                                    text = "En attente d'événement capteur...",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                SectionHeader(
                    title = "Inventaire des Capteurs",
                    subtitle = "Nom, fabricant, plage maximale et résolution"
                )
            }

            if (state.sensors.isEmpty()) {
                item {
                    GlassCard {
                        Text(
                            text = "Aucun capteur matériel n'a été retourné par SensorManager.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                itemsIndexed(state.sensors, key = { index, sensor -> "${index}_${sensor.type}_${sensor.name}" }) { _, sensor ->
                    SensorRowCard(
                        sensor = sensor,
                        isSelected = sensor.type == state.selectedSensorType,
                        onClick = { viewModel.selectSensor(sensor.type) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TiltBubbleVisualizer(ax: Float, ay: Float) {
    val normX = (-ax / 9.81f).coerceIn(-1f, 1f)
    val normY = (ay / 9.81f).coerceIn(-1f, 1f)

    Box(
        modifier = Modifier
            .size(130.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f))
            .border(1.5.dp, EmeraldPrimary.copy(alpha = 0.45f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            drawLine(
                color = EmeraldPrimary.copy(alpha = 0.2f),
                start = Offset(0f, cy),
                end = Offset(size.width, cy),
                strokeWidth = 1.5f
            )
            drawLine(
                color = EmeraldPrimary.copy(alpha = 0.2f),
                start = Offset(cx, 0f),
                end = Offset(cx, size.height),
                strokeWidth = 1.5f
            )
            drawCircle(
                color = EmeraldPrimary.copy(alpha = 0.3f),
                radius = size.minDimension * 0.22f,
                style = Stroke(width = 1.5f)
            )
            val maxOffset = size.minDimension * 0.36f
            drawCircle(
                color = EmeraldLight,
                radius = 12f,
                center = Offset(cx + normX * maxOffset, cy + normY * maxOffset)
            )
        }
    }
}

@Composable
private fun SensorRowCard(
    sensor: SensorInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.clickable(onClick = onClick),
        borderColor = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Sensors,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = sensor.typeString,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    color = EmeraldPrimary.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isSelected) "ACTIF" else "TYPE ${sensor.type}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = EmeraldLight,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = sensor.name,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Fabricant: ${sensor.vendor}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = String.format(
                    Locale.US,
                    "Plage max: %.2f · Résolution: %.4f · Conso: %.2f mA",
                    sensor.maxRange, sensor.resolution, sensor.powerMa
                ),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun sensorUnitFor(type: Int): String = when (type) {
    Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GRAVITY, Sensor.TYPE_LINEAR_ACCELERATION -> "m/s²"
    Sensor.TYPE_GYROSCOPE -> "rad/s"
    Sensor.TYPE_MAGNETIC_FIELD -> "µT"
    Sensor.TYPE_LIGHT -> "lx"
    Sensor.TYPE_PROXIMITY -> "cm"
    Sensor.TYPE_PRESSURE -> "hPa"
    Sensor.TYPE_AMBIENT_TEMPERATURE -> "°C"
    Sensor.TYPE_RELATIVE_HUMIDITY -> "%"
    else -> ""
}
