package com.example.suryashaktimain.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.suryashaktimain.data.local.EnergyLogEntity
import com.example.suryashaktimain.ui.theme.SolarBlack
import com.example.suryashaktimain.ui.theme.SolarCyan
import com.example.suryashaktimain.ui.theme.SolarMuted
import com.example.suryashaktimain.ui.theme.SolarPanel
import com.example.suryashaktimain.ui.theme.SolarPanelLight
import com.example.suryashaktimain.ui.theme.SolarText
import com.example.suryashaktimain.ui.theme.SolarYellow
import java.util.Locale
import kotlin.math.max

@Composable
fun AppBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SolarBlack)
    ) {
        content()
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SolarPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = SolarYellow)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = SolarMuted, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = value,
                color = SolarText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(subtitle, color = SolarMuted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier,
        color = SolarText,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun SolarUsageRing(
    solarShare: Float,
    gridShare: Float,
    independenceScore: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SolarPanel)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
                CircularProgressIndicator(
                    progress = { solarShare.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = SolarYellow,
                    trackColor = SolarPanelLight,
                    strokeWidth = 14.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$independenceScore%",
                        color = SolarText,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Independence",
                        color = SolarMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                LegendRow("Solar usage", solarShare, SolarYellow)
                LinearProgressIndicator(
                    progress = { solarShare.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = SolarYellow,
                    trackColor = SolarPanelLight
                )
                LegendRow("Grid usage", gridShare, SolarCyan)
                LinearProgressIndicator(
                    progress = { gridShare.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = SolarCyan,
                    trackColor = SolarPanelLight
                )
            }
        }
    }
}

@Composable
private fun LegendRow(label: String, share: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = SolarMuted, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "${(share.coerceIn(0f, 1f) * 100).toInt()}%",
            color = color,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun LabeledValue(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = SolarMuted,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            color = SolarText,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun SolarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SolarText,
            unfocusedTextColor = SolarText,
            focusedLabelColor = SolarYellow,
            unfocusedLabelColor = SolarMuted,
            focusedBorderColor = SolarYellow,
            unfocusedBorderColor = SolarPanelLight,
            cursorColor = SolarYellow
        )
    )
}

@Composable
fun ReportBarChart(
    logs: List<EnergyLogEntity>,
    modifier: Modifier = Modifier
) {
    val chartLogs = logs.take(10).reversed()
    val maxValue = max(
        1.0,
        chartLogs.maxOfOrNull { max(it.solarGeneration, it.consumption) } ?: 1.0
    )
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SolarPanel)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Generation vs Consumption", color = SolarText, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = SolarYellow, modifier = Modifier.size(18.dp))
                    Text("Solar", color = SolarMuted, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(10.dp).background(SolarCyan))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Used", color = SolarMuted, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val groupWidth = size.width / chartLogs.size.coerceAtLeast(1)
                val barWidth = groupWidth * 0.26f
                chartLogs.forEachIndexed { index, log ->
                    val startX = groupWidth * index + groupWidth * 0.22f
                    val solarHeight = (log.solarGeneration / maxValue * size.height).toFloat()
                    val consumptionHeight = (log.consumption / maxValue * size.height).toFloat()
                    drawRoundRect(
                        color = SolarYellow,
                        topLeft = Offset(startX, size.height - solarHeight),
                        size = Size(barWidth, solarHeight),
                    )
                    drawRoundRect(
                        color = SolarCyan,
                        topLeft = Offset(startX + barWidth + 6.dp.toPx(), size.height - consumptionHeight),
                        size = Size(barWidth, consumptionHeight),
                    )
                }
            }
            Text(
                text = if (chartLogs.isEmpty()) "No report data yet" else "Last ${chartLogs.size} entries",
                color = SolarMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun Double.kwh(): String = String.format(Locale.getDefault(), "%.1f kWh", this)
fun Double.money(): String = String.format(Locale.getDefault(), "Rs. %.2f", this)

