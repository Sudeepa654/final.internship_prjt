package com.example.suryashaktimain.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.suryashaktimain.data.local.EnergyLogEntity
import com.example.suryashaktimain.ui.components.AppBackground
import com.example.suryashaktimain.ui.components.MetricCard
import com.example.suryashaktimain.ui.components.SectionTitle
import com.example.suryashaktimain.ui.components.SolarUsageRing
import com.example.suryashaktimain.ui.components.kwh
import com.example.suryashaktimain.ui.components.money
import com.example.suryashaktimain.ui.theme.SolarBlack
import com.example.suryashaktimain.ui.theme.SolarCyan
import com.example.suryashaktimain.ui.theme.SolarMuted
import com.example.suryashaktimain.ui.theme.SolarOrange
import com.example.suryashaktimain.ui.theme.SolarPanel
import com.example.suryashaktimain.ui.theme.SolarPanelLight
import com.example.suryashaktimain.ui.theme.SolarText
import com.example.suryashaktimain.ui.theme.SolarYellow
import com.example.suryashaktimain.viewmodel.EnergyViewModel

@Composable
fun DashboardScreen(
    userName: String,
    energyViewModel: EnergyViewModel,
    onAddLogClick: () -> Unit
) {
    val metrics by energyViewModel.dashboardMetrics.collectAsStateWithLifecycle()
    val logs by energyViewModel.logs.collectAsStateWithLifecycle()

    AppBackground {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DashboardHeader(
                    userName = userName,
                    onAddLogClick = onAddLogClick
                )
            }
            item {
                SolarUsageRing(
                    solarShare = metrics.solarShare,
                    gridShare = metrics.gridShare,
                    independenceScore = metrics.independenceScore
                )
            }
            item {
                MetricGrid(
                    solarGeneration = metrics.todaySolarGeneration,
                    consumption = metrics.todayConsumption,
                    battery = metrics.batteryPercentage,
                    savings = metrics.netSavings,
                    exported = metrics.exportedToGrid
                )
            }
            item {
                SuggestionCard(suggestion = metrics.peakUsageSuggestion)
            }
            item {
                SectionTitle("Recent Logs")
            }
            if (logs.isEmpty()) {
                item { EmptyLogsCard(onAddLogClick = onAddLogClick) }
            } else {
                items(logs.take(3), key = { it.id }) { log ->
                    RecentLogCard(log = log)
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(userName: String, onAddLogClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hello, $userName",
                color = SolarText,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Your solar home command center",
                color = SolarMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(
            onClick = onAddLogClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = SolarYellow,
                contentColor = SolarBlack
            )
        ) {
            Text("Log")
        }
    }
}

@Composable
private fun MetricGrid(
    solarGeneration: Double,
    consumption: Double,
    battery: Int,
    savings: Double,
    exported: Double
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Solar generated",
                value = solarGeneration.kwh(),
                subtitle = "Today",
                icon = Icons.Default.WbSunny,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Consumption",
                value = consumption.kwh(),
                subtitle = "Today",
                icon = Icons.Default.ElectricMeter,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Battery",
                value = "$battery%",
                subtitle = "Latest level",
                icon = Icons.Default.BatteryChargingFull,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Net savings",
                value = savings.money(),
                subtitle = "Solar units used",
                icon = Icons.Default.CurrencyRupee,
                modifier = Modifier.weight(1f)
            )
        }
        MetricCard(
            title = "Exported to Grid",
            value = exported.kwh(),
            subtitle = "Over-generation energy",
            icon = Icons.Default.GridOn,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SuggestionCard(suggestion: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SolarPanel),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = SolarOrange)
            Column {
                Text("Peak usage suggestion", color = SolarMuted, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = suggestion,
                    color = SolarText,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun EmptyLogsCard(onAddLogClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SolarPanel),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("No logs yet", color = SolarText, style = MaterialTheme.typography.titleMedium)
            Text(
                "Add your first generation and consumption entry to view live savings.",
                color = SolarMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = onAddLogClick,
                colors = ButtonDefaults.buttonColors(containerColor = SolarYellow, contentColor = SolarBlack)
            ) {
                Text("Add energy log")
            }
        }
    }
}

@Composable
private fun RecentLogCard(log: EnergyLogEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SolarPanel),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(log.date, color = SolarText, style = MaterialTheme.typography.titleMedium)
                Text(log.weatherCondition, color = SolarYellow, style = MaterialTheme.typography.labelLarge)
            }
            LinearProgressIndicator(
                progress = {
                    if (log.consumption > 0.0) (log.solarUsage / log.consumption).toFloat().coerceIn(0f, 1f) else 0f
                },
                modifier = Modifier.fillMaxWidth(),
                color = SolarYellow,
                trackColor = SolarPanelLight
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Solar ${log.solarGeneration.kwh()}", color = SolarMuted, style = MaterialTheme.typography.bodyMedium)
                Text("Used ${log.consumption.kwh()}", color = SolarMuted, style = MaterialTheme.typography.bodyMedium)
                Text("Grid ${log.gridUsage.kwh()}", color = SolarCyan, style = MaterialTheme.typography.bodyMedium)
            }
            if (log.exportedToGrid > 0.0) {
                Text(
                    "Exported ${log.exportedToGrid.kwh()}",
                    color = SolarYellow,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

