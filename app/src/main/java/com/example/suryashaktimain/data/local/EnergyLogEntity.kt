package com.example.suryashaktimain.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "energy_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class EnergyLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val date: String,
    val solarGeneration: Double,
    val consumption: Double,
    val batteryLevel: Int,
    val weatherCondition: String,
    val previousMeterReading: Double,
    val currentMeterReading: Double,
    val solarUsage: Double,
    val gridUsage: Double,
    val netUsage: Double,
    val exportedToGrid: Double,
    val savings: Double,
    val createdAt: Long = System.currentTimeMillis()
)

