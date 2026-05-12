package com.example.suryashaktimain.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.suryashaktimain.data.local.EnergyLogEntity
import com.example.suryashaktimain.domain.EnergyCalculator
import com.example.suryashaktimain.ui.components.AppBackground
import com.example.suryashaktimain.ui.components.LabeledValue
import com.example.suryashaktimain.ui.components.MetricCard
import com.example.suryashaktimain.ui.components.ReportBarChart
import com.example.suryashaktimain.ui.components.SectionTitle
import com.example.suryashaktimain.ui.components.kwh
import com.example.suryashaktimain.ui.components.money
import com.example.suryashaktimain.ui.theme.SolarMuted
import com.example.suryashaktimain.ui.theme.SolarPanel
import com.example.suryashaktimain.ui.theme.SolarText
import com.example.suryashaktimain.ui.theme.SolarYellow
import com.example.suryashaktimain.viewmodel.EnergyViewModel

@Composable
fun ReportsScreen(energyViewModel: EnergyViewModel) {
    val logs by energyViewModel.logs.collectAsStateWithLifecycle()
    val report by energyViewModel.reportMetrics.collectAsStateWithLifecycle()
    val last30Logs = EnergyCalculator.logsWithinLast30Days(logs)

    AppBackground {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text("30-Day Savings Report", color = SolarText, style = MaterialTheme.typography.headlineSmall)
                    Text("Solar performance, savings, and grid export", color = SolarMuted)
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard(
                            title = "Solar generated",
                            value = report.totalSolarGenerated.kwh(),
                            subtitle = "Total",
                            icon = Icons.Default.WbSunny,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Units consumed",
                            value = report.totalUnitsConsumed.kwh(),
                            subtitle = "Total",
                            icon = Icons.Default.ElectricMeter,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard(
                            title = "Grid units saved",
                            value = report.totalGridUnitsSaved.kwh(),
                            subtitle = "Solar used",
                            icon = Icons.Default.Bolt,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Money saved",
                            value = report.totalMoneySaved.money(),
                            subtitle = "Net savings",
                            icon = Icons.Default.CurrencyRupee,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    MetricCard(
                        title = "Exported energy to grid",
                        value = report.exportedEnergyToGrid.kwh(),
                        subtitle = "Over-generation",
                        icon = Icons.Default.GridOn,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item {
                ReportBarChart(logs = last30Logs)
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = SolarPanel)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SectionTitle("Report Summary")
                        LabeledValue("Average independence score", "${report.averageIndependence}%")
                        LabeledValue("Formula", "Solar units used x unit rate")
                    }
                }
            }
            item {
                SectionTitle("Recent Report Entries")
            }
            if (last30Logs.isEmpty()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = SolarPanel)) {
                        Text(
                            "No report data yet. Add logs to generate a 30-day report.",
                            modifier = Modifier.padding(16.dp),
                            color = SolarMuted
                        )
                    }
                }
            } else {
                items(last30Logs, key = { it.id }) { log ->
                    ReportLogRow(log = log)
                }
            }
        }
    }
}

@Composable
private fun ReportLogRow(log: EnergyLogEntity) {
    Card(colors = CardDefaults.cardColors(containerColor = SolarPanel)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(log.date, color = SolarText, style = MaterialTheme.typography.titleMedium)
                Text(log.weatherCondition, color = SolarYellow, style = MaterialTheme.typography.labelLarge)
            }
            LabeledValue("Generated", log.solarGeneration.kwh())
            LabeledValue("Consumed", log.consumption.kwh())
            LabeledValue("Saved", log.savings.money())
            if (log.exportedToGrid > 0.0) {
                LabeledValue("Exported to Grid", log.exportedToGrid.kwh())
            }
        }
    }
}

