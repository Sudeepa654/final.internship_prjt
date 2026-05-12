package com.example.suryashaktimain.data.repository

import com.example.suryashaktimain.data.local.EnergyLogDao
import com.example.suryashaktimain.data.local.EnergyLogEntity
import com.example.suryashaktimain.data.local.UserDao
import com.example.suryashaktimain.data.local.UserEntity
import com.example.suryashaktimain.domain.EnergyCalculator
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class SolarRepository(
    private val userDao: UserDao,
    private val energyLogDao: EnergyLogDao
) {
    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        homeLocation: String,
        solarPanelCapacityKw: Double,
        electricityUnitRate: Double
    ): Result<UserEntity> = runCatching {
        val normalizedEmail = email.trim().lowercase(Locale.getDefault())
        if (userDao.getUserByEmail(normalizedEmail) != null) {
            error("An account with this email already exists.")
        }

        val user = UserEntity(
            name = name.trim(),
            email = normalizedEmail,
            password = password,
            homeLocation = homeLocation.trim(),
            solarPanelCapacityKw = solarPanelCapacityKw,
            electricityUnitRate = electricityUnitRate
        )
        val id = userDao.insertUser(user).toInt()
        user.copy(id = id)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> = runCatching {
        val normalizedEmail = email.trim().lowercase(Locale.getDefault())
        userDao.login(normalizedEmail, password) ?: error("Invalid email or password.")
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<Unit> = runCatching {
        val normalizedEmail = email.trim().lowercase(Locale.getDefault())
        val rows = userDao.updatePassword(normalizedEmail, newPassword)
        if (rows == 0) error("No local account found for this email.")
    }

    fun logsForUser(userId: Int): Flow<List<EnergyLogEntity>> = energyLogDao.getLogsForUser(userId)

    fun last30LogsForUser(userId: Int): Flow<List<EnergyLogEntity>> {
        return energyLogDao.getLast30LogsForUser(userId)
    }

    suspend fun addEnergyLog(
        user: UserEntity,
        date: String,
        weatherCondition: String,
        solarGeneration: Double,
        batteryLevel: Int,
        previousMeterReading: Double,
        currentMeterReading: Double,
        unitsConsumed: Double
    ): Result<Unit> = runCatching {
        val calculation = EnergyCalculator.calculateLog(
            solarGeneration = solarGeneration,
            unitsConsumed = unitsConsumed,
            electricityRate = user.electricityUnitRate
        )

        val log = EnergyLogEntity(
            userId = user.id,
            date = date,
            solarGeneration = solarGeneration,
            consumption = calculation.consumption,
            batteryLevel = batteryLevel.coerceIn(0, 100),
            weatherCondition = weatherCondition,
            previousMeterReading = previousMeterReading,
            currentMeterReading = currentMeterReading,
            solarUsage = calculation.solarUsage,
            gridUsage = calculation.gridUsage,
            netUsage = calculation.netUsage,
            exportedToGrid = calculation.exportedToGrid,
            savings = calculation.savings
        )
        energyLogDao.insertLog(log)
    }
}

