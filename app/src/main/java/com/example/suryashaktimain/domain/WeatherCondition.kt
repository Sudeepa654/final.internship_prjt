package com.example.suryashaktimain.domain

enum class WeatherCondition(val label: String) {
    SUNNY("Sunny"),
    CLOUDY("Cloudy"),
    RAINY("Rainy");

    companion object {
        fun fromLabel(label: String): WeatherCondition {
            return entries.firstOrNull { it.label.equals(label, ignoreCase = true) } ?: SUNNY
        }
    }
}

