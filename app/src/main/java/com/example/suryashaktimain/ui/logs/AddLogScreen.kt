package com.example.suryashaktimain.ui.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.suryashaktimain.data.local.UserEntity
import com.example.suryashaktimain.domain.EnergyCalculation
import com.example.suryashaktimain.domain.WeatherCondition
import com.example.suryashaktimain.ui.components.AppBackground
import com.example.suryashaktimain.ui.components.LabeledValue
import com.example.suryashaktimain.ui.components.SectionTitle
import com.example.suryashaktimain.ui.components.SolarTextField
import com.example.suryashaktimain.ui.components.kwh
import com.example.suryashaktimain.ui.components.money
import com.example.suryashaktimain.ui.theme.SolarBlack
import com.example.suryashaktimain.ui.theme.SolarMuted
import com.example.suryashaktimain.ui.theme.SolarPanel
import com.example.suryashaktimain.ui.theme.SolarText
import com.example.suryashaktimain.ui.theme.SolarYellow
import com.example.suryashaktimain.viewmodel.EnergyViewModel
import java.time.LocalDate

@Composable
fun AddLogScreen(
    user: UserEntity,
    energyViewModel: EnergyViewModel
) {
    val uiState by energyViewModel.uiState.collectAsStateWithLifecycle()
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var weather by remember { mutableStateOf(WeatherCondition.SUNNY) }
    var solarGeneration by remember {
        mutableStateOf(energyViewModel.simulateGeneration(WeatherCondition.SUNNY, user.solarPanelCapacityKw))
    }
    var batteryLevel by remember { mutableFloatStateOf(72f) }
    var previousReading by remember { mutableStateOf("") }
    var currentReading by remember { mutableStateOf("") }
    var unitsConsumed by remember { mutableStateOf("") }

    val meterUnits = calculateMeterUnits(previousReading, currentReading)
    val unitsForPreview = unitsConsumed.ifBlank { meterUnits.toString() }
    val preview = energyViewModel.calculatePreview(
        solarGenerationText = solarGeneration,
        unitsConsumedText = unitsForPreview,
        electricityRate = user.electricityUnitRate
    )

    fun applyWeatherSimulation(selectedWeather: WeatherCondition) {
        val simulatedLog = energyViewModel.simulateLogInputs(
            weather = selectedWeather,
            capacityKw = user.solarPanelCapacityKw
        )
        solarGeneration = simulatedLog.solarGeneration.toString()
        batteryLevel = simulatedLog.batteryLevel.toFloat()
        previousReading = simulatedLog.previousMeterReading.toString()
        currentReading = simulatedLog.currentMeterReading.toString()
        unitsConsumed = ""
    }

    LaunchedEffect(uiState.message, uiState.errorMessage) {
        if (uiState.message != null || uiState.errorMessage != null) {
            kotlinx.coroutines.delay(3500)
            energyViewModel.clearMessages()
        }
    }

    AppBackground {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text("Add Energy Log", color = SolarText, style = MaterialTheme.typography.headlineSmall)
                    Text("Generation and consumption tracker", color = SolarMuted)
                }
            }
            item {
                GenerationCard(
                    date = date,
                    onDateChange = { date = it },
                    weather = weather,
                    onWeatherChange = {
                        weather = it
                        applyWeatherSimulation(it)
                    },
                    solarGeneration = solarGeneration,
                    onSolarGenerationChange = { solarGeneration = it },
                    onSimulate = { applyWeatherSimulation(weather) },
                    batteryLevel = batteryLevel,
                    onBatteryChange = { batteryLevel = it }
                )
            }
            item {
                ConsumptionCard(
                    previousReading = previousReading,
                    onPreviousReadingChange = { previousReading = it },
                    currentReading = currentReading,
                    onCurrentReadingChange = { currentReading = it },
                    unitsConsumed = unitsConsumed,
                    onUnitsConsumedChange = { unitsConsumed = it },
                    meterUnits = meterUnits,
                    onUseMeterUnits = { unitsConsumed = meterUnits.toString() }
                )
            }
            item {
                PreviewCard(preview = preview)
            }
            item {
                if (uiState.message != null || uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage ?: uiState.message.orEmpty(),
                        color = if (uiState.errorMessage != null) MaterialTheme.colorScheme.error else SolarYellow,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Button(
                    onClick = {
                        energyViewModel.addLog(
                            date = date,
                            weatherCondition = weather,
                            solarGenerationText = solarGeneration,
                            batteryLevel = batteryLevel.toInt(),
                            previousMeterReadingText = previousReading,
                            currentMeterReadingText = currentReading,
                            unitsConsumedText = unitsConsumed
                        )
                    },
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SolarYellow,
                        contentColor = SolarBlack
                    )
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Text(
                        text = if (uiState.isSaving) "Saving..." else "Save log",
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun GenerationCard(
    date: String,
    onDateChange: (String) -> Unit,
    weather: WeatherCondition,
    onWeatherChange: (WeatherCondition) -> Unit,
    solarGeneration: String,
    onSolarGenerationChange: (String) -> Unit,
    onSimulate: () -> Unit,
    batteryLevel: Float,
    onBatteryChange: (Float) -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = SolarPanel)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionTitle("Generation Log")
            SolarTextField(value = date, onValueChange = onDateChange, label = "Date (yyyy-MM-dd)")
            WeatherSelector(selected = weather, onSelected = onWeatherChange)
            SolarTextField(
                value = solarGeneration,
                onValueChange = onSolarGenerationChange,
                label = "Solar generation in kWh"
            )
            OutlinedButton(onClick = onSimulate, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = SolarYellow)
                Text("Simulate weather and meter", modifier = Modifier.padding(start = 8.dp), color = SolarYellow)
            }
            Text("Battery level: ${batteryLevel.toInt()}%", color = SolarText)
            Slider(
                value = batteryLevel,
                onValueChange = onBatteryChange,
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = SolarYellow,
                    activeTrackColor = SolarYellow,
                    inactiveTrackColor = SolarMuted
                )
            )
        }
    }
}

@Composable
private fun WeatherSelector(
    selected: WeatherCondition,
    onSelected: (WeatherCondition) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WeatherCondition.entries.forEach { weather ->
            FilterChip(
                selected = selected == weather,
                onClick = { onSelected(weather) },
                label = { Text(weather.label) },
                leadingIcon = {
                    Icon(weather.icon(), contentDescription = null, modifier = Modifier.padding(start = 2.dp))
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SolarYellow,
                    selectedLabelColor = SolarBlack,
                    selectedLeadingIconColor = SolarBlack,
                    labelColor = SolarText,
                    iconColor = SolarMuted
                )
            )
        }
    }
}

@Composable
private fun ConsumptionCard(
    previousReading: String,
    onPreviousReadingChange: (String) -> Unit,
    currentReading: String,
    onCurrentReadingChange: (String) -> Unit,
    unitsConsumed: String,
    onUnitsConsumedChange: (String) -> Unit,
    meterUnits: Double,
    onUseMeterUnits: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = SolarPanel)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionTitle("Consumption Tracker")
            SolarTextField(
                value = previousReading,
                onValueChange = onPreviousReadingChange,
                label = "Previous meter reading"
            )
            SolarTextField(
                value = currentReading,
                onValueChange = onCurrentReadingChange,
                label = "Current meter reading"
            )
            LabeledValue("Meter difference", meterUnits.kwh())
            SolarTextField(
                value = unitsConsumed,
                onValueChange = onUnitsConsumedChange,
                label = "Units consumed in kWh"
            )
            OutlinedButton(onClick = onUseMeterUnits, modifier = Modifier.fillMaxWidth()) {
                Text("Use meter difference", color = SolarYellow)
            }
        }
    }
}

@Composable
private fun PreviewCard(preview: EnergyCalculation) {
    Card(colors = CardDefaults.cardColors(containerColor = SolarPanel)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle("Calculated Result")
            LabeledValue("Solar usage", preview.solarUsage.kwh())
            LabeledValue("Grid usage", preview.gridUsage.kwh())
            LabeledValue("Net usage", preview.netUsage.kwh())
            LabeledValue("Exported to Grid", preview.exportedToGrid.kwh())
            LabeledValue("Net savings", preview.savings.money())
            if (preview.exportedToGrid > 0.0) {
                Text(
                    "Extra energy exported to grid.",
                    color = SolarYellow,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

private fun WeatherCondition.icon(): ImageVector {
    return when (this) {
        WeatherCondition.SUNNY -> Icons.Default.WbSunny
        WeatherCondition.CLOUDY -> Icons.Default.Cloud
        WeatherCondition.RAINY -> Icons.Default.Cloud
    }
}

private fun calculateMeterUnits(previousReading: String, currentReading: String): Double {
    val previous = previousReading.toDoubleOrNull() ?: 0.0
    val current = currentReading.toDoubleOrNull() ?: 0.0
    return (current - previous).coerceAtLeast(0.0)
}

