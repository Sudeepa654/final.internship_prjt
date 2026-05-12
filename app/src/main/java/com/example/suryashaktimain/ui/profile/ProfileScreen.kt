package com.example.suryashaktimain.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.suryashaktimain.ui.components.AppBackground
import com.example.suryashaktimain.ui.components.LabeledValue
import com.example.suryashaktimain.ui.components.MetricCard
import com.example.suryashaktimain.ui.components.money
import com.example.suryashaktimain.ui.theme.SolarBlack
import com.example.suryashaktimain.ui.theme.SolarMuted
import com.example.suryashaktimain.ui.theme.SolarPanel
import com.example.suryashaktimain.ui.theme.SolarText
import com.example.suryashaktimain.ui.theme.SolarYellow
import com.example.suryashaktimain.viewmodel.AuthViewModel
import com.example.suryashaktimain.viewmodel.EnergyViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    energyViewModel: EnergyViewModel
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val report by energyViewModel.reportMetrics.collectAsStateWithLifecycle()
    val user = authState.currentUser ?: return

    AppBackground {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text("Profile", color = SolarText, style = MaterialTheme.typography.headlineSmall)
                    Text(user.email, color = SolarMuted)
                }
            }
            item {
                MetricCard(
                    title = "Lifetime demo savings",
                    value = report.totalMoneySaved.money(),
                    subtitle = "From local logs",
                    icon = Icons.Default.Bolt,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = SolarPanel)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Home Details", color = SolarText, style = MaterialTheme.typography.titleLarge)
                        LabeledValue("Name", user.name)
                        LabeledValue("Email", user.email)
                        LabeledValue("Home location", user.homeLocation)
                        LabeledValue("Solar capacity", "${user.solarPanelCapacityKw} kW")
                        LabeledValue("Electricity rate", user.electricityUnitRate.money() + " / unit")
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(
                        title = "Solar profile",
                        value = "${user.solarPanelCapacityKw} kW",
                        subtitle = user.homeLocation,
                        icon = Icons.Default.WbSunny,
                        modifier = Modifier.fillMaxWidth()
                    )
                    MetricCard(
                        title = "Account",
                        value = user.name,
                        subtitle = "Local Room user",
                        icon = Icons.Default.Person,
                        modifier = Modifier.fillMaxWidth()
                    )
                    MetricCard(
                        title = "Location",
                        value = user.homeLocation,
                        subtitle = "Home monitoring area",
                        icon = Icons.Default.Home,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item {
                Button(
                    onClick = {
                        energyViewModel.setActiveUser(null)
                        authViewModel.logout()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SolarYellow,
                        contentColor = SolarBlack
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Text(
                        text = "Logout",
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

