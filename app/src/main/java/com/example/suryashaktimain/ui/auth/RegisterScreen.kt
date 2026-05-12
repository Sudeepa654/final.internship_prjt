package com.example.suryashaktimain.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.suryashaktimain.ui.components.AppBackground
import com.example.suryashaktimain.ui.components.SolarTextField
import com.example.suryashaktimain.ui.theme.SolarBlack
import com.example.suryashaktimain.ui.theme.SolarMuted
import com.example.suryashaktimain.ui.theme.SolarText
import com.example.suryashaktimain.ui.theme.SolarYellow
import com.example.suryashaktimain.viewmodel.AuthUiState

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onRegister: (String, String, String, String, String, String) -> Unit,
    onBackToLogin: () -> Unit,
    onMessageShown: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var solarCapacity by remember { mutableStateOf("3") }
    var unitRate by remember { mutableStateOf("8") }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        if (uiState.errorMessage != null || uiState.successMessage != null) {
            kotlinx.coroutines.delay(3500)
            onMessageShown()
        }
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.WbSunny, contentDescription = null, tint = SolarYellow)
            Text(
                text = "Create Account",
                color = SolarText,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Set up your home's solar profile",
                color = SolarMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(20.dp))

            AuthCard {
                SolarTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Name",
                    trailingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SolarMuted) }
                )
                SolarTextField(value = email, onValueChange = { email = it }, label = "Email")
                SolarTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    visualTransformation = PasswordVisualTransformation()
                )
                SolarTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = "Home location",
                    trailingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = SolarMuted) }
                )
                SolarTextField(
                    value = solarCapacity,
                    onValueChange = { solarCapacity = it },
                    label = "Solar panel capacity in kW"
                )
                SolarTextField(
                    value = unitRate,
                    onValueChange = { unitRate = it },
                    label = "Electricity unit rate"
                )
                MessageText(uiState)
                Button(
                    onClick = {
                        onRegister(name, email, password, location, solarCapacity, unitRate)
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SolarYellow,
                        contentColor = SolarBlack
                    )
                ) {
                    Text(if (uiState.isLoading) "Creating..." else "Create account", fontWeight = FontWeight.Bold)
                }
            }

            TextButton(onClick = onBackToLogin) {
                Text("Back to login", color = SolarYellow)
            }
        }
    }
}

