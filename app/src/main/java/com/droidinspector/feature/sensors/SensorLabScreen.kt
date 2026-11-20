package com.droidinspector.feature.sensors

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
fun SensorLabScreen(
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
                text = "Sensor Lab",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("Capteurs Mouvement & Orientation", "Lecture en temps réel via SensorManager")

        SensorCardItem(
            name = "Accéléromètre 3 Axes",
            vendor = "Bosch Sensortec / STMicroelectronics",
            type = "TYPE_ACCELEROMETER",
            values = listOf("X: +0.12 m/s²", "Y: +9.78 m/s²", "Z: +0.45 m/s²"),
            maxRange = "78.4 m/s²",
            power = "0.25 mA"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SensorCardItem(
            name = "Gyroscope Calibré",
            vendor = "InvenSense / TDK",
            type = "TYPE_GYROSCOPE",
            values = listOf("X: +0.001 rad/s", "Y: -0.002 rad/s", "Z: +0.000 rad/s"),
            maxRange = "34.9 rad/s",
            power = "3.20 mA"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SensorCardItem(
            name = "Magnétomètre (Boussole)",
            vendor = "Asahi Kasei Microdevices (AKM)",
            type = "TYPE_MAGNETIC_FIELD",
            values = listOf("X: -14.2 µT", "Y: +28.5 µT", "Z: -41.0 µT"),
            maxRange = "4900.0 µT",
            power = "0.80 mA"
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader("Capteurs d'Environnement", "Lumière ambiante, pression et proximité")

        SensorCardItem(
            name = "Capteur de Luminosité Ambiante",
            vendor = "AMS AG",
            type = "TYPE_LIGHT",
            values = listOf("Luminance: 320.0 lux"),
            maxRange = "100000 lux",
            power = "0.15 mA"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SensorCardItem(
            name = "Capteur de Proximité",
            vendor = "AMS AG",
            type = "TYPE_PROXIMITY",
            values = listOf("Distance: 5.0 cm (Loin)"),
            maxRange = "5.0 cm",
            power = "0.75 mA"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SensorCardItem(
            name = "Baromètre (Pression Atmosphérique)",
            vendor = "Bosch Sensortec BMP380",
            type = "TYPE_PRESSURE",
            values = listOf("Pression: 1013.25 hPa", "Altitude est.: 54 m"),
            maxRange = "1250.0 hPa",
            power = "0.08 mA"
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SensorCardItem(
    name: String,
    vendor: String,
    type: String,
    values: List<String>,
    maxRange: String,
    power: String
) {
    GlassCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Text(text = type, color = EmeraldPrimary, fontSize = 11.sp)
            }
            Text(text = "Fabriquant: $vendor", color = TextMutedDark, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                values.forEach { v ->
                    Surface(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = v,
                            color = EmeraldPrimary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Plage max: $maxRange · Consommation: $power",
                color = TextMutedDark,
                fontSize = 11.sp
            )
        }
    }
}
