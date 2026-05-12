package com.example.suryashaktimain.domain

import com.example.suryashaktimain.data.local.EnergyLogEntity
import com.example.suryashaktimain.data.local.UserEntity
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class EnergyCalculation(
    val consumption: Double,
    val solarUsage: Double,
    val gridUsage: Double,
    val netUsage: Double,
    val exportedToGrid: Double,
    val savings: Double
)

data class DashboardMetrics(
    val todaySolarGeneration: Double = 0.0,
    val todayConsumption: Double = 0.0,
    val batteryPercentage: Int = 0,
    val netSavings: Double = 0.0,
    val independenceScore: Int = 0,
    val solarShare: Float = 0f,
    val gridShare: Float = 0f,
    val exportedToGrid: Double = 0.0,
    val peakUsageSuggestion: String = "Add today's first energy log to unlock suggestions.",
    val latestLog: EnergyLogEntity? = null
)

data class ReportMetrics(
    val totalSolarGenerated: Double = 0.0,
    val totalUnitsConsumed: Double = 0.0,
    val totalGridUnitsSaved: Double = 0.0,
    val totalMoneySaved: Double = 0.0,
    val exportedEnergyToGrid: Double = 0.0,
    val averageIndependence: Int = 0
)

data class SimulatedLogInputs(
    val solarGeneration: Double,
    val previousMeterReading: Double,
    val currentMeterReading: Double,
    val batteryLevel: Int
)

object EnergyCalculator {
    fun simulateGeneration(weather: WeatherCondition, capacityKw: Double): Double {
        val productiveSunHours = when (weather) {
            WeatherCondition.SUNNY -> 4.8
            WeatherCondition.CLOUDY -> 2.7
            WeatherCondition.RAINY -> 1.2
        }
        return round1(max(0.0, capacityKw) * productiveSunHours)
    }

    fun simulateLogInputs(
        weather: WeatherCondition,
        capacityKw: Double,
        lastMeterReading: Double?
    ): SimulatedLogInputs {
        val solarGeneration = simulateGeneration(weather, capacityKw)
        val consumption = simulateConsumption(weather, capacityKw)
        val previousReading = lastMeterReading?.takeIf { it > 0.0 } ?: 1000.0
        val currentReading = previousReading + consumption
        val batteryLevel = simulateBatteryLevel(weather, solarGeneration, consumption)

        return SimulatedLogInputs(
            solarGeneration = solarGeneration,
            previousMeterReading = round1(previousReading),
            currentMeterReading = round1(currentReading),
            batteryLevel = batteryLevel
        )
    }

    fun calculateLog(
        solarGeneration: Double,
        unitsConsumed: Double,
        electricityRate: Double
    ): EnergyCalculation {
        val safeGeneration = max(0.0, solarGeneration)
        val safeConsumption = max(0.0, unitsConsumed)
        val solarUsage = min(safeGeneration, safeConsumption)
        val gridUsage = max(0.0, safeConsumption - safeGeneration)
        val exported = max(0.0, safeGeneration - safeConsumption)
        val savings = solarUsage * max(0.0, electricityRate)

        return EnergyCalculation(
            consumption = round1(safeConsumption),
            solarUsage = round1(solarUsage),
            gridUsage = round1(gridUsage),
            netUsage = round1(gridUsage),
            exportedToGrid = round1(exported),
            savings = round2(savings)
        )
    }

    fun buildDashboard(user: UserEntity?, logs: List<EnergyLogEntity>): DashboardMetrics {
        if (user == null) return DashboardMetrics()

        val today = LocalDate.now().toString()
        val todayLogs = logs.filter { it.date == today }
        val latestLog = logs.firstOrNull()
        val dashboardLogs = todayLogs.ifEmpty { latestLog?.let { listOf(it) } ?: emptyList() }

        val solarGeneration = dashboardLogs.sumOf { it.solarGeneration }
        val consumption = dashboardLogs.sumOf { it.consumption }
        val solarUsage = dashboardLogs.sumOf { it.solarUsage }
        val gridUsage = dashboardLogs.sumOf { it.gridUsage }
        val exported = dashboardLogs.sumOf { it.exportedToGrid }
        val savings = dashboardLogs.sumOf { it.savings }
        val totalUsedPower = solarUsage + gridUsage
        val battery = latestLog?.batteryLevel ?: 0
        val independence = independenceScore(solarUsage, consumption, battery)

        return DashboardMetrics(
            todaySolarGeneration = round1(solarGeneration),
            todayConsumption = round1(consumption),
            batteryPercentage = battery,
            netSavings = round2(savings),
            independenceScore = independence,
            solarShare = if (totalUsedPower > 0) (solarUsage / totalUsedPower).toFloat() else 0f,
            gridShare = if (totalUsedPower > 0) (gridUsage / totalUsedPower).toFloat() else 0f,
            exportedToGrid = round1(exported),
            peakUsageSuggestion = smartSuggestion(latestLog, consumption, exported),
            latestLog = latestLog
        )
    }

    fun buildReport(logs: List<EnergyLogEntity>): ReportMetrics {
        val totalSolar = logs.sumOf { it.solarGeneration }
        val totalConsumption = logs.sumOf { it.consumption }
        val savedUnits = logs.sumOf { it.solarUsage }
        val moneySaved = logs.sumOf { it.savings }
        val exported = logs.sumOf { it.exportedToGrid }
        val independence = independenceScore(savedUnits, totalConsumption, logs.firstOrNull()?.batteryLevel ?: 0)

        return ReportMetrics(
            totalSolarGenerated = round1(totalSolar),
            totalUnitsConsumed = round1(totalConsumption),
            totalGridUnitsSaved = round1(savedUnits),
            totalMoneySaved = round2(moneySaved),
            exportedEnergyToGrid = round1(exported),
            averageIndependence = independence
        )
    }

    fun logsWithinLast30Days(logs: List<EnergyLogEntity>): List<EnergyLogEntity> {
        val startDate = LocalDate.now().minusDays(29)
        return logs.filter { log ->
            runCatching {
                !LocalDate.parse(log.date).isBefore(startDate)
            }.getOrDefault(false)
        }
    }

    fun smartSuggestion(log: EnergyLogEntity?, consumption: Double, exported: Double): String {
        if (log == null) return "Add today's first energy log to unlock suggestions."
        return when {
            exported > 0.0 -> "Extra energy exported to grid."
            log.batteryLevel < 25 -> "Battery low: reduce heavy usage."
            consumption >= 15.0 -> "Try shifting washing machine or pump usage to daytime."
            WeatherCondition.fromLabel(log.weatherCondition) == WeatherCondition.SUNNY -> {
                "High Sun: Ideal time for heavy appliances."
            }
            else -> "Balanced usage today. Keep high-load tasks near daylight hours."
        }
    }

    private fun simulateConsumption(weather: WeatherCondition, capacityKw: Double): Double {
        val baseConsumption = when (weather) {
            WeatherCondition.SUNNY -> 9.5
            WeatherCondition.CLOUDY -> 7.6
            WeatherCondition.RAINY -> 6.4
        }
        val homeSizeFactor = max(1.0, capacityKw) * 0.35
        return round1(baseConsumption + homeSizeFactor)
    }

    private fun simulateBatteryLevel(
        weather: WeatherCondition,
        solarGeneration: Double,
        consumption: Double
    ): Int {
        val baseLevel = when (weather) {
            WeatherCondition.SUNNY -> 86
            WeatherCondition.CLOUDY -> 62
            WeatherCondition.RAINY -> 38
        }
        val surplusBonus = if (solarGeneration > consumption) 8 else 0
        val heavyUsePenalty = if (consumption > solarGeneration * 1.4) 10 else 0
        return (baseLevel + surplusBonus - heavyUsePenalty).coerceIn(10, 100)
    }

    private fun independenceScore(solarUsage: Double, consumption: Double, batteryLevel: Int): Int {
        if (consumption <= 0.0) return if (solarUsage > 0.0) 100 else 0
        val solarRatio = (solarUsage / consumption) * 100
        val batteryBonus = min(10.0, max(0, batteryLevel) / 10.0)
        return (solarRatio + batteryBonus).roundToInt().coerceIn(0, 100)
    }

    private fun round1(value: Double): Double = (value * 10.0).roundToInt() / 10.0
    private fun round2(value: Double): Double = (value * 100.0).roundToInt() / 100.0
}

