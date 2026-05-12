package com.example.suryashaktimain.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.suryashaktimain.ui.components.AppBackground
import com.example.suryashaktimain.ui.components.SolarTextField
import com.example.suryashaktimain.ui.theme.SolarBlack
import com.example.suryashaktimain.ui.theme.SolarMuted
import com.example.suryashaktimain.ui.theme.SolarPanel
import com.example.suryashaktimain.ui.theme.SolarText
import com.example.suryashaktimain.ui.theme.SolarYellow
import com.example.suryashaktimain.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onMessageShown: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.WbSunny,
                contentDescription = null,
                tint = SolarYellow,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "Surya-Shakti",
                color = SolarYellow,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Personal Solar Energy Monitor",
                color = SolarMuted,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            AuthCard {
                SolarTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    trailingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SolarMuted) }
                )
                SolarTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    visualTransformation = PasswordVisualTransformation(),
                    trailingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SolarMuted) }
                )
                MessageText(uiState)
                Button(
                    onClick = { onLogin(email, password) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SolarYellow,
                        contentColor = SolarBlack
                    )
                ) {
                    Text(if (uiState.isLoading) "Checking..." else "Login", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onForgotPasswordClick) {
                    Text("Forgot password?", color = SolarYellow)
                }
            }

            TextButton(onClick = onRegisterClick) {
                Text("Create a new account", color = SolarText)
            }
        }
    }
}

@Composable
internal fun AuthCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SolarPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
    }
}

@Composable
internal fun MessageText(uiState: AuthUiState) {
    val message = uiState.errorMessage ?: uiState.successMessage
    if (message != null) {
        Text(
            text = message,
            color = if (uiState.errorMessage != null) MaterialTheme.colorScheme.error else SolarYellow,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

